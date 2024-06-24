package com.service.gatewayapi.config;


import com.service.gatewayapi.config.interfaces.UserDataContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDataContainer {
    private final String username;
    private final String userId;
    private final String email;
    private final Set<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
               return roles.stream()
                       .map(SimpleGrantedAuthority::new)
                       .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public String getUserId() {
        return userId;
    }
    public String getEmail(){
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
