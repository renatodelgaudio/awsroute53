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

import static com.renatodelgaudio.awsupdate.EnvUtil.buildRoute53;
import static com.renatodelgaudio.awsupdate.EnvUtil.getAwsFilePath;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trim;

import java.io.File;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.services.route53.AmazonRoute53;

public class PropertyConfig implements Configuration {

    private final static Logger log = LoggerFactory.getLogger(PropertyConfig.class);


    @Value("${zone.id}")
    protected String zoneId;
    @Value("${record.name}")
    private String recordName;
    @Value("${record.ttl}")
    private String ttl;
    @Value("${mailsender.host}")
    private String mailSenderHost;
    @Value("${mailsender.protocol}")
    private String mailSenderProtocol;
    @Value("${mailsender.port}")
    private String mailSenderPort;
    @Value("${mailsender.username}")
    private String mailSenderUsername;
    @Value("${mailsender.password}")
    private String mailSenderPassword;
    @Value("${mailsender.from}")
    private String mailSenderFrom;
    @Value("${mailsender.to}")
    private String mailSenderTo;
    @Value("${mailsender.enable}")
    private String mailsenderEnable;
    
    
	

    private AmazonRoute53 r53;
    
    private String maskPasswd(String passwd){
	if (isBlank(passwd)){
	    return "";
	}
	int len = passwd.length();
	StringBuilder sb = new StringBuilder(len);
	for(int i=0;i<len;i++)
	    sb.append("X");
	return sb.toString();
    }

    @PostConstruct
    private void init() {
	try{
	    String awsPath = getAwsFilePath();
	    r53 = buildRoute53(new File(awsPath));

	    boolean ok =  isNotBlank(zoneId) && isNotBlank(recordName) && isNotBlank(ttl);
	    if(!ok) {
		log.error("Configuration is not OK. Please check that aws.properties contains correct values for zone.id, record.name and record.ttl");
		log.error("zoneId:"+zoneId);
		log.error("recordName:"+recordName);
		log.error("ttl:"+ttl);
	    }
	    
	    boolean mailOK = !equalsIgnoreCase(mailsenderEnable,"true") || (isNotBlank(mailSenderHost) && isNotBlank(mailSenderProtocol) && isNotBlank(mailSenderPort) &&
		    isNotBlank(mailSenderUsername) && isNotBlank(mailSenderPassword) && isNotBlank(mailSenderFrom) && isNotBlank(mailSenderTo)) ;
	    if(!mailOK) {
		log.warn("Configuration email is not OK. Emails might not send out" );
		log.warn("Please check that aws.properties contains at least all values for mailsender.* properties" );
		log.warn("mailSenderHost:"+mailSenderHost);
		log.warn("mailSenderProtocol:"+mailSenderProtocol);
		log.warn("mailSenderPort:"+mailSenderPort);
		log.warn("mailSenderUsername:"+mailSenderUsername);
		log.warn("mailSenderPassword:"+maskPasswd(mailSenderPassword));
		log.warn("mailSenderFrom:"+mailSenderFrom);
		log.warn("mailSenderTo:"+mailSenderTo);
	    }

	}catch(Exception e){
	    log.error("Wrong configuration or incomplete installation. Please try to re-install and complete the post installation configuration.");
	   throw new ConfigurationException(e);
	}
    }

    @Override
    public String getZoneId() {
	return trim(zoneId);
    }

    @Override
    public String getRecordName() {
	return trim(recordName);
    }
    @Override
    public AmazonRoute53 getAmazonClient() {
	return r53;
    }

    @Override
    public String getTTL() {
	return ttl;
    }

}
