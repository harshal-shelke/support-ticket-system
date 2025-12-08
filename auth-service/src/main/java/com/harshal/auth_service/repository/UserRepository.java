package com.harshal.auth_service.repository;

import com.harshal.auth_service.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByEmail(String email);
//    boolean existByEmail(String email);
}
