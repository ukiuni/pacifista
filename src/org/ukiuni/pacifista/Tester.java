package org.ukiuni.pacifista;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;

import org.ukiuni.pacifista.util.ScriptingUtil;
import org.ukiuni.pacifista.util.ScriptingUtil.LsResult;

public class Tester {
	private Remote remote;

	public Tester() {

	}

	public Tester(Remote remote) {
		this.remote = remote;
	}

	public Tester create(Remote remote) {
		return new Tester(remote);
	}

	public void assertFile(String path, String mode) throws IOException, AssertionError {
		assertFile(path, mode, null, null);
	}

	public void assertFile(String path, String mode, String owner) throws IOException, AssertionError {
		assertFile(path, mode, null, owner);
	}

	public void portOpen(int portNumber) throws UnknownHostException, IOException {
		try {
			Socket socket = new Socket(remote.getHost(), portNumber);
			socket.close();
		} catch (Exception e) {
			throw new AssertionError("host " + remote.getHost() + "'s port [" + portNumber + "] is not open", e);
		}
	}

	public void assertFile(String path, String mode, String group, String owner) throws IOException, AssertionError {
		String lsValue = this.remote.execute("ls -aldpF --color=no --time-style=+'%Y/%m/%d %H:%M:%S' " + path);
		try {
			LsResult lsResult = ScriptingUtil.parseLs(lsValue);
			if (null != owner && !lsResult.owner.equals(owner)) {
				throw new AssertionError("owner expect[" + owner + "] but was [" + lsResult.owner + "]");
			}
			if (null != group && !lsResult.group.equals(group)) {
				throw new AssertionError("group expect[" + group + "] but was [" + lsResult.group + "]");
			}
			if (null != mode && !lsResult.mode.equals(mode)) {
				throw new AssertionError("mode expect[" + mode + "] but was [" + lsResult.mode + "]");
			}
		} catch (ParseException e) {
			throw new AssertionError(e);
		}
	}

	public static class AssertionError extends Error {

		public AssertionError() {
			super();
		}

		public AssertionError(String message, Throwable cause) {
			super(message, cause);
		}

		public AssertionError(String message) {
			super(message);
		}

		public AssertionError(Throwable cause) {
			super(cause);
		}

	}

	public Remote getRemote() {
		return remote;
	}

	public void setRemote(Remote remote) {
		this.remote = remote;
	}
}
