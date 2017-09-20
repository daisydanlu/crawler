package org.daisy.crawler.http;

import java.util.ArrayList;
import java.util.List;


final public class ErrorPage extends Page {

    private final Status error;

    public ErrorPage(final String url, final Status error) {
    	super(url, "unknown", "unknown");
        this.error = error;
    }

    public String getContent() {
        return "";
    }

    public Status getStatusCode() {
        return error;
    }
    
    public List<String> getLinks() {
    	return new ArrayList<String>();
    }
    
    public byte[] getByteData() {
    	return null;
    }

}
