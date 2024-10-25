package com.udipoc.api.config.redistcache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udipoc.api.entity.ServiceToken;
import com.udipoc.api.util.DateUtils;
import com.udipoc.api.util.enums.ServiceName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RedisServiceTokenRepository {

    private static final int TIME_HOUR = 2;
    private final HashOperations hashOperations;
    private final RedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisServiceTokenRepository(RedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
        this.objectMapper = objectMapper;
    }


    public Map<String, List<ServiceToken>> multiGetServiceToken(List<String> serviceTokenIds) {
        Map<String, List<ServiceToken>> serviceTokenMap = new HashMap<>();
        List<Object> serviceTokens = hashOperations.multiGet("serviceToken", serviceTokenIds);
        for (int i = 0; i < serviceTokenIds.size(); i++) {
            serviceTokenMap.put(serviceTokenIds.get(i), (List<ServiceToken>) serviceTokens.get(i));
        }
        return serviceTokenMap;
    }

    public void save(ServiceToken serviceToken) {
        hashOperations.put("serviceToken", serviceToken.getServiceName(), serviceToken);
    }

    public List findAll() {
        return hashOperations.values("serviceToken");
    }

    public ServiceToken findByServiceName(ServiceName serviceName) {
        Object serviceTokenObj = hashOperations.get("serviceToken", serviceName);
        if (serviceTokenObj != null) {
            ServiceToken serviceTokenConvertValue = objectMapper.convertValue(serviceTokenObj, ServiceToken.class);
            Boolean expireStatus = DateUtils.validateServiceTokenExpire(new Date(), serviceTokenConvertValue.getGenerateTimestamp(), TIME_HOUR);
            if (expireStatus) {
                delete(serviceTokenConvertValue.getServiceName());
                return null;
            } else {
                return serviceTokenConvertValue;
            }
        } else {
            return null;
        }
    }

    public void update(ServiceToken serviceToken) {
        save(serviceToken);
    }

    public void delete(ServiceName serviceName) {
        hashOperations.delete("serviceToken", serviceName);
    }

}
