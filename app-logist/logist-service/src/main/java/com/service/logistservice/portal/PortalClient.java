package com.service.logistservice.portal;

import com.service.logistservice.dto.CarDTO;
import com.service.logistservice.dto.CompanyDTO;
import com.service.logistservice.dto.DriverDTO;
import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;

import java.util.Map;

public interface PortalClient {
    @RequestLine("POST /company/driver/get/{driver_name}")
    DriverDTO findDriverByName(@Param("driver_name") String driverName,
                             @HeaderMap Map<String,String> headers,
                               CompanyDTO companyDTO);

    @RequestLine("POST /cars/get/{number}")
    CarDTO findCarByNumber(@Param("number") String number,
                           @HeaderMap Map<String,String> headers);
}
