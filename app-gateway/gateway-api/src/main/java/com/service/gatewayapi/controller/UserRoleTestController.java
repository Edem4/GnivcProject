package com.service.gatewayapi.controller;

import com.service.gatewayapi.config.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class UserRoleTestController {

    @GetMapping("/test")
    public Set<String> getRoles(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return  userDetails.getRoles();
    }
}
