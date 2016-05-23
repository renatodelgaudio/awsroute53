/*
 * Copyright (c) 2016 Renato Del Gaudio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
