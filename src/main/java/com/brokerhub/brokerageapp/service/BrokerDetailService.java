package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BrokerDetailService implements UserDetailsService {

    @Autowired
    BrokerRepository brokerRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<Broker> broker = brokerRepository.findByUserName(userName);
        if(broker.isPresent()){
            var brokerObj = broker.get();
            return User.builder()
                    .username(brokerObj.getUserName())
                    .password(brokerObj.getPassword())
                    .build();
        }
        else{
            throw new UsernameNotFoundException(userName);
        }
    }

//    private String[] getRoles(User user){
//        if(user.getRole() == null){
//            return STRING[]{"USER"};
//        }
//        return user.getRole().split(",");
//    }
}
