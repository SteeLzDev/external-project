package com.zetra.econsig.config;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDestroyedEvent;

import io.lettuce.core.ClientOptions;

/**
 * <p>Title: RedisConfig</p>
 * <p>Description: Configuração do cliente para conexão ao Redis.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
@Configuration
@EnableSpringHttpSession
@SuppressWarnings("java:S125")
public class RedisConfig {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RedisConfig.class);

    @Value("${spring.data.redis.type:lettuce}")
    private String type;

    @Value("${spring.data.redis.timeout:10}")
    private Integer timeout;

    @Value("${spring.data.redis.database:0}")
    private Integer database;

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private Integer port;

    @SuppressWarnings("java:S3305")
    @Autowired(required = false)
    RedissonConnectionFactory redissonConnectionFactory;

    @Bean
    @ConditionalOnProperty(prefix = "spring", name = "session.store-type", havingValue = "none")
    SafeSessionRepository<?> mapSessionRepository(ApplicationEventPublisher publisher) {
        return new SafeSessionRepository<>(new MapSessionRepository(new ConcurrentHashMap<>()), publisher);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring", name = "session.store-type", havingValue = "redis")
    SafeSessionRepository<?> redisSessionRepository(ApplicationEventPublisher publisher) {
        return new SafeSessionRepository<>(new RedisSessionRepository(redisTemplate(redisConnectionFactory())), publisher);
    }

    /**
     * Define a Factory de conexão ao Redis, considerando a configuração do tipo de cliente usar.
     * Valores disopníveis: lettuce (default), jedis e redisson
     * @return uma RedisConnectionFactory do tipo especificado na propriedade spring.data.redis.type
     */
    @Primary
    @Bean
    @ConditionalOnProperty(prefix = "spring", name = "session.store-type", havingValue = "redis")
    RedisConnectionFactory redisConnectionFactory() {
        if ("redisson".equals(type)) {
            return redissonConnectionFactory;

        } else if ("jedis".equals(type)) {
            final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
            return new JedisConnectionFactory(config);

        } else {
            final LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder().commandTimeout(Duration.ofSeconds(timeout)).clientOptions(ClientOptions.builder().build()).build();
            final RedisStandaloneConfiguration clusterConfiguration = new RedisStandaloneConfiguration(host, port);
            return new LettuceConnectionFactory(clusterConfiguration, lettuceClientConfiguration);
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring", name = "session.store-type", havingValue = "redis")
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    static class SafeSessionRepository<T extends Session> implements SessionRepository<T> {
        private final SessionRepository<T> delegate;
        private final ApplicationEventPublisher publisher;

        SafeSessionRepository(SessionRepository<T> delegate, ApplicationEventPublisher publisher) {
            this.delegate = delegate;
            this.publisher = publisher;
        }

        @Override
        public T createSession() {
            T session = delegate.createSession();
            publisher.publishEvent(new SessionCreatedEvent(this, session));
            return session;
        }

        @Override
        public void save(T session) {
            try {
                delegate.save(session);
            } catch (IllegalStateException ex) {
                LOG.warn(ex.getMessage());
            }
        }

        @Override
        public T findById(String id) {
            try {
                return delegate.findById(id);
            } catch (IllegalStateException ex) {
                delegate.deleteById(id);
                return null;
            }
        }

        @Override
        public void deleteById(String id) {
            T session = findById(id);
            delegate.deleteById(id);

            if (session != null) {
                publisher.publishEvent(new SessionDestroyedEvent(this, session));
            }
        }
    }
}