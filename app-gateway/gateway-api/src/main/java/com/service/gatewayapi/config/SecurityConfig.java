package com.service.gatewayapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig{

    private final AccountAuthenticationProvider provider;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http){
        return http.httpBasic(Customizer.withDefaults())
                .headers(headerSpec ->
                        headerSpec.contentSecurityPolicy(contentSecurityPolicySpec ->
                                contentSecurityPolicySpec.policyDirectives("upgrade-insecure-request")))
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(requests -> {
                    requests.pathMatchers("/openid-connect/**").permitAll();
                    requests.pathMatchers("/portal/users/create").permitAll();
                    requests.pathMatchers("/portal/company/create").hasAnyAuthority("REGISTRATOR");
                    requests.pathMatchers("/portal/company/add/worker").hasAnyAuthority("ADMIN","LOGIST");
                    requests.pathMatchers("/portal/company/get").hasAnyAuthority("ADMIN");
                    requests.pathMatchers("/portal/company/get/all").hasAnyAuthority("ADMIN");
                    requests.pathMatchers("/portal/users/reset/password").permitAll();
                    requests.pathMatchers("/portal/company/driver/**").hasAnyAuthority("ADMIN","LOGIST");
                    requests.pathMatchers("/portal/users/change").hasAnyAuthority("ADMIN","REGISTRATOR","LOGIST","DRIVER");
                    requests.pathMatchers("/portal/cars/**").hasAnyAuthority("ADMIN","LOGIST");
                    requests.pathMatchers("/logist/**").hasAnyAuthority("LOGIST");
                    requests.pathMatchers("/dwh").permitAll();
                })
                .oauth2ResourceServer(ServerConfig ->
                        ServerConfig.authenticationManagerResolver(context -> Mono.just(provider))
                ).build();
    }

}
