package com.example.springredislettuce.lettuce.contiguge;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@ConditionalOnClass({ RedisOperations.class , MessageListener.class })
@AutoConfigureAfter({ JacksonAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class })
public class RedisAutoConfiguration {


    @Configuration
    @ConditionalOnExpression("!'${spring.redis.host:}'.isEmpty()")
    public static class RedisStandAloneAutoConfiguration {
        @Bean
        public RedisMessageListenerContainer customizeRedisListenerContainer(
                RedisConnectionFactory redisConnectionFactory,MessageListener messageListener) {
            RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
            redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
            redisMessageListenerContainer.addMessageListener(messageListener,new PatternTopic("__keyspace@0__:*"));
            return redisMessageListenerContainer;
        }
    }


    @Configuration
    @ConditionalOnExpression("'${spring.redis.host:}'.isEmpty()")
    public static class RedisClusterAutoConfiguration {
        @Bean
        public RedisMessageListenerFactory redisMessageListenerFactory(BeanFactory beanFactory,
                RedisConnectionFactory redisConnectionFactory) {
            RedisMessageListenerFactory beans = new RedisMessageListenerFactory();
            beans.setBeanFactory(beanFactory);
            beans.setRedisConnectionFactory(redisConnectionFactory);
            return beans;
        }
    }
}