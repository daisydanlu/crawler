package org.daisy.crawler.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.daisy.crawler.exception.CrawlerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

final public class OkPage extends Page {

	private byte[] data;
	private String content;
	private Document doc;

	public OkPage(String url, String charset, String mimeType, byte[] data) {
		super(url, charset, mimeType);
		this.data = data;
	}

	public List<String> getLinks() {

		List<String> list = new ArrayList<String>();
		list.addAll(getSrcLinks());
		list.addAll(getAhrefLinks());
		list.addAll(getImportLinks());
		return list;
	}
	
	public List<String> getAhrefLinks() {
		Document doc = getDocument();
		List<String> list = new ArrayList<String>();
		Elements links = doc.select("a[href]");
        for (Element link : links) {
            list.add(link.attr("abs:href"));
        }
		return list;
	}
	
	public List<String> getImportLinks() {
		Document doc = getDocument();
		List<String> list = new ArrayList<String>();
		Elements links = doc.select("link[href]");
        for (Element link : links) {
            list.add(link.attr("abs:href"));
        }
		return list;
	}
	
	public List<String> getSrcLinks() {
		Document doc = getDocument();
		List<String> list = new ArrayList<String>();
		Elements links = doc.select("[src]");
        for (Element link : links) {
            list.add(link.attr("abs:src"));
        }
		return list;
	}
	
	public Document getDocument() {
		if (doc == null) doc = Jsoup.parse(getContent(), getUrl());
		return doc;
	}

	public String getContent() {
		if (content == null) {
			try {
				content = new String(data, getCharset());
			} catch (UnsupportedEncodingException e) {
				throw new CrawlerException("Unsupported charset.", e);
			}
		}
		return content;
	}

    public byte[] getByteData(){
    	return data;
    }
    
	public Status getStatusCode() {
		return Status.OK;
	}
}
