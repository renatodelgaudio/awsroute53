package com.renatodelgaudio.awsupdate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class EnvUtilTest {
	
	@Test(expected=ConfigurationException.class)
	public void testInstallDirNoProperty(){
		
		EnvUtil.INSTALL_DIR();
		
	}
	
	@Test
	public void testInstallDir(){
		
		String baseLocation = "C:\\awsupdate";
		String locations [] = {baseLocation,baseLocation+"\\",baseLocation+"/"}; 
		String expecteds [] = {baseLocation+File.separator,baseLocation+"\\",baseLocation+"/"}; 
		assertTrue(locations.length == expecteds.length);
		
		for(int i=0;i<locations.length;i++){
			System.setProperty(EnvUtil.PROP_INSTALL_DIR,locations[i]);
			String actual = EnvUtil.INSTALL_DIR();
			assertEquals("Root installation not set correctly",expecteds[i],actual);
		}
					
	}
	
	
}
