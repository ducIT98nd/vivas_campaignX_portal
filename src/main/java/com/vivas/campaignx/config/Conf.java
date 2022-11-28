package com.vivas.campaignx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Conf {
    public static String REDIS_HOST;
    public static int REDIS_PORT = 0;
    public static String REDIS_PASSWORD;
    public static int REDIS_DB = 0;
    public static int REDIS_POOL_MAX_ACTIVE = 50;
    public static int REDIS_POOL_MAX_WAIT =100000;
    public static int REDIS_POOL_MAX_IDLE = 10;
    public static int REDIS_POOL_MIN_IDLE = 0;

    public static String PREFIX_KEY_NOTIFY_EXPORT_MSISDN = "cx:notify:";

    //config redis limit
    @Value("${redis.host}")
    public void setRedisHost(String value) {
        REDIS_HOST = value;
    }
    @Value("${redis.port}")
    public void setRedisPort(Integer value) {
        REDIS_PORT = value;
    }
    @Value("${redis.password}")
    public void setRedisPassword(String value) {
        REDIS_PASSWORD = value;
    }
    @Value("${redis.db}")
    public void setRedisDb(Integer value) {
        REDIS_DB = value;
    }
    @Value("${redis.pool.max-active}")
    public void setRedisPoolMaxActive(Integer value) {
        REDIS_POOL_MAX_ACTIVE = value;
    }
    @Value("${redis.pool.max-wait}")
    public void setRedisPoolMaxWait(Integer value) {
        REDIS_POOL_MAX_WAIT = value;
    }
    @Value("${redis.pool.max-idle}")
    public void setRedisPoolMaxIdle(Integer value) {
        REDIS_POOL_MAX_IDLE = value;
    }
    @Value("${redis.pool.min-idle}")
    public void setRedisPoolMinIdle(Integer value) {
        REDIS_POOL_MIN_IDLE = value;
    }
}
