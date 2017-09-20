package org.daisy.crawler;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.daisy.crawler.exception.CrawlerException;
import org.daisy.crawler.http.Page;
import org.daisy.crawler.http.Status;
import org.daisy.crawler.http.Url;
import org.daisy.crawler.scheduler.Scheduler;
import org.daisy.crawler.visitor.PageVisitor;

final public class PageCrawlerTask implements Runnable {
	
	private PageCrawler crawler;
	private Scheduler scheduler;
	private PageVisitor visitor;
	private Downloader downloader;
	private ThreadPoolExecutor executor; 

    private final Logger log = Logger.getLogger(PageCrawlerTask.class);
    private final Url urlToCrawl;
    
    public PageCrawlerTask(Url urlToCrawl, PageCrawler crawler, PageVisitor visitor) {
    	this.urlToCrawl = urlToCrawl;
    	this.crawler = crawler;
    	this.scheduler = crawler.getScheduler();
    	this.downloader = crawler.getDownloader();
    	this.executor = crawler.getExecutor();
    	this.visitor = visitor;
    	crawler.incTaskCounter();
    }

    public void run() {
        try {
        	crawler.incRunningTaskCounter();
            log.info("crawling url: " + urlToCrawl.link());
            Page page = downloader.get(urlToCrawl.link());
            if (page.getStatusCode() != Status.OK) {
                visitor.onError(urlToCrawl, page.getStatusCode());
            } else {
                visitor.visit(page);
            }
            if (!page.getMimeType().equals("text/html")) return;
            int tasksNum = crawler.getTasksNumber();
            Thread.sleep(tasksNum*10);
            for (String link : page.getLinks()) {
                final Url url = new Url(link, urlToCrawl.depth() + 1);
                if (scheduler.needToVisit(url)) {
                    executor.execute(new PageCrawlerTask(url, crawler, visitor));
                }
            }

        } catch (InterruptedException e) {
        	throw new CrawlerException("Task is interrupted.", e);
		} finally {
        	crawler.decRunningTaskCounter();
        	crawler.decTaskCounter();
        }
    }

}
