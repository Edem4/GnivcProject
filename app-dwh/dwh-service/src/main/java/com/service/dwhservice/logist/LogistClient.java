package com.service.dwhservice.logist;

import com.sadikov.myLibrary.dto.GetStatisticDTO;
import com.sadikov.myLibrary.dto.StatisticDTO;
import feign.HeaderMap;
import feign.RequestLine;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface LogistClient {
    @RequestLine("POST /stat")
    StatisticDTO getCompanyStatistic(GetStatisticDTO getStatisticDTO,
                                @HeaderMap Map<String, String> headers);

}
