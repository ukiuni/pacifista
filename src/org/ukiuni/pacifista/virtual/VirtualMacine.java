package org.ukiuni.pacifista.virtual;

import java.io.File;

public class VirtualMacine {
	private File baseDir;

	public VirtualMacine(File baseDir) {
		this.baseDir = baseDir;
	}

	public VirtualBoxHost getHost(String type, String host) {
		return new VirtualBoxHost(host, baseDir);
	}

	public VirtualBoxHost getHost(String host) {
		return new VirtualBoxHost(host, baseDir);
	}
}
