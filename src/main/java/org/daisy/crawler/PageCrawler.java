
package org.daisy.crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.daisy.crawler.config.CrawlerConfiguration;
import org.daisy.crawler.exception.CrawlerException;
import org.daisy.crawler.filter.UrlFilter;
import org.daisy.crawler.http.Url;
import org.daisy.crawler.scheduler.DefaultScheduler;
import org.daisy.crawler.scheduler.Scheduler;
import org.daisy.crawler.visitor.PageVisitor;

public class PageCrawler {

	private final Logger log = Logger.getLogger(PageCrawler.class);
	
	private Scheduler scheduler;
	private Downloader downloader;
	private BlockingQueue<Runnable> blockingQueue;
	private ThreadPoolExecutor executor;
	private final CrawlerConfiguration config;
	private final AtomicInteger runningTaskCounter = new AtomicInteger(0);
	private final AtomicInteger taskCounter = new AtomicInteger(0);

	public PageCrawler(CrawlerConfiguration config, Scheduler scheduler, Downloader downloader) {
		this.config = config;
		this.scheduler = scheduler;
		this.downloader = downloader;
		this.blockingQueue = new LinkedBlockingQueue<Runnable>();
		this.executor = new ThreadPoolExecutor(config.getMinPoolSize(), config.getMaxPoolSize(), config
				.getAliveMilliseconds(), TimeUnit.MILLISECONDS, blockingQueue);
	}

	public PageCrawler(CrawlerConfiguration config) {
		this(config, new DefaultScheduler(config.isDomainLimited(), config.getMaxDepth()), 
				new PageDownloader());
	}
	
	public PageCrawler() {
		this(new CrawlerConfiguration());
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public void setUrlFilter(UrlFilter urlFilter) {
		scheduler.setUrlFilter(urlFilter);
	}
	
	public void setDownloader(Downloader downloader) {
		this.downloader = downloader;
	}
	
	public Downloader getDownloader() {
		return downloader;
	}
	
	public  int tasksNumberInQueue() {
		return blockingQueue.size();
	}
	
	public ThreadPoolExecutor getExecutor() {
		return executor;
	}
	
	public CrawlerConfiguration getConfiguration() {
		return config;
	}
	
    public void setMaxdepth(int maxDepth) {
    	scheduler.setMaxDepth(maxDepth);
    }
	
	public void enableDomainLimit() {
		scheduler.enableDomainLimit();
	}
	
	public void disableDomainLimit() {
		scheduler.disableDomainLimit();
	}
	
	public void incRunningTaskCounter() {
		runningTaskCounter.incrementAndGet();
	}
	
	public void decRunningTaskCounter() {
		runningTaskCounter.decrementAndGet();
	}
	
	public void incTaskCounter() {
		taskCounter.incrementAndGet();
	}
	
	public void decTaskCounter() {
		taskCounter.decrementAndGet();
	}
	
	public int getTasksNumber() {
		return taskCounter.get();
	}
	public void crawl(String beginUrl, PageVisitor visitor) {
		crawl(beginUrl, visitor, null);
	}
	public void crawl(String beginUrl, PageVisitor visitor, UrlFilter urlFilter) {
		
		if ((beginUrl == null) || (beginUrl.trim().length() == 0)) {
			throw new IllegalArgumentException("beginUrl cannot be null or empty");
		}
		if (!Pattern.compile("(?s)^http[s]?://.*$").matcher(beginUrl).matches()) {
			throw new IllegalArgumentException("beginUrl must start with http:// or https://");
		}
		if (scheduler.isDomainLimited()) {
			scheduler.setDomainName(beginUrl);
		}
		setUrlFilter(urlFilter);
		log.info("crawling starting.");
		if (visitor == null) {
			throw new IllegalArgumentException("visitor cannot be null");
		}

		try {
			executor.execute(new PageCrawlerTask(new Url(beginUrl, 1), this, visitor));

			while (taskCounter.get() != 0) {
				log.info("Tasks that finished: " + executor.getCompletedTaskCount());
				log.info("Number of tasks running: " + runningTaskCounter.get());
				log.info("Number of tasks in queue: " + blockingQueue.size());
				sleep();
			}
			log.info("No tasks anymore.");
		} finally {
			executor.shutdown();
		}
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new CrawlerException("main thread died.", e);
		}

	}
}
