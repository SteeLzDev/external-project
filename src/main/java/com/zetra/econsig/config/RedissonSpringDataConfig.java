package com.zetra.econsig.config;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Title: RedissonSpringDataConfig</p>
 * <p>Description: Configuração do cliente Redisson para conexão ao Redis.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
@Configuration
@ConditionalOnProperty(name = "spring.data.redis.type", havingValue = "redisson")
public class RedissonSpringDataConfig {

    @Value("${spring.data.redis.timeout:10}")
    private Integer timeout;

    @Value("${spring.data.redis.database:0}")
    private Integer database;

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private Integer port;

    @Value("${spring.data.redis.redisson.connection.minimum.idle.size:16}")
    private Integer connectionMinimumIdleSize;
    
    @Value("${spring.data.redis.redisson.connection.pool.size:32}")
    private Integer connectionPoolSize;

    @Value("${spring.data.redis.redisson.netty.threads:32}")
    private Integer nettyThreads;

    @Bean
    RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
        final Config config = new Config();
        config.useSingleServer()
              .setAddress(String.format("redis://%s:%s", host, port))
              .setTimeout(timeout * 1000)
              .setDatabase(database)
              .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
              .setConnectionPoolSize(connectionPoolSize);
        config.setNettyThreads(nettyThreads);
        return Redisson.create(config);
    }
}