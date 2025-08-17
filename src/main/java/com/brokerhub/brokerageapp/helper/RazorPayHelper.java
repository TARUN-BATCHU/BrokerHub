package com.brokerhub.brokerageapp.helper;

import com.brokerhub.brokerageapp.dto.BankDetailsDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RazorPayHelper {

    private final RestTemplate restTemplate = new RestTemplate();

    public BankDetailsDTO fetchBankDetails(String ifscCode) {
        BankDetailsDTO dto = new BankDetailsDTO();
        String url = "https://ifsc.razorpay.com/" + ifscCode;

        try {
            String jsonString = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(jsonString);

            dto.setIfscCode(json.path("IFSC").asText(null));
            dto.setBankName(json.path("BANK").asText(null));
            dto.setBranch(json.path("BRANCH").asText(null));
            dto.setBankContact(json.path("CONTACT").asText(null));
            dto.setBankAddress(json.path("ADDRESS").asText(null));
            dto.setBankCode(json.path("BANKCODE").asText(null));
            dto.setMICR(json.path("MICR").asText(null));
            dto.setRTGS(json.path("RTGS").asBoolean(false));
            dto.setIMPS(json.path("IMPS").asBoolean(false));
            dto.setUPI(json.path("UPI").asBoolean(false));
            dto.setNEFT(json.path("NEFT").asBoolean(false));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
}
