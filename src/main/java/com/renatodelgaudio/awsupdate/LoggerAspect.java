package com.renatodelgaudio.awsupdate;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggerAspect {

    @Before("execution(* com.renatodelgaudio.awsupdate.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {

	Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
	if(log.isDebugEnabled()){
	    Object args[] = joinPoint.getArgs();
	    StringBuilder sb = new StringBuilder();
	    if(args!=null){
		for(Object arg : args){
		    sb.append(arg==null? null : arg);
		    sb.append("\n");
		}
	    }     	
	    log.debug(joinPoint.getSignature().getName()+" ENTRY - parameters\n"+sb.toString());
	}
    }
    
    @After("execution(* com.renatodelgaudio.awsupdate.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {

	Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
	if(log.isDebugEnabled()){
	    log.debug(joinPoint.getSignature().getName()+" EXIT");
	}
    }

}
