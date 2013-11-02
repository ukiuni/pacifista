package org.ukiuni.pacifista.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.ukiuni.pacifista.util.IOUtil;

public class HttpUtil {
	public enum HttpMethod {
		GET, POST, PUT, DELETE;
	}

	public static void download(String url, OutputStream out) throws IOException {
		download(url, out, null, 0, null, null);
	}

	public static URLConnection openConnection(String url, HttpMethod method, String proxyHost, int proxyPort, final String proxyUser, final String proxyPass) throws IOException {
		if (null == proxyHost) {
			return new URL(url).openConnection();
		} else {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
			switch (method) {
			case POST:
			case PUT:
				connection.setDoOutput(true);
			case DELETE:
			case GET:
				connection.setRequestMethod(method.toString());
			}
			if (null != proxyUser && null != proxyPass) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
					}
				});
			}
			return connection;
		}
	}

	public static void download(String url, OutputStream out, String proxyHost, int proxyPort, final String proxyUser, final String proxyPass) throws IOException {
		httpRequest(url, HttpMethod.GET, out, proxyHost, proxyPort, proxyUser, proxyPass);
	}

	public static void httpRequest(String url, HttpMethod httpMethod, OutputStream out, String proxyHost, int proxyPort, final String proxyUser, final String proxyPass) throws IOException {
		InputStream in = openConnection(url, HttpMethod.GET, proxyHost, proxyPort, proxyUser, proxyPass).getInputStream();
		IOUtil.copy(in, out);
		Authenticator.setDefault(null);
		in.close();
	}
}
