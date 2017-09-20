
package org.daisy.crawler;

import org.daisy.crawler.http.Page;


public interface Downloader {

    public Page get(String url);

}
