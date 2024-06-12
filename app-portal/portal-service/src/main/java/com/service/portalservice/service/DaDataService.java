package com.service.portalservice.service;

import com.service.portalservice.dto.DaDataDto;
import com.service.portalservice.models.Company;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class DaDataService {
    private final String url = "http://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/party";
    @Value("${dadata.token}")
    private String tokenValue;


    public Company getInfo(DaDataDto daDataDto){
        RestTemplate restTemplate = new RestTemplate();
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.add("Authorization", tokenValue);
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Collections.singletonList(interceptor));
        ResponseEntity<Company> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(daDataDto),
                Company.class);
        return response.getBody();
    }
}
