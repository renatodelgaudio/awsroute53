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

import static com.renatodelgaudio.awsupdate.IpUtil.retrievePublicIP;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
/**
 * 
 * @author Renato Del Gaudio
 *
 */
public class SimpleUpdater implements Updater {

    private final static Logger log = LoggerFactory.getLogger(SimpleUpdater.class);

    @Inject
    protected AWSRecordService recordService;
    @Inject
    protected Configuration config;
    @Inject
    protected Mailer mailSender;
    /**
     * This is the main implementation
     */
    public void run(ApplicationContext context) {

	String publicIP;
	try {
	    publicIP = retrievePublicIP();
	} catch (IpRetrievalException e) {
	    log.error(e.getMessage(),e);
	    throw new RuntimeException(e);
	}

	String dnsIp = recordService.getCurrentIP();
	String sbj = "["+config.getRecordName()+"] awsroute53 ";
	if(equalsIgnoreCase(publicIP,dnsIp)){
	    log.info("AWS DNS ("+config.getRecordName()+") is already configured with the public IP "+publicIP+" No actions were performed at this time");
	    mailSender.sendDebugEmail(sbj+ "OK", "AWS DNS ("+config.getRecordName()+") is already configured with the public IP "+publicIP+" No actions were performed at this time");
	    return;
	}

	boolean success = recordService.updateRecord(publicIP);
	
	if(!success){
	    log.error("Ops. Something went wrong and DNS was not updated");
	    mailSender.sendEmail(sbj+"NOT OK", "Ops. Something went wrong and DNS was not updated");
	    return;
	}
	 mailSender.sendEmail(sbj+"IP UPDATED OK", "Since a new IP has been detected ["+publicIP+"], the DNS for record "+config.getRecordName()+" has been updated accordingly");
    }

}
