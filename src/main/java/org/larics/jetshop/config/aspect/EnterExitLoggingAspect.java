package org.larics.jetshop.config.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * Defines aspects.
 * 
 * @author Igor Laryukhin
 */
// Note: Instead of using of "Component" the aspect can also be declared 
// as "Bean" in Config.
@Component
@Aspect
@Order(0)
public class EnterExitLoggingAspect {

	private static final Logger logger = LoggerFactory.
			getLogger(EnterExitLoggingAspect.class);
	private static final String ROOT_PACKAGE = "org.larics.jetshop";

	// Sets pointcut: 
	// all methods of all classes within root and sub-packages
	@Pointcut("execution(* " + ROOT_PACKAGE + "..*.*(..))")
	public void method() {
	}

	// Using the pointcut defined above
	@Before("method()")
	public void logBefore(JoinPoint joinPoint) {
		logger.trace("--- Entering {}", joinPoint.getSignature());
	}

	@After("method()")
	public void logAfter(JoinPoint joinPoint) {
		logger.trace("--- Exiting {}", joinPoint.getSignature());
	}

}
