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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

public class IpInfo implements IpProvider {
	
	private final static Logger log = LoggerFactory.getLogger(IpInfo.class);

	public String providerName() {
		return "IpInfo";
	}
	@SuppressWarnings(value="DM_DEFAULT_ENCODING",justification="There are no other changes when the content encoding is not present in the HTTP response")
	public String getIP() throws IpRetrievalException{
		try{
			Gson gson = new Gson();
			URL url = new URL("http://ipinfo.io/json");
			URLConnection conn = url.openConnection();
			String encoding = conn.getContentEncoding();
			// open the stream and put it into BufferedReader
			InputStreamReader isr = encoding == null ? new InputStreamReader(conn.getInputStream()) : new InputStreamReader(conn.getInputStream(),encoding);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ( (line = br.readLine()) != null){
				sb.append(line);
			}

			br.close();

			IpInfoBean one = gson.fromJson(sb.toString(), IpInfoBean.class);

			log.info("Public IP: "+one.getIp());
			return one.getIp();
		}catch(Exception e){
			log.error("Cannot get IP from "+"http://ipinfo.io/json");
			IpRetrievalException ex = new IpRetrievalException("Cannot get IP from "+"http://ipinfo.io/json", e);
			ex.setProviderName(providerName());
			throw ex;
		}
	}

}
