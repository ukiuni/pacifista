package org.ukiuni.pacifista.virtual;

import java.io.IOException;

public interface VirtualHost {

	public abstract void setParameters(String parameters);

	public abstract void boot() throws IOException, InterruptedException;

	public abstract void shutdown() throws IOException, InterruptedException;

	public abstract boolean isRunning() throws IOException, InterruptedException;

	public abstract boolean isExist() throws IOException, InterruptedException;

	public abstract String downloadImage(String url) throws IOException;

	public abstract String downloadImage(String url, String proxyHost, int proxyPort) throws IOException;

	public abstract String downloadImage(String url, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException;

	public abstract InstanceSSHAddress create(String stragePath) throws IOException, InterruptedException;

	public abstract InstanceSSHAddress create(String stragePath, String type, int memory, int port) throws IOException, InterruptedException;

	public abstract void remove() throws IOException, InterruptedException;

	public abstract void openPort(String protocol, int port) throws IOException, InterruptedException;

	public static class InstanceSSHAddress {
		public String host;
		public int port;
		public String keyPath;

		public InstanceSSHAddress(String host, int port, String keyPath) {
			this.host = host;
			this.port = port;
			this.keyPath = keyPath;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getKeyPath() {
			return keyPath;
		}

		public void setKeyPath(String keyPath) {
			this.keyPath = keyPath;
		}
	}

}