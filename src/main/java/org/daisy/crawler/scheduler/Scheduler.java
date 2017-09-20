package org.daisy.crawler.scheduler;

import org.daisy.crawler.filter.UrlFilter;
import org.daisy.crawler.http.Url;

public interface Scheduler {

    boolean needToVisit(Url url);
    
    void setMaxDepth(int maxDepth);
    
    boolean reachMaxDepth(Url url);
    
    void setDomainName(String url);
    
    boolean isDomainLimited();
    
    void enableDomainLimit();
    
    void disableDomainLimit();
    
    void setUrlFilter(UrlFilter urlFilter);
}