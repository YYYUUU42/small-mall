package com.yu.mall.common.lock;

import com.yu.mall.common.lock.aspect.LockAspect;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;


/**
 * @author yu
 * @description Redisson 配置类
 * @date 2024-11-04
 */
@Slf4j
@ConditionalOnClass({RedissonClient.class, Redisson.class})
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonConfig {

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";
    private static final int DEFAULT_TIMEOUT = 3000;
    private static final int DEFAULT_DATABASE = 0;

    /**
     * 配置分布式锁的切面
     */
    @Bean
    @ConditionalOnMissingBean
    public LockAspect lockAspect(RedissonClient redissonClient) {
        return new LockAspect(redissonClient);
    }

    /**
     * 配置 RedissonClient，根据 Redis 配置的不同模式（单机、集群、哨兵）进行初始化。
     */
    @Bean
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(RedisProperties properties) {
        log.debug("尝试初始化 RedissonClient");

        // 获取 Redis 配置
        RedisProperties.Cluster cluster = properties.getCluster();
        RedisProperties.Sentinel sentinel = properties.getSentinel();
        String password = properties.getPassword();
        int timeout = Optional.ofNullable(properties.getTimeout())
                .map(Duration::toMillis)
                .map(Long::intValue)
                .orElse(DEFAULT_TIMEOUT);

        // 设置 Redisson 配置
        Config config = new Config();
        if (cluster != null && !CollectionUtils.isEmpty(cluster.getNodes())) {
            log.debug("使用集群模式初始化 RedissonClient");
            configureClusterMode(config, cluster, timeout, password);
        } else if (sentinel != null && StringUtils.hasText(sentinel.getMaster())) {
            log.debug("使用哨兵模式初始化 RedissonClient");
            configureSentinelMode(config, sentinel, timeout, password);
        } else {
            log.debug("使用单机模式初始化 RedissonClient");
            configureSingleMode(config, properties, timeout, password);
        }

        // 创建 Redisson 客户端
        return Redisson.create(config);
    }

    /**
     * 配置集群模式
     */
    private void configureClusterMode(Config config, RedisProperties.Cluster cluster, int timeout, String password) {
        config.useClusterServers()
                .addNodeAddress(convertNodes(cluster.getNodes()))
                .setConnectTimeout(timeout)
                .setPassword(password);
    }

    /**
     * 配置哨兵模式
     */
    private void configureSentinelMode(Config config, RedisProperties.Sentinel sentinel, int timeout, String password) {
        config.useSentinelServers()
                .setMasterName(sentinel.getMaster())
                .addSentinelAddress(convertNodes(sentinel.getNodes()))
                .setConnectTimeout(timeout)
                .setDatabase(DEFAULT_DATABASE)
                .setPassword(password);
    }

    /**
     * 配置单机模式
     */
    private void configureSingleMode(Config config, RedisProperties properties, int timeout, String password) {
        config.useSingleServer()
                .setAddress(REDIS_PROTOCOL_PREFIX + properties.getHost() + ":" + properties.getPort())
                .setConnectTimeout(timeout)
                .setDatabase(DEFAULT_DATABASE)
                .setPassword(password);
    }

    /**
     * 将 Redis 节点列表转换为带协议的地址数组
     */
    private String[] convertNodes(List<String> nodes) {
        return nodes.stream()
                .map(this::addProtocolIfMissing)
                .toArray(String[]::new);
    }

    /**
     * 为 Redis 节点添加协议前缀（如果不存在）
     */
    private String addProtocolIfMissing(String node) {
        if (!node.startsWith(REDIS_PROTOCOL_PREFIX) && !node.startsWith(REDISS_PROTOCOL_PREFIX)) {
            return REDIS_PROTOCOL_PREFIX + node;
        } else {
            return node;
        }
    }
}

