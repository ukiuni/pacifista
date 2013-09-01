package org.ukiuni.pacifista.util;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TestScriptUtil {
	@Test
	public void testPicupParameter() {
		String url = "script.js?test1=param1&test2=param2";
		Map<String, String> paramMap = ScriptingUtil.pickupParameters(url);
		Assert.assertEquals(2, paramMap.size());
		Assert.assertEquals("param1", paramMap.get("test1"));
		Assert.assertEquals("param2", paramMap.get("test2"));
		
	}
}
