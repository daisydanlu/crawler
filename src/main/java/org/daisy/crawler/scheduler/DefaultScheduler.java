package org.daisy.crawler.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.daisy.crawler.filter.UrlFilter;
import org.daisy.crawler.http.Url;

public class DefaultScheduler implements Scheduler {
	
	private final Map<Url, Boolean> visitedUrls = new ConcurrentHashMap<Url, Boolean>();
	private String domainName;
	private boolean isDomainLimited;
	private int maxDepth;
	private UrlFilter urlFilter;
	
	public DefaultScheduler(boolean isDomainLimited, int maxDepth) {
		this(isDomainLimited, maxDepth, null);
	}
	
	public DefaultScheduler(boolean isDomainLimited, int maxDepth, UrlFilter urlFilter) {
		this.maxDepth = maxDepth;
		this.isDomainLimited = isDomainLimited;
		this.urlFilter = urlFilter;
		domainName = null;
	}
	
    public boolean needToVisit(Url url) {
    	
        if (visitedUrls.get(url) == Boolean.TRUE)
            return false;
        if (reachMaxDepth(url)) 
        	return false;
        if (isDomainLimited && !url.link().startsWith(domainName))
        	return false;
        if (urlFilter != null && !urlFilter.isLinkNeedToSearch(url.link()))
        	return false;
        visitedUrls.put(url, true);
        return true;
    }
    
    public boolean reachMaxDepth(Url url){
    	return url.depth() > maxDepth ? true : false;
    }
    
    public void setDomainName(String baseUrl) {
		Matcher matcher = Pattern.compile("(http://[^/]+)").matcher(baseUrl);
		if (!matcher.find()) {
			throw new IllegalArgumentException("baseUrl must match http://[^/]+");
		}
		this.domainName = matcher.group(1) + "/";
    }
    
    public void setMaxDepth(int maxDepth) {
    	this.maxDepth = maxDepth;
    }
    
    public boolean isDomainLimited() {
    	return isDomainLimited;
    }
    
    public void disableDomainLimit() {
    	isDomainLimited = false;
    }
    
    public void enableDomainLimit() {
    	isDomainLimited = true;
    }
    
    public void setUrlFilter(UrlFilter urlFilter) {
    	this.urlFilter = urlFilter;
    }
    
}