package org.ukiuni.pacifista;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.ukiuni.pacifista.util.HttpUtil;
import org.ukiuni.pacifista.util.HttpUtil.HttpMethod;
import org.ukiuni.pacifista.util.ScriptingUtil;

public class Http {
	private static final String DEFAUL_ENCODE = "UTF-8";
	private Runtime runtime;

	public Http(Runtime runtime) {
		this.runtime = runtime;
	}

	public String get(String url) throws IOException {
		return get(url, DEFAUL_ENCODE);
	}

	public String get(String url, String encode) throws IOException {
		String proxyHost = (String) runtime.getEnv("httpProxyHost");
		int proxyPort = null == runtime.getEnv("httpProxyPort") ? 0 : Integer.parseInt((String) runtime.getEnv("httpProxyPort"));
		String proxyUser = (String) runtime.getEnv("httpProxyUser");
		String proxyPassword = (String) runtime.getEnv("httpProxyPassword");
		return get(url, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public String get(String url, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		return get(url, DEFAUL_ENCODE, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public String get(String url, String encode, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		return request(url, encode, "GET", proxyHost, proxyPort, proxyUser, proxyPass);
	}

	public String post(String url) throws IOException {
		return post(url, DEFAUL_ENCODE);
	}

	public String post(String url, String encode) throws IOException {
		String proxyHost = (String) runtime.getEnv("httpProxyHost");
		int proxyPort = null == runtime.getEnv("httpProxyPort") ? 0 : Integer.parseInt((String) runtime.getEnv("httpProxyPort"));
		String proxyUser = (String) runtime.getEnv("httpProxyUser");
		String proxyPassword = (String) runtime.getEnv("httpProxyPassword");
		return post(url, encode, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public String post(String url, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		return request(url, "POST", DEFAUL_ENCODE, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public String post(String url, String encode, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		return request(url, "POST", encode, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public String request(String url, String method) throws IOException {
		return request(url, method, DEFAUL_ENCODE);
	}

	public String request(String url, String encode, String method) throws IOException {
		String proxyHost = (String) runtime.getEnv("httpProxyHost");
		int proxyPort = null == runtime.getEnv("httpProxyPort") ? 0 : Integer.parseInt((String) runtime.getEnv("httpProxyPort"));
		String proxyUser = (String) runtime.getEnv("httpProxyUser");
		String proxyPassword = (String) runtime.getEnv("httpProxyPassword");
		return request(url, method, encode, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public String request(String url, String encode, String method, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			HttpUtil.httpRequest(url, HttpMethod.valueOf(method), out, proxyHost, proxyPort, proxyUser, proxyPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(out.toByteArray(), encode);
	}
	public String request(String url, String method, String requestParameter, String headerParameters) throws IOException {
		return request(url, DEFAUL_ENCODE, method, requestParameter, headerParameters);
	}
	public String request(String url, String encode, String method, String requestParameter, String headerParameters) throws IOException {
		String proxyHost = (String) runtime.getEnv("httpProxyHost");
		int proxyPort = null == runtime.getEnv("httpProxyPort") ? 0 : Integer.parseInt((String) runtime.getEnv("httpProxyPort"));
		String proxyUser = (String) runtime.getEnv("httpProxyUser");
		String proxyPassword = (String) runtime.getEnv("httpProxyPassword");
		return request(url, encode, method, requestParameter, headerParameters, proxyHost, proxyPort, proxyUser, proxyPassword);
	}
	public String request(String url, String method, String requestParameter, String headerParameters, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		return request(url, DEFAUL_ENCODE, method, requestParameter, headerParameters, proxyHost, proxyPort, proxyUser, proxyPassword);
	}

	public String request(String url, String encode, String method, String requestParameter, String headerParameters, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Map<String, String> headerMap = ScriptingUtil.parseParameters(headerParameters);
		HttpUtil.httpRequest(url, HttpMethod.valueOf(method), out, requestParameter, headerMap, proxyHost, proxyPort, proxyUser, proxyPassword);
		return new String(out.toByteArray(), encode);
	}
}
