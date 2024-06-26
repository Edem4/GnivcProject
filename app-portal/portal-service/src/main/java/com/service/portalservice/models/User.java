package com.service.portalservice.models;


import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class User {

    private String userId;
    private String username;
    private String email;

    private Set<String> roles = new HashSet<>();

    public void setRoles(List<String> list){
        this.roles.addAll(list);
    }

    public boolean checkAuthority(String authority){
        for(String role: roles){
            if(role.equals(authority)) return true;
        }
        return false;
    }
}