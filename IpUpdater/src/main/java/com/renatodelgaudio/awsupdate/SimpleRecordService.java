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

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResult;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

public class SimpleRecordService implements AWSRecordService {
	
	private final static Logger log = LoggerFactory.getLogger(SimpleRecordService.class);
	@Autowired
	Configuration config;
	
	@Override
	public String getCurrentIP() {
		ResourceRecordSet set = getCurrentRecordSet();
		if (set!=null && set.getResourceRecords() != null && set.getResourceRecords().size() > 0){
			ResourceRecord	rr = set.getResourceRecords().get(0);
			if(rr!=null){
				return rr.getValue();
			}
		}
		return null;
	}
	
	public ResourceRecordSet getCurrentRecordSet() {
		
		if (log.isDebugEnabled()){
			log.debug("zoneId:"+config.getZoneId());
			log.debug("recordName:"+config.getRecordName());
		}
		ListResourceRecordSetsRequest request = new ListResourceRecordSetsRequest();
		request.setHostedZoneId(config.getZoneId());

		ListResourceRecordSetsResult result = config.getAmazonClient().listResourceRecordSets(request);
		List<ResourceRecordSet> recordSets = result.getResourceRecordSets();


		for(ResourceRecordSet recordSet : recordSets) {

			String currName = recordSet.getName();

			if (currName == null)
				continue;
			currName = currName.trim();

			// AWS ends name with dot.
			if (currName.endsWith("."))
				currName = currName.substring(0,currName.length()-1);

			if (equalsIgnoreCase(config.getRecordName(),currName)) {			
				return recordSet;
			}
		}

		return null;
	}

	@Override
	public boolean updateRecord(String ip) {
	    
	    String recordName = config.getRecordName();
		
		ChangeResourceRecordSetsRequest changeRequest = new ChangeResourceRecordSetsRequest();
		changeRequest.setHostedZoneId(config.getZoneId());
		ChangeBatch batch = new ChangeBatch();
		Change change = new Change();
		
		
		ResourceRecordSet set = getCurrentRecordSet();
		if (set!=null){
			if (!equalsIgnoreCase("A", set.getType())){
				log.error("Record already exists but not as Type A. No actions were performed.");
				return false;
			}
			change.setAction("UPSERT");
			log.info("Record ["+recordName+"] already present on AWS Route 53. Upating it..");
		} else{
			change.setAction("CREATE");
			log.info("Record ["+recordName+"] not present on AWS Route 53. Creating it..");
			set = new ResourceRecordSet().withName(recordName).withType("A");
		}

		
		set.setTTL(Long.parseLong(config.getTTL()));
		List<ResourceRecord> l = new ArrayList<ResourceRecord>();
		l.add(new ResourceRecord(ip));
		set.setResourceRecords(l);

		change.setResourceRecordSet(set);
		batch.withChanges(change);

		changeRequest.setChangeBatch(batch);

		log.info("Updating DNS "+recordName+" with IP "+ip);
	
		ChangeResourceRecordSetsResult result =  config.getAmazonClient().changeResourceRecordSets(changeRequest);
		log.info(result.toString()); 
		return true;
	}

}
