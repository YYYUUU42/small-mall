package com.yu.mall.common.lock.aspect;

import com.yu.mall.common.exception.CustomException;
import com.yu.mall.common.lock.anno.Lock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yu
 * @description 分布式锁切面
 * @date 2024-11-04
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {

	private static final Pattern SPEL_PATTERN = Pattern.compile("\\#\\{([^\\}]*)\\}");
	private static final String SPEL_PREFIX = "#";
	private static final String ERROR_MSG = "leaseTime不能为空";

	private final RedissonClient redissonClient;

	/**
	 * 缓存方法反射获取
	 */
	private static final ConcurrentMap<MethodSignature, Method> methodCache = new ConcurrentHashMap<>();

	@Around("@annotation(properties)")
	public Object handleLock(ProceedingJoinPoint pjp, Lock properties) throws Throwable {
		validateLockProperties(properties);

		String lockName = resolveLockName(properties.name(), pjp);
		RLock rLock = properties.lockType().getLock(redissonClient, lockName);

		boolean lockAcquired = properties.lockStrategy().tryLock(rLock, properties);
		if (!lockAcquired) {
			// 获取锁失败，返回 null 或者可以根据需求抛出异常
			return null;
		}

		try {
			// 执行目标方法
			return pjp.proceed();
		} finally {
			// 自动解锁逻辑
			releaseLock(rLock, properties.autoUnlock());
		}
	}

	/**
	 * 校验锁属性
	 */
	private void validateLockProperties(Lock properties) {
		if (!properties.autoUnlock() && properties.leaseTime() <= 0) {
			throw new CustomException(ERROR_MSG);
		}
	}

	/**
	 * 释放锁，避免 unlock 时抛出异常导致程序崩溃
	 */
	private void releaseLock(RLock rLock, boolean autoUnlock) {
		if (autoUnlock) {
			try {
				if (rLock.isHeldByCurrentThread()) {
					rLock.unlock();
				}
			} catch (IllegalMonitorStateException e) {
				// 解锁失败的异常处理，可以记录日志
				log.error("Failed to release lock: {}", e.getMessage());
			}
		}
	}

	/**
	 * 解析锁名称，支持 spEl 表达式
	 */
	private String resolveLockName(String lockName, ProceedingJoinPoint pjp) {
		if (StringUtils.isEmpty(lockName) || !lockName.contains(SPEL_PREFIX)) {
			return lockName;
		}

		// 构建 spEl 上下文
		StandardEvaluationContext context = buildEvaluationContext(pjp);
		ExpressionParser parser = new SpelExpressionParser();
		Matcher matcher = SPEL_PATTERN.matcher(lockName);

		StringBuilder result = new StringBuilder();
		while (matcher.find()) {
			String spElExpression = matcher.group(1);
			Expression expression = parser.parseExpression(spElExpression);
			Object value = expression.getValue(context);
			matcher.appendReplacement(result, ObjectUtils.nullSafeToString(value));
		}
		matcher.appendTail(result);

		return result.toString();
	}

	/**
	 * 构建 SpEL 的上下文
	 */
	private StandardEvaluationContext buildEvaluationContext(ProceedingJoinPoint pjp) {
		Method method = resolveMethod(pjp);
		StandardEvaluationContext context = new StandardEvaluationContext();
		Class<?>[] paramNames = methodCache.get(method).getParameterTypes();

		Object[] args = pjp.getArgs();
		for (int i = 0; i < args.length; i++) {
			context.setVariable(String.valueOf(paramNames[i]), args[i]);
		}

		return context;
	}

	/**
	 * 获取方法对象，使用缓存提升性能
	 */
	private Method resolveMethod(ProceedingJoinPoint pjp) {
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		return methodCache.computeIfAbsent(signature, sig -> {
			try {
				return pjp.getTarget().getClass().getDeclaredMethod(sig.getName(), sig.getParameterTypes());
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException("Method not found: " + sig.getName(), e);
			}
		});
	}
}
