package com.brick.bca.controller;

import com.brick.bca.model.BaseResponse;
import com.brick.bca.model.UserRequest;
import com.brick.bca.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ApiController {
    Logger log = LoggerFactory.getLogger(ApiController.class);
    @Autowired
    TransactionService transactionService;
    @PostMapping("transactions")
    public BaseResponse getListTransaction(@RequestBody UserRequest userRequest){
        log.info("getListTransaction request: {}",userRequest.getUserId());
        try{
            transactionService.getTransactions(userRequest);
        }catch (ResponseStatusException e){
            return new BaseResponse("Error", "Wrong user_id or password");
        }

        return new BaseResponse("Success","Successfully saved transactions data to CSV");
    }
}
