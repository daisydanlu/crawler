package org.daisy.crawler.http;

import java.util.List;


public abstract class Page {
	
	private String url;
	private String mimeType;
	private String charset;
	
	protected Page(String url, String charset, String mimeType) {
		this.url = url;
		this.charset = charset;
		this.mimeType = mimeType;
	}

    public String getUrl(){
    	return url;
    }
    
    public String getMimeType(){
    	return mimeType;
    }
    
    public String getCharset() {
    	return charset;
    }

    public abstract String getContent();

    public abstract Status getStatusCode();
    
    public abstract List<String> getLinks();
    
    public abstract byte[] getByteData();

}
