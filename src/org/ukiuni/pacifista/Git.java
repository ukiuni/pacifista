package org.ukiuni.pacifista;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class Git {
	private File baseDir;
	private Runtime runtime;

	public Git(File baseDir, Runtime runtime) {
		this.baseDir = baseDir;
		this.runtime = runtime;
	}

	public void clone(String path, String to) throws InvalidRemoteException, TransportException, GitAPIException {
		this.clone(path, to, null, null);
	}

	public void clone(String path, String to, String user, String password) throws InvalidRemoteException, TransportException, GitAPIException {
		File toFolder = new File(baseDir, to);
		toFolder.getParentFile().mkdirs();
		CloneCommand clone = org.eclipse.jgit.api.Git.cloneRepository().setURI(path).setDirectory(toFolder);
		if (null != user && null != password) {
			clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, password));
		}
		clone.call();
	}
}
