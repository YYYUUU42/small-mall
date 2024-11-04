package com.yu.mall.common.lock.enums;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * @author yu
 * @description 锁类型枚举，定义了不同的锁类型
 * @date 2024-11-04
 */
public enum LockType {

    /**
     * 默认的非公平锁。
     */
    DEFAULT {
        @Override
        public RLock getLock(RedissonClient redissonClient, String name) {
            return redissonClient.getLock(name);
        }
    },

    /**
     * 公平锁，保证锁的获取顺序。
     */
    FAIR_LOCK {
        @Override
        public RLock getLock(RedissonClient redissonClient, String name) {
            return redissonClient.getFairLock(name);
        }
    },

    /**
     * 读锁，适用于读写锁模式。
     */
    READ_LOCK {
        @Override
        public RLock getLock(RedissonClient redissonClient, String name) {
            return redissonClient.getReadWriteLock(name).readLock();
        }
    },

    /**
     * 写锁，适用于读写锁模式。
     */
    WRITE_LOCK {
        @Override
        public RLock getLock(RedissonClient redissonClient, String name) {
            return redissonClient.getReadWriteLock(name).writeLock();
        }
    };

    /**
     * 获取锁的抽象方法。
     *
     * @param redissonClient Redisson 客户端
     * @param name           锁的名称
     * @return 对应类型的锁对象
     */
    public abstract RLock getLock(RedissonClient redissonClient, String name);
}
