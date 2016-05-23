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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * 
 * @author Renato Del Gaudio
 *
 */
public class RouteMain {
	
	
	private final static Logger log = LoggerFactory.getLogger(RouteMain.class);

	public static void main( String[] args )
	{
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
		Updater updater =  context.getBean(Updater.class);   
		Mailer mailer = context.getBean(Mailer.class);

		log.info("Running "+updater.getClass().getName()+" ....");
		long start = System.currentTimeMillis();
		try{
			updater.run(context);
		}catch(Exception e) {
			log.error("Opps something went wrong", e);
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String st = errors.toString();
			mailer.sendEmail("IpUpdater: Software error", "Something went wrong during the program execution.\nStack Trace\n\n"+st);
		}
		long end = System.currentTimeMillis();
		long timeMin = (end - start) / 1000 / 60 / 60;
		log.info(RouteMain.class.getSimpleName()+" completed in "+timeMin+" sec.");
	}
	
}
