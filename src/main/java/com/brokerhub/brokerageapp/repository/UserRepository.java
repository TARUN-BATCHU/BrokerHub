package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
