package com.cubeia.poker.util;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class ThreadLocalProfilerTest {

	@Test
	public void testProfiler() {
		ThreadLocalProfiler.start();
		Tester tester = new Tester();
		tester.a();
		tester.b();
		ThreadLocalProfiler.stop();
		
		Map<String, Long> map = ThreadLocalProfiler.get();
		Assert.assertEquals(4, map.size());
		Assert.assertNotNull(map.get("start"));
		Assert.assertNotNull(map.get("a"));
		Assert.assertNotNull(map.get("b"));
		
		ThreadLocalProfiler.clear();
		Assert.assertNull(ThreadLocalProfiler.get());
	}
	
	
	private static class Tester {
		
		public void a() {
			ThreadLocalProfiler.add("a");
		}
		
		public void b() {
			ThreadLocalProfiler.add("b");
		}
	}
}


