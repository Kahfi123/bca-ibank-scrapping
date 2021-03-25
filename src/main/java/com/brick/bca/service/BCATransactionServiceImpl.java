package com.brick.bca.service;

import com.brick.bca.helper.CsvHelper;
import com.brick.bca.model.UserRequest;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.brick.bca.helper.StaticVariableHelper.COOKIE_KEY;
import static com.brick.bca.helper.URLHelper.*;
import static com.brick.bca.mapper.DataMapper.accountStatementRequestMapper;
import static com.brick.bca.mapper.DataMapper.loginRequestMapper;

@Service
public class BCATransactionServiceImpl implements TransactionService {
    @Autowired
    private JedisPool jedisPool;
    @Value("${spring.redis.password}")
    private String password;
    private Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        //jedis.auth(password);
        return jedis;
    }
    Logger log = LoggerFactory.getLogger(BCATransactionServiceImpl.class);
    @Override
    public void getTransactions(UserRequest userRequest) {
        log.info("Get last 31 days transactions from BCA");
        Map<String, String> cookies = login(userRequest);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(31); //BCA Only allow last 31 days transactions
        Document transactionDocument = getAccountStatement(cookies, startDate,endDate);
        //CsvHelper.saveDataToCSV(transactionDocument);
    }

    private Document getAccountStatement(Map<String, String> cookies, LocalDate startDate, LocalDate endDate) {
        Document transactionDocument = null;
        try {
            Map<String, String> requestFormParams = accountStatementRequestMapper(startDate,endDate);
            Connection.Response accountStatementPageResponse = Jsoup.connect(BCA_BASE_URL+BCA_ACCOUNT_STATEMENT_PATH)
                    .method(Connection.Method.POST)
                    .cookies(cookies)
                    .userAgent(USER_AGENT)
                    .timeout(10 * 1000)
                    .data(requestFormParams)
                    .followRedirects(true)
                    .execute();
            transactionDocument = accountStatementPageResponse.parse();
            log.info("getAccountStatement method successfully get transactionDocument");
        } catch (IOException e) {
            log.error("getAccountStatement method error occured: {}",e.toString());
        }
        return transactionDocument;

    }

    private Map<String, String> login(UserRequest userRequest) {
        log.info("login with user_id: {}",userRequest.getUserId());
        Jedis jedis = getJedis();
        String userCookieKey = userRequest.getUserId()+"-"+COOKIE_KEY;
        log.info("checking in redis if session with user_id: {} already exists",userRequest.getUserId());
        if(jedis.exists(userCookieKey)){
            log.info("session with user_id: {} already exists in redis, returning session from redis",userRequest.getUserId());
            return getJedis().hgetAll(userCookieKey);
        }
        log.info("session with user_id: {} doesn't exist in redis, proceed to log to IBANK BCA",userRequest.getUserId());
        Map<String, String> requestFormParams = loginRequestMapper(userRequest);
        String strActionURL = BCA_BASE_URL+BCA_AUTHENTICATION_PATH;
        Connection.Response responsePostLogin = null;
        try {
            responsePostLogin = Jsoup.connect(strActionURL)
                    .method(Connection.Method.POST)
                    .referrer(BCA_BASE_URL+BCA_LOGIN_PATH)
                    .userAgent(USER_AGENT)
                    .timeout(10 * 1000)
                    .data(requestFormParams)
                    .followRedirects(true)
                    .execute();
            Document document = responsePostLogin.parse();
            if(!document.select("input").isEmpty()){
                log.info("Wrong user_id/password or");
                throw new ResponseStatusException(
                        HttpStatus.OK, "user_id not found");
            }
            jedis.hset(userCookieKey,responsePostLogin.cookies());
            jedis.expire(userCookieKey,300);
            log.info("Saving cookies to key {}",userCookieKey);
        } catch (IOException e) {
            log.error(e.toString());
        }
        return responsePostLogin.cookies();
    }
}
