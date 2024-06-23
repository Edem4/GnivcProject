package com.service.driverservice.logist;

import com.sadikov.myLibrary.dto.FlightDTO;
import com.sadikov.myLibrary.dto.GetTaskDTO;
import feign.HeaderMap;
import feign.RequestLine;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LogistClient {
    @RequestLine("GET /tasks/driver")
    List<GetTaskDTO> getTasksDriver(@HeaderMap Map<String, String> headers);
    @RequestLine("POST /flight/create")
    void createFlight(FlightDTO createRouteDTO,
            @HeaderMap Map<String, String> headers);
}
