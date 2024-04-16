package com.tool.reg.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut expression to match methods within service package
    @Before("execution(* com.tool.reg.service.impl.*.*(..))")
    public void logServiceAccess(JoinPoint joinPoint) {
        logger.info("Accessing: " + joinPoint.getSignature().toShortString() + " - Args: " + Arrays.toString(joinPoint.getArgs()));
    }

    @Before("execution(* com.tool.reg.controller.*.*(..))")
    public void logControllerAccess(JoinPoint joinPoint) {
        logger.info("Accessing: " + joinPoint.getSignature().toShortString() + " - Args: " + Arrays.toString(joinPoint.getArgs()));
    }

}
