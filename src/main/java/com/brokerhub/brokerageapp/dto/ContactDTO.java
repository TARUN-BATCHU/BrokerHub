package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    private Long id;
    private String firmName;
    private String userName;
    private String gstNumber;
    private String additionalInfo;
    private Long brokerId;
    private List<Long> sectionIds;
    private List<String> sectionNames;
    private List<ContactPhoneDTO> phoneNumbers;
    private List<ContactAddressDTO> addresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}