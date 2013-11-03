package org.ukiuni.pacifista;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestHttp {
	@Test
	public void testRequest() throws Exception {
		Http http = new Http(new Runtime(new File("."), new File("templates"), new File("plugins"), new HashMap<String, Object>()));
		final Map<String, String> map = new HashMap<String, String>();
		new Thread() {
			public void run() {
				try {
					ServerSocket ssocket = new ServerSocket(12345);
					Socket socket = ssocket.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line = in.readLine();
					while (null != line) {
						System.out.println(line);
						line = in.readLine();
						if ("".equals(line.trim())) {
							String body = in.readLine();
							map.put("body", body);
							OutputStream out = socket.getOutputStream();
							out.write("HTTP/1.1 200 OK\r\nDate: Thu, 26 Jan 2013 12:01:01 GMTServer: Apache\r\nAccept-Ranges: bytes\r\nContent-Length: 4\r\nConnection: close\r\nContent-Type: text/html; charset=ISO-8859-1\r\n\r\nTEST".getBytes());
							out.close();
							socket.close();
						}
					}
					ssocket.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		Thread.sleep(1000);

		String response = http.request("http://localhost:12345", "UTF-8", "PUT", "{\"src\":\"resp\"}\r\n", "Content-Type=application/json-rpc");
		assertEquals("{\"src\":\"resp\"}", map.get("body"));
	}
}
