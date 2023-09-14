package com.loan_system.lls.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Value("${caching.ttl.minute.expire}")
    private int ttlMints;

    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;

    private Map<String, Object> tempResponse = new HashMap<>();

    public void storeOtpWithExpiry(String mobile, Object otp) {
        redisTemplate.opsForValue().set(mobile, otp);
        logger.debug("In storeOtpWithExpiry() -> RedisService.java ");
        // redisTemplate.expire(mobile, Duration.ofMinutes(ttlMints));
        redisTemplate.expire(mobile, Duration.ofSeconds(ttlMints));

        tempResponse.put("otp", otp);
    }

    public String getOtpByMobile(String mobile) {
        String otp = null;
        try{
            otp = (String) redisTemplate.opsForValue().get(mobile);
        } catch (Exception e){
        logger.debug("In getOtpByMobile() -> RedisService.java : Exception{}",e);
        }
        logger.debug("In getOtpByMobile() -> RedisService.java : otp{}",otp);
        return otp;
    }

}
