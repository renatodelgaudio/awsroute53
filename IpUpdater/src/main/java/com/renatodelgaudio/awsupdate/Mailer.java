package com.renatodelgaudio.awsupdate;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class Mailer {
    private final static Logger log = LoggerFactory.getLogger(Mailer.class);

    
    JavaMailSenderImpl mailSender;
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
    @Value("${mailsender.debug.to}")
    private String mailSenderDebugTo;
    @Value("${mailsender.enable}")
    private String mailsenderEnable;
    
    private String[] to;
    private String[] debugTo;
    
    
    private Boolean init = Boolean.FALSE;

    /**
     * @param args
     */
    public void sendEmail(String subject,String text) {
	initMailer();
	sendEmailTolist(subject, text, getMergedToList());
    }
    public void sendDebugEmail(String subject,String text) {
	initMailer();
	if(!isDebugListEmpty()){
	    sendEmailTolist(subject, text, debugTo);
	}
	
    }
    private void sendEmailTolist(String subject,String text,String []toList) {
	if(!equalsIgnoreCase(mailsenderEnable,"true")) {
	    log.info("Email with subject ["+subject+"] not sent as notification is not enabled.");
	    return;
	}
	try {
	    initMailer();
	    SimpleMailMessage smm = new SimpleMailMessage();
	    smm.setSubject(subject);
	    smm.setFrom(mailSenderFrom);
	    smm.setText(text);
	    smm.setTo(toList);
	    mailSender.send(smm);
	    log.info("Email sent with success.\nSubject:"+subject+"\n"+text);
	}
	catch (Exception e) {
	    log.error(e.getMessage(),e);
	}

    }
    
    private String[] getMergedToList(){
	initMailer();
	Set<String> toList = new HashSet<String>();
	for(String address: to){
	    toList.add(address);
	}
	if(!isDebugListEmpty()){
	    for(String address: debugTo){
		    toList.add(address);
		}
	}
	
	return toList.toArray(new String[toList.size()]);
		
    }
    
    private boolean isDebugListEmpty(){
	return debugTo==null || debugTo.length == 0;
    }
    private void initMailer(){
	synchronized (init) {
	    if (!init){
		mailSender = new JavaMailSenderImpl();
		
		mailSender.setHost(mailSenderHost);
		mailSender.setProtocol(mailSenderProtocol);	
		mailSender.setPort(Integer.parseInt(mailSenderPort));
		mailSender.setUsername(mailSenderUsername);
		mailSender.setPassword(mailSenderPassword);
		
		
		to = mailSenderTo.split(",");
		if(StringUtils.isNotBlank(mailSenderDebugTo))
		    debugTo = mailSenderDebugTo.split(",");
		
		// Advanced section
		Properties prop = EnvUtil.getConfigAsProperty();
		Properties javaMail = new Properties();
		for( Object key : prop.keySet()) {
		    String sk = (String) key;
		    if (StringUtils.startsWith(sk, "mail.")){
			javaMail.put(sk, prop.getProperty(sk));
		    }
		}
		
	
		mailSender.setJavaMailProperties(javaMail);
		init = true;
		log.info("mailSender configured");
	    }
	    
	}
    }


}
