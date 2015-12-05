package com.etas.jenkins.plugins.VersionPatcher;

import java.io.File;
import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

public final class FileEncodingDetector {
	
	public static String getFileEncoding(File filePath) throws IOException{
		byte[] buf = new byte[4096];
		java.io.FileInputStream fis = new java.io.FileInputStream(filePath);
	    UniversalDetector detector = new UniversalDetector(null);
	    int nread;
	    while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
	      detector.handleData(buf, 0, nread);
	    }
	    detector.dataEnd();


	    String encoding = detector.getDetectedCharset();
	   
	    detector.reset();
	    fis.close();
		return encoding;
		
	}

}
