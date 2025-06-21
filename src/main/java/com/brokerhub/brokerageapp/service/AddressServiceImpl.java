package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.BrokersAddress;
import com.brokerhub.brokerageapp.repository.AddressRepository;
import com.brokerhub.brokerageapp.repository.BrokersAddressRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    BrokersAddressRepository brokersAddressRepository;

    @Autowired
    TenantContextService tenantContextService;

    private static final String POSTOFFICE_API_URL = "https://api.postalpincode.in/pincode/";

    public boolean isCityExists(String city) {
        if(null != city) {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            boolean CityExists = addressRepository.existsByBrokerBrokerIdAndCity(currentBrokerId, city);
            return CityExists;
        }
        return false;
    }

    public Address findAddressByPincode(String pincode) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        Address address = addressRepository.findByBrokerBrokerIdAndPincode(currentBrokerId, pincode);
        return address;
    }

    public BrokersAddress findBrokersAddressByPincode(String pincode) throws IOException, InterruptedException {
        BrokersAddress brokersAddress = brokersAddressRepository.findByPincode(pincode);
        if(null == brokersAddress){
            try {
                brokersAddress = fetchAddressfromPostOffice(pincode);
            }catch(Exception e){
                brokersAddress = null;
            }
            //setting default address if not found in post office also
            if(null == brokersAddress){
                return brokersAddressRepository.findByPincode("000000");
            }
            brokersAddress.setPincode(pincode);
            createBrokersAddress(brokersAddress);
            brokersAddress = brokersAddressRepository.findByPincode(pincode);
        }
        return brokersAddress;
    }

    public BrokersAddress fetchAddressfromPostOffice(String pincode) throws IOException, InterruptedException{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(POSTOFFICE_API_URL + pincode))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            if (root.isArray() && root.get(0).get("Status").asText().equalsIgnoreCase("Success")) {
                JsonNode postOffice = root.get(0).get("PostOffice").get(0);
                String area = postOffice.get("Name").asText();
                String state =  postOffice.get("State").asText();
                String city = postOffice.get("Region").asText();
                BrokersAddress brokersAddress = new BrokersAddress();
                brokersAddress.setState(state);
                brokersAddress.setCity(city);
                brokersAddress.setArea(area);
                return brokersAddress;
            }

            return null;
    }

    public BrokersAddress createBrokersAddress(BrokersAddress brokersAddress){
        return brokersAddressRepository.save(brokersAddress);
    }

    @Override
    public Address saveAddress(Address address) {
        // Set the broker for multi-tenant isolation if not already set
        if (address.getBroker() == null) {
            Broker currentBroker = tenantContextService.getCurrentBroker();
            address.setBroker(currentBroker);
        }
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAllAddresses() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        List<Address> addresses = addressRepository.findByBrokerBrokerId(currentBrokerId);
        if(addresses.size()>0){
            return addresses;
        }
        return null;
    }

    @Override
    public ResponseEntity createAddress(Address address) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        Broker currentBroker = tenantContextService.getCurrentBroker();

        // Check if address already exists for this broker
        Optional<Address> existingAddress = addressRepository.findByBrokerBrokerIdAndCityAndAreaAndPincode(
            currentBrokerId, address.getCity(), address.getArea(), address.getPincode());
        if(existingAddress.isPresent()){
            return ResponseEntity.status(409).body("Address already exists for this broker");
        }

        // Set the broker for multi-tenant isolation
        address.setBroker(currentBroker);

        Address address1 = addressRepository.save(address);
        return ResponseEntity.ok().body("Address created successfully with id: "+address1.getAddressId());
    }

    @Override
    public ResponseEntity updateAddress(Address address) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();

        // Check if address exists and belongs to current broker
        Optional<Address> existingAddress = addressRepository.findByBrokerBrokerIdAndAddressId(currentBrokerId, address.getAddressId());
        if(!existingAddress.isPresent()){
            return ResponseEntity.status(404).body("Address does not exist or does not belong to current broker");
        }

        // Ensure the broker is set correctly
        address.setBroker(tenantContextService.getCurrentBroker());

        addressRepository.save(address);
        return ResponseEntity.ok().body("Address updated successfully");
    }
}
