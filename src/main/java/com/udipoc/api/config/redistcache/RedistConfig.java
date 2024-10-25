package com.udipoc.api.config.redistcache;

import com.udipoc.api.entity.ServiceToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.Duration;

@Configuration
@EnableRedisRepositories
public class RedistConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private Integer redisPort;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(redisPort);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, ServiceToken> redisTemplate() {
        RedisTemplate<String, ServiceToken> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.afterPropertiesSet();
        template.expire("serviceToken", Duration.ofHours(2));
        return template;
    }
}
