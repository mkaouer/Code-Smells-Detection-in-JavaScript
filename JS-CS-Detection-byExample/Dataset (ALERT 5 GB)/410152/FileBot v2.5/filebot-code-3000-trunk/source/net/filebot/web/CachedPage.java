package net.filebot.web;

import static net.filebot.util.FileUtilities.*;
import static net.filebot.web.WebRequest.*;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class CachedPage extends AbstractCachedResource<String, String> {

	public CachedPage(URL url) {
		super(url.toString(), String.class, ONE_DAY, 0, 0); // 24h update interval
	}

	@Override
	protected Cache getCache() {
		return CacheManager.getInstance().getCache("web-datasource");
	}

	@Override
	public String process(String data) throws Exception {
		return data;
	}

	@Override
	protected String fetchData(URL url, long lastModified) throws IOException {
		return readAll(openConnection(url));
	}

	protected Reader openConnection(URL url) throws IOException {
		return getReader(url.openConnection());
	}

}
