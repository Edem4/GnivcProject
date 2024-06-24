package com.service.gatewayapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.gatewayapi.config.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



@Component
@RequiredArgsConstructor
public class UserDataFilter implements GlobalFilter {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext().filter(context -> context.getAuthentication() != null)
                .flatMap(context -> {
                        UserDetailsImpl user = (UserDetailsImpl) context.getAuthentication().getPrincipal();
                        ServerHttpRequest request = getMutateRequest(exchange,user);
                        return chain.filter(exchange.mutate().request(request).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private ServerHttpRequest getMutateRequest(ServerWebExchange exchange, UserDetailsImpl user) {
        try {
            return exchange.getRequest().mutate()
                    .header("userName", user.getUsername())
                    .header("userId", user.getUserId())
                    .header("userEmail", user.getEmail())
                    .header("roles", objectMapper.writeValueAsString(user.getRoles()))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

