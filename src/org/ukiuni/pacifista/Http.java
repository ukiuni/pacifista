package org.ukiuni.pacifista;

import java.io.IOException;

public class Http {
	private Local local;

	public Http(Local local) {
		this.local = local;
	}

	public String get(String url) throws IOException {
		return this.local.download(url);
	}

	public String get(String url, String encode) throws IOException {
		return this.local.download(url, encode);
	}

	public String get(String url, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		return this.local.httpRequest(url, "GET", "UTF-8", proxyHost, proxyPort, proxyUser, proxyPass);
	}

	public String get(String url, String encode, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		return this.local.httpRequest(url, "POST", encode, proxyHost, proxyPort, proxyUser, proxyPass);
	}

	public String post(String url) throws IOException {
		return this.local.download(url);
	}

	public String post(String url, String encode) throws IOException {
		return this.local.download(url, encode);
	}

	public String post(String url, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		return this.local.httpRequest(url, "POST", "UTF-8", proxyHost, proxyPort, proxyUser, proxyPass);
	}

	public String post(String url, String encode, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		return this.local.httpRequest(url, "POST", encode, proxyHost, proxyPort, proxyUser, proxyPass);
	}

	public String request(String url, String method, String encode) throws IOException {
		return this.local.httpRequest(url, method, encode, null, 0, null, null);
	}

	public String request(String url, String method, String encode, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		return this.local.httpRequest(url, method, encode, proxyHost, proxyPort, proxyUser, proxyPass);
	}
}
