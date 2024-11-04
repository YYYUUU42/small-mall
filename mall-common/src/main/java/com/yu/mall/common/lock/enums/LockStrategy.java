package com.yu.mall.common.lock.enums;

import com.yu.mall.common.exception.CustomException;
import com.yu.mall.common.lock.anno.Lock;
import org.redisson.api.RLock;

/**
 * @author yu
 * @description 锁策略枚举，定义了不同的锁获取策略
 * @date 2024-11-04
 */
public enum LockStrategy {

	/**
	 * 不重试，直接结束，返回 false。
	 */
	SKIP_FAST {
		@Override
		public boolean tryLock(RLock lock, Lock lockProperties) throws InterruptedException {
			return lock.tryLock(0, lockProperties.leaseTime(), lockProperties.timeUnit());
		}
	},

	/**
	 * 不重试，直接结束，抛出异常。
	 */
	FAIL_FAST {
		@Override
		public boolean tryLock(RLock lock, Lock lockProperties) throws InterruptedException {
			boolean success = lock.tryLock(0, lockProperties.leaseTime(), lockProperties.timeUnit());
			if (!success) {
				throw new CustomException("获取锁失败: 请求太频繁");
			}
			return true;
		}
	},

	/**
	 * 重试，直到超时后，直接结束，返回 false。
	 */
	SKIP_AFTER_RETRY_TIMEOUT {
		@Override
		public boolean tryLock(RLock lock, Lock lockProperties) throws InterruptedException {
			return lock.tryLock(lockProperties.waitTime(), lockProperties.leaseTime(), lockProperties.timeUnit());
		}
	},

	/**
	 * 重试，直到超时后，抛出异常。
	 */
	FAIL_AFTER_RETRY_TIMEOUT {
		@Override
		public boolean tryLock(RLock lock, Lock lockProperties) throws InterruptedException {
			boolean success = lock.tryLock(lockProperties.waitTime(), lockProperties.leaseTime(), lockProperties.timeUnit());
			if (!success) {
				throw new CustomException("获取锁超时: 请求超时");
			}
			return true;
		}
	},

	/**
	 * 不停重试，直到成功为止。
	 */
	KEEP_RETRY {
		@Override
		public boolean tryLock(RLock lock, Lock lockProperties) throws InterruptedException {
			// 直接使用 lock 方法，不设定等待时间，直到成功为止。
			lock.lock(lockProperties.leaseTime(), lockProperties.timeUnit());
			return true;
		}
	};

	/**
	 * 尝试获取锁的抽象方法。
	 *
	 * @param lock           Redisson 锁对象
	 * @param lockProperties 锁的相关配置
	 * @return 是否成功获取锁
	 * @throws InterruptedException 如果线程在等待锁时被中断
	 */
	public abstract boolean tryLock(RLock lock, Lock lockProperties) throws InterruptedException;
}
