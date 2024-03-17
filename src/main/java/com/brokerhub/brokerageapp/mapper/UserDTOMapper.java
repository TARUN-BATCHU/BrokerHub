package com.brokerhub.brokerageapp.mapper;

import com.brokerhub.brokerageapp.dto.UserDTO;
import com.brokerhub.brokerageapp.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper {

    @Autowired
    private ModelMapper modelMapper;

    public UserDTO convertUsertoUserDTO(User user){
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    public User convertUserDTOtoUser(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        return user;
    }
}
