package com.service.gatewayapi.config;


import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.*;

@Component
public class AccountAuthenticationProvider implements ReactiveAuthenticationManager {
    private final ReactiveJwtDecoder jwtDecoder;

    public AccountAuthenticationProvider(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        BearerTokenAuthenticationToken authenticationToken = (BearerTokenAuthenticationToken) authentication;

        return getJwt(authenticationToken).map(jwt -> {
                    return new UserDetailsImpl(jwt.getClaimAsString("preferred_username"),
                            jwt.getClaimAsString("sub"),
                            jwt.getClaimAsString("email"),
                            getRoles(jwt));
                })
                .map(userDetails -> (Authentication) new UsernamePasswordAuthenticationToken(
                        userDetails,
                        authenticationToken.getCredentials(),
                        userDetails.getAuthorities()
                ));
    }

    private Set<String> getRoles(Jwt jwt) {

        Map<String, Object> claim = Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
                .orElse(new HashMap<>());

        List<String> rolesList = (List<String>) Optional.ofNullable(claim.get("roles")).orElse(new ArrayList<>());
        return new HashSet<>(rolesList);
    }

    private Mono<Jwt> getJwt(BearerTokenAuthenticationToken authenticationToken) {
        try {
            return this.jwtDecoder.decode(authenticationToken.getToken());
        }catch (JwtException err){
            throw new AuthenticationServiceException(err.getMessage(),err);
        }
    }
}
