package com.service.portalservice.repository;

import com.service.portalservice.models.UserDataBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDataBase, String> {
    UserDataBase findByUsername(String username);
}
