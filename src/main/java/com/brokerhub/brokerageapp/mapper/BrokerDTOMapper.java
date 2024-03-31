package com.brokerhub.brokerageapp.mapper;

import com.brokerhub.brokerageapp.dto.BrokerDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BrokerDTOMapper {
    @Autowired
    private ModelMapper modelMapper;

    public BrokerDTO convertBrokertoBrokerDTO(Broker broker){
        BrokerDTO brokerDTO = modelMapper.map(broker, BrokerDTO.class);
        return brokerDTO;
    }

    public Broker convertBrokerDTOtoBroker(BrokerDTO brokerDTO){
        Broker broker = modelMapper.map(brokerDTO, Broker.class);
        return broker;
    }
}
