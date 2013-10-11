package org.ukiuni.pacifista;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class TestLocal {
	@Test
	public void testReplaceLine() throws IOException {
		String targetFileName = "testData/TestLocal.replaceTest.txt";
		File file = new File(targetFileName);
		PrintWriter out = new PrintWriter(new FileWriter(file));
		out.println("test non replace line1");
		out.println("test replace line");
		out.println("test non replace line2");
		out.println("test replace line");
		out.close();
		new Local(new File("."), new Runtime(new File("."), new File("templates"), new File("plugins"), new HashMap<String, Object>())).replaceLine(targetFileName, "test replace line", "replaced");
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		Assert.assertEquals("test non replace line1", in.readLine());
		Assert.assertEquals("replaced", in.readLine());
		Assert.assertEquals("test non replace line2", in.readLine());
		Assert.assertEquals("replaced", in.readLine());
		Assert.assertNull(in.readLine());
		in.close();
		file.delete();
	}
}
