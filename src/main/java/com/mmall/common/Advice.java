package com.mmall.common;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by Zhang Chen
 */
@Aspect
public class Advice {

    @Pointcut("execution(* com.mmall.controller.*.*.*(..))")
    public void forControllerPackage(){}

    @AfterReturning(pointcut="forControllerPackage()", returning="response")
    public void afterReturningFromController(JoinPoint theJoinPoint, ServerResponse response) {

    }


}
