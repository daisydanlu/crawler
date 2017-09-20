package org.daisy.crawler.exception;

public class CrawlerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CrawlerException(final String message) {
		super(message);
	}

	public CrawlerException(final String message, final Throwable t) {
		super(message, t);
	}

	public CrawlerException(final Throwable t) {
		super(t);
	}

}
