package org.example.tournamentapp.config;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

//    @Bean
//    public CacheManager cacheManager() {
//        CaffeineCache tasksCache = new CaffeineCache("cache",
//                Caffeine.newBuilder()
//                        .expireAfterWrite(10, TimeUnit.MINUTES)
//                        .maximumSize(1000)
//                        .build());
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//        cacheManager.setCaches(List.of(tasksCache));
//        return cacheManager;
    }
