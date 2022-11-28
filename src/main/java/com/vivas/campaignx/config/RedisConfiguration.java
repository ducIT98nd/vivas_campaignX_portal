package com.vivas.campaignx.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@DependsOn("conf")
public class RedisConfiguration {

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinIdle(Conf.REDIS_POOL_MIN_IDLE);
        poolConfig.setMaxIdle(Conf.REDIS_POOL_MAX_IDLE);
        poolConfig.setMaxTotal(Conf.REDIS_POOL_MAX_ACTIVE);
        poolConfig.setMaxWaitMillis(Conf.REDIS_POOL_MAX_WAIT);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(10);
        poolConfig.setTimeBetweenEvictionRunsMillis(60000);
        
        JedisClientConfiguration clientconfig = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(poolConfig)
                .build();
        //System.out.println("host=" + Conf.REDIS_HOST + ", port=" + Conf.REDIS_PORT + ", db=" + Conf.REDIS_DB);
        RedisStandaloneConfiguration redisconfig = new RedisStandaloneConfiguration(Conf.REDIS_HOST, Conf.REDIS_PORT);
        redisconfig.setDatabase(Conf.REDIS_DB);
        redisconfig.setPassword(Conf.REDIS_PASSWORD);
        return new JedisConnectionFactory(redisconfig, clientconfig);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }
}
