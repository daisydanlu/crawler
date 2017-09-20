package org.daisy.crawler.visitor;

import org.daisy.crawler.http.Page;
import org.daisy.crawler.http.Status;
import org.daisy.crawler.http.Url;


public interface PageVisitor {

	void visit(Page page);

	void onError(Url errorUrl, Status statusError);

}
