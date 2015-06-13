/**
 * Copyright (c) 2015 Renato Del Gaudio
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
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.trim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53Client;

public class EnvUtil {
	
	/**
	 * This is the expected property passed to Java on boot
	 * <br> e.g. java -DINSTALL_DIR= 
	 */
	public static final String PROP_INSTALL_DIR = "INSTALL_DIR";
	public static final String PROP_AWS_FILE_NAME = "aws.properties";
	
	
	private final static Logger log = LoggerFactory.getLogger(EnvUtil.class);

	private EnvUtil(){}
	/*
	 * It returns the installation path ending with either / or \
	 */
	public static String INSTALL_DIR(){
		String root = System.getProperty(PROP_INSTALL_DIR);
		
		if (isBlank(root)){
			log.error("Environment not initiazed properly. Please make sure Java is started with a not empty value for -D"+PROP_INSTALL_DIR);
			throw new ConfigurationException("-D"+PROP_INSTALL_DIR+" is missing or empty. Program aborted");
		}
		String trim = trim(root);
		if (!StringUtils.endsWith(trim, "/") && !StringUtils.endsWith(trim, "\\"))
			trim += File.separator;
		
		return trim;
	}
	
	public static String getAwsFilePath(){
		return INSTALL_DIR() + PROP_AWS_FILE_NAME;
	}
	
	public static AmazonRoute53 buildRoute53(File aws){
		AmazonRoute53 r53 = null;
		try {
			r53 = new AmazonRoute53Client(new PropertiesCredentials(aws));
		} catch (Exception e) {
			log.error("Cannot build AmazonRouter53Client",e);
			throw new ConfigurationException(e.getMessage());
		} 
		return r53;
	}
	
	public static Properties getConfigAsProperty(){
	    String awsPath = getAwsFilePath();
	    File file = new File(awsPath);

	    FileInputStream stream =null;

	    try {
		stream = new FileInputStream(file);
		Properties ret = new Properties();
		ret.load(stream);
		return ret;
	    } 
	    catch (Exception e) {
		throw new ConfigurationException(e.getMessage());
	    }
	    finally {
		try {
		    stream.close();
		} catch (IOException ignored){}
	    }
	}
}
