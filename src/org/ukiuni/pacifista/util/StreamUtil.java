package org.ukiuni.pacifista.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

public class StreamUtil {
	public static class LinkedListOutputStream extends OutputStream {
		private final LinkedList<Integer> inToOut;

		public LinkedListOutputStream(LinkedList<Integer> inToOut) {
			this.inToOut = inToOut;
		}

		public LinkedListOutputStream(LinkedList<Integer> inToOut, int readTimeout) {
			this(inToOut);
		}

		@Override
		public void write(int b) throws IOException {
			synchronized (inToOut) {
				inToOut.add(b);
			}
		}

		@Override
		public void flush() throws IOException {
			synchronized (inToOut) {
				if (0 < inToOut.size()) {
					inToOut.notifyAll();
				}
			}
			super.flush();
		}
	}

	public static class LinkedListInputStream extends InputStream {
		public static final int RETURN_AS_TIMEOUT = -2;
		private final LinkedList<Integer> outToIn;
		private int readTimeout = -1;

		public LinkedListInputStream(LinkedList<Integer> outToIn) {
			this.outToIn = outToIn;
		}

		public LinkedListInputStream(LinkedList<Integer> outToIn, int readTimeout) {
			this(outToIn);
			this.readTimeout = readTimeout;
		}

		@Override
		public int read() throws IOException {
			synchronized (outToIn) {
				if (outToIn.isEmpty()) {
					try {
						if (readTimeout > 0) {
							outToIn.wait(readTimeout);
						} else {
							outToIn.wait();
						}
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				if (outToIn.isEmpty()) {
					return RETURN_AS_TIMEOUT;
				}
				return outToIn.removeFirst();
			}
		}

		@Override
		public int read(byte[] array, int begin, int length) throws IOException {
			int readed = 0;
			for (int i = begin; i < begin + length; i++) {
				int arg = read();
				if (arg > 0) {
					array[i] = (byte) arg;
					readed++;
				} else {
					return readed;
				}
			}
			return readed;
		}
	}

	public static class CopyWorker extends Thread {
		private OutputStream out;
		private InputStream in;
		private EventHandler handler;

		public CopyWorker(InputStream in, OutputStream out, EventHandler handler, boolean daemon) {
			this.in = in;
			this.out = out;
			this.handler = handler;
			this.setDaemon(true);
		}

		@Override
		public void run() {
			try {
				int readed = in.read();
				while (readed != -1) {
					out.write(readed);
					readed = in.read();
				}
			} catch (IOException e) {
				if (null != handler) {
					handler.onError(e);
				}
			}
			if (null != handler) {
				handler.onEnd();
			}

		}

		public static interface EventHandler {
			public void onEnd();

			public void onError(Throwable e);
		}
	}

	public static class InputStreamFilter extends InputStream {
		private InputStream in;
		private EventHandler handler;

		public InputStreamFilter(InputStream in, EventHandler handler) {
			this.in = in;
			this.handler = handler;
		}

		@Override
		public int read() throws IOException {
			int readed = in.read();
			if (null != handler) {
				handler.onRead(readed);
			}
			return readed;
		}

		@Override
		public int read(byte[] array, int begin, int length) throws IOException {
			return in.read(array, begin, length);
		}

		public static interface EventHandler {
			public void onRead(int in);
		}
	}
}
