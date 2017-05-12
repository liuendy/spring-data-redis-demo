package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void contextLoads() {

		/*Todo： build spring data redis
		*
		* According to:
		* 	http://www.cnblogs.com/edwinchen/p/3816938.html
		* */

		String key = "spring-data-redis:" + Instant.now();
		String value = "val:" + LocalDateTime.now();
		ValueOperations valueops = redisTemplate.opsForValue();
		valueops.set(key, value, 10000, TimeUnit.MILLISECONDS);

		System.out.println("redis set ok.");

		ValueOperations valueopsGet = redisTemplate.opsForValue();
		System.out.println("return:" + valueopsGet.get(key));

		/*System.out.println("heloo");*/
	}

}
