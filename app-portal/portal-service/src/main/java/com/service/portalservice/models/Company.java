package com.service.portalservice.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "company")
public class Company {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column(unique = true)
    private String inn;
    @Column
    private String address;
    @Column
    private String kpp;
    @Column
    private String ogrn;
    @Column
    private List<String> workersId = new ArrayList<>();
    @Column
    private List<String> roleCompany = new ArrayList<>();
    @Column
    private int driverCount = 0;
    @Column
    private int logistCount = 0;

    public void setRoleCompany(List<RoleRepresentation> role){
        for(RoleRepresentation roleStr: role){
            roleCompany.add(roleStr.getName());
        }
    }
    public void setWorkersId(String userId){
            workersId.add(userId);
    }
    public void setLogistCount() {
        logistCount++;
    }
    public void setDriverCount() {
        driverCount++;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("suggestions")
    private void unpackNested(List<Object> suggestions) {
        try {
            Map<String, Object> suggestion = (Map<String, Object>) suggestions.get(0);
            this.name = (String) suggestion.get("value");
            Map<String, Object> data = (Map<String, Object>) suggestion.get("data");
            this.inn = (String) data.get("inn");
            this.ogrn = (String) data.get("ogrn");
            this.kpp = (String) data.get("kpp");
            Map<String, Object> address = (Map<String, Object>) data.get("address");
            this.address = (String) address.get("value");
        } catch (Exception ignored) {
        }
    }
}