package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * config class
 * Created by zhile on 2017/5/11 0011.
 */
@Configuration
@ImportResource(locations = "classpath:redis-context.xml")
public class ConfigClass {

}
