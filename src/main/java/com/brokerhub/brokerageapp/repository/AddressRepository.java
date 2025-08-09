package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // Multi-tenant aware queries - all include broker filtering
    @Query("SELECT a.addressId FROM Address a WHERE a.broker.brokerId = :brokerId AND a.city = :city AND a.area = :area")
    Long findAddressIdByBrokerIdAndCityAndArea(@Param("brokerId") Long brokerId, @Param("city") String city, @Param("area") String area);

    Address findByBrokerBrokerIdAndCityAndArea(Long brokerId, String city, String area);

    Address findByBrokerBrokerIdAndPincode(Long brokerId, String pincode);

    boolean existsByBrokerBrokerIdAndCity(Long brokerId, String city);

    Optional<Address> findByBrokerBrokerIdAndCityAndAreaAndPincode(Long brokerId, String city, String area, String pincode);

    Optional<Address> findByBrokerBrokerIdAndAddressId(Long brokerId, Long addressId);

    List<Address> findByBrokerBrokerId(Long brokerId);

    @Query("SELECT a.city, a.addressId FROM Address a WHERE a.broker.brokerId = :brokerId")
    List<Object[]> findCitiesWithAddressIdByBrokerId(@Param("brokerId") Long brokerId);

    // Legacy methods (deprecated - use broker-aware versions)
    @Deprecated
    Long findAddressIdByCityAndArea(String city, String area);

    @Deprecated
    Address findByCityAndArea(String city, String area);

    @Deprecated
    Address findByPincode(String pincode);

    @Deprecated
    boolean existsByCity(String city);

    @Deprecated
    Optional<Address> findByCityAndAreaAndPincode(String city, String area, String pincode);

    @Deprecated
    Optional<Address> findByAddressId(Long addressId);
}
