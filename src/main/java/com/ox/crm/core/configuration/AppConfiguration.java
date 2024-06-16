package com.ox.crm.core.configuration;

import static com.ox.crm.core.constants.AppConstants.Cache.CLIENTS_CACHE;
import static com.ox.crm.core.constants.AppConstants.Cache.CONTACTS_CACHE;
import static com.ox.crm.core.constants.AppConstants.Cache.TASKS_CACHE;

import java.time.Duration;
import java.util.Map;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfiguration {

  @Bean
  public RedisCacheConfiguration cacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofHours(1))
        .disableCachingNullValues()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
  }

  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    var ttl = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1));

    return builder -> builder
        .withInitialCacheConfigurations(
            Map.of(
                CLIENTS_CACHE, ttl,
                CONTACTS_CACHE, ttl,
                TASKS_CACHE, ttl
            )
        );
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
