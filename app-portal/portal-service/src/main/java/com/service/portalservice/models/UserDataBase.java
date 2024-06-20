package com.service.portalservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class UserDataBase {
    @Id
    @Column(name = "userId")
    private String userId;
    @Column(unique = true)
    private String username;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column(unique = true)
    private String userEmail;
    @Column
    private List<String> rolesToCompany = new ArrayList<>();

    public void setRolesToCompany(String role){
        this.rolesToCompany.add(role);
    }


}
