package com.example.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * Created by zhile on 2017/5/11 0011.
 */
@Component
public class RedisConnector {

    @Autowired
    private RedisTemplate redisTemplate;

    public void add(String key, Object value) {
        ValueOperations valueops = redisTemplate.opsForValue();
        valueops.set(key, value);
    }

    public Object get(String key) {
        ValueOperations valueopsGet = redisTemplate.opsForValue();
        return valueopsGet.get(key);
    }
}
