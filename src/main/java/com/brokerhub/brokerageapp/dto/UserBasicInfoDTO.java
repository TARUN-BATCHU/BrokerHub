package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBasicInfoDTO implements Serializable {
    
    private Long userId;
    private String firmName;
    private String userType;
    private String gstNumber;
    private String ownerName;
    
    // Constructor for Object[] mapping from repository queries
    public UserBasicInfoDTO(Object[] data) {
        if (data.length >= 5) {
            this.userId = (Long) data[0];
            this.firmName = (String) data[1];
            this.userType = (String) data[2];
            this.gstNumber = (String) data[3];
            this.ownerName = (String) data[4];
        }
    }
    
    // Constructor for userId and firmName only
    public UserBasicInfoDTO(Long userId, String firmName) {
        this.userId = userId;
        this.firmName = firmName;
    }
}
