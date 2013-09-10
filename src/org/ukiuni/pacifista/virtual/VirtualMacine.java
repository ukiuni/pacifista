package org.ukiuni.pacifista.virtual;

import java.io.File;

public class VirtualMacine {
	private File baseDir;

	public VirtualMacine(File baseDir) {
		this.baseDir = baseDir;
	}

	public VirtualHost getHost(String type, String host) {
		if ("AWS".equals(type)) {
			return new EC2VirtualHost(baseDir, host);
		}
		return new VirtualBoxHost(baseDir, host);
	}

	public VirtualHost getHost(String host) {
		return new VirtualBoxHost(baseDir, host);
	}
}
