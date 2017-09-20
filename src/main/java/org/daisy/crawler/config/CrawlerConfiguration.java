package org.daisy.crawler.config;


final public class CrawlerConfiguration {

	private int minPoolSize;
	private int maxPoolSize;
	private long aliveMilliseconds;
	private boolean isDomainLimited;
	private int maxDepth;

	public CrawlerConfiguration() {
		this(30, 30, 3000L, true, Integer.MAX_VALUE);
	}
	
	public CrawlerConfiguration(int minPoolSize, int maxPoolSize,long aliveMilliseconds,
								boolean isDomainLimited, int maxDepth) {
		
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.aliveMilliseconds = aliveMilliseconds;
		this.isDomainLimited = isDomainLimited;
		this.maxDepth = maxDepth;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	
	public void setMaxDepth(int depth) {
		this.maxDepth = depth;
	}
	
	public int getMinPoolSize() {
		return minPoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public long getAliveMilliseconds() {
		return aliveMilliseconds;
	}

	public void setMinPoolSize(final int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public void setMaxPoolSize(final int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public void setAliveMilliseconds(final long aliveMilliseconds) {
		this.aliveMilliseconds = aliveMilliseconds;
	}
	
	public boolean isDomainLimited() {
		return isDomainLimited;
	}

}
