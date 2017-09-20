package org.daisy.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpConnectionParams;
import org.apache.log4j.Logger;
import org.daisy.crawler.exception.CrawlerException;
import org.daisy.crawler.http.Cookie;
import org.daisy.crawler.http.ErrorPage;
import org.daisy.crawler.http.OkPage;
import org.daisy.crawler.http.Page;
import org.daisy.crawler.http.Status;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class PageDownloader implements Downloader {

	private final Logger log = Logger.getLogger(PageDownloader.class);

	private final ConcurrentLinkedQueue<Cookie> cookies;

	public PageDownloader(List<Cookie> cookies) {
		
		this.cookies = new ConcurrentLinkedQueue<Cookie>(cookies);
	}

	public PageDownloader() {
		this(new ArrayList<Cookie>());
	}
	
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	public Page get(final String url) {
		DefaultHttpClient client = new DefaultHttpClient();
		for (Cookie cookie : cookies) {
			String name = cookie.name();
			String value = cookie.value();
			log.info("Creating cookie [" + name + " = " + value + "] " + cookie.domain());
			BasicClientCookie clientCookie = new BasicClientCookie(name, value);
			clientCookie.setPath(cookie.path());
			clientCookie.setDomain(cookie.domain());
			client.getCookieStore().addCookie(clientCookie);
		}
		client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 60000);
		client.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 60000);
		return get(client, url);
	}

	public Page get(final HttpClient client, final String url) {
		try {
			String encodedUrl = encode(url);
			log.info("Requesting url: [" + encodedUrl + "]");
			HttpGet method = new HttpGet(encodedUrl);

			try {
				HttpResponse response = client.execute(method);
				Status status = Status.fromHttpCode(response.getStatusLine().getStatusCode());
				if (Status.OK.equals(status)) {
					HttpEntity entity = response.getEntity();
					byte[] data = read(entity.getContent());
					ContentType contentType = ContentType.getOrDefault(entity);
					String mimeType = contentType.getMimeType();
					Charset charset = contentType.getCharset();
					CharsetDetector detector = new CharsetDetector();
					if (charset != null)
						detector.setDeclaredEncoding(charset.name());
					detector.setText(data);
					CharsetMatch match = detector.detect();
					String charsetName = match.getName();

					return new OkPage(url, charsetName, mimeType, data);
				}
				return new ErrorPage(url, status);
			} finally {
				method.abort();
			}

		} catch (IOException e) {
			throw new CrawlerException("Could not retrieve data from " + url, e);
		}
	}

	private byte[] read(final InputStream inputStream) {
		byte[] bytes = new byte[1000];
		int i = 0;
		int b;
		try {
			while ((b = inputStream.read()) != -1) {
				bytes[i++] = (byte) b;
				if (bytes.length == i) {
					byte[] newBytes = new byte[(bytes.length * 3) / 2 + 1];
					for (int j = 0; j < bytes.length; j++) {
						newBytes[j] = bytes[j];
					}
					bytes = newBytes;
				}
			}
		} catch (IOException e) {
			throw new CrawlerException("There was a problem reading stream.", e);
		}

		byte[] copy = Arrays.copyOf(bytes, i);

		return copy;
	}
	
	private String encode(final String url) {
		String res = "";
		for (char c : url.toCharArray()) {
			if (!":/.?&#=".contains("" + c)) {
				try {
					res += URLEncoder.encode("" + c, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new CrawlerException(
							"There is something really wrong with your JVM. It could not find UTF-8 encoding.", e);
				}
			} else {
				res += c;
			}
		}

		return res;
	}
}