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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtil {

	private final static Logger log = LoggerFactory.getLogger(IpUtil.class);
	
	
	/**
	 * 
	 * @return the known IP Provider in random order
	 */
	public static List<IpProvider> getIpProviders(){

		IpProvider providers[] = new IpProvider[2];
		providers[0] = new IcanHazip();
		providers[1] = new IpInfo();

		int mainIndex = new Random().nextInt(2);
		int fallBackIndex = ~mainIndex + 2;

		List<IpProvider> ret = new ArrayList<IpProvider>();
		ret.add(providers[mainIndex]);
		ret.add(providers[fallBackIndex]);
		return ret;

	}

	/**
	 * It retrieves the Public IP trying multiple providers (if needed)
	 * @return
	 * @throws IpRetrievalException if no IP can be retrieved from the available providers or an invalid IP is returned
	 */
	public static String retrievePublicIP() throws IpRetrievalException{

		List<IpProvider> providers = getIpProviders();
		String ip = null;
		IpProvider provider = null;
		for(IpProvider p : providers){
			try{
				provider = p;
				ip = p.getIP();
				break;
			}catch(IpRetrievalException e){
				log.error("Could not retrieve the public IP from "+provider.providerName(),e);
				continue;
			}
		}

		// validate IP
		if (isBlank(ip)){
			log.error("Cannot get public IP. Exiting...");
			IpRetrievalException ex = new IpRetrievalException("Cannot get public IP. Exiting...");
			ex.setProviderName(provider.providerName());
			throw ex;
		}
		if (!new IPAddressValidator().validate(ip)){
			log.error(ip+ " is not a valid IP. Exiting...");
			IpRetrievalException ex = new IpRetrievalException(ip+ " is not a valid IP. Exiting...");
			ex.setProviderName(provider.providerName());
			throw ex;
		}


		return ip;		
	}

}
