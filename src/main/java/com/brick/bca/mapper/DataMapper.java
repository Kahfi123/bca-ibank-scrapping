package com.brick.bca.mapper;

import com.brick.bca.model.UserRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DataMapper {
    public static Map<String,String> accountStatementRequestMapper(LocalDate startDate, LocalDate endDate){
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("value(D1)","0");
        requestParams.put("value(r1)","1");
        requestParams.put("value(startDt)", String.valueOf(startDate.getDayOfMonth()));
        requestParams.put("value(startMt)",String.valueOf(startDate.getMonthValue()));
        requestParams.put("value(startYr)",String.valueOf(startDate.getYear()));
        requestParams.put("value(endDt)",String.valueOf(endDate.getDayOfMonth()));
        requestParams.put("value(endMt)",String.valueOf(endDate.getMonthValue()));
        requestParams.put("value(endYr)",String.valueOf(endDate.getYear()));
        requestParams.put("value(fDt)","");
        requestParams.put("value(tDt)","");
        requestParams.put("value(submit1)","View Account Statement");
        return requestParams;
    }
    public static Map<String,String> loginRequestMapper(UserRequest userRequest){
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("value(actions)", "login");
        requestParams.put("value(user_id)", userRequest.getUserId());
        requestParams.put("value(user_ip)", "112.215.238.71");
        requestParams.put("value(browser_info)", "Mozilla/5.0+(Windows+NT+10.0;+Win64;+x64)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Chrome/88.0.4324.190+Safari/537.36");
        requestParams.put("value(mobile)", "false");
        requestParams.put("value(pswd)", userRequest.getPassword());
        requestParams.put("value(Submit)", "LOGIN");
        return requestParams;
    }
}
