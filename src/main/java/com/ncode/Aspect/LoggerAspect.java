package com.ncode.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggerAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    @Before("execution(* com.ncode.controller.*.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        for(Object arg: joinPoint.getArgs()) {
            sb.append("arg:" + arg + " | ");
        }
        logger.info("beforeMethod " + sb.toString());
    }

    @After("execution(* com.ncode.controller.*.*(..))")
    public void afterMethod() {
        logger.info("afterMethod");
    }
}
