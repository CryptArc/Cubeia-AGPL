package com.cubeia.poker.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ThreadLocalProfiler {

    private static ThreadLocal<Map<String, Long>> calls = new ThreadLocal<Map<String, Long>>();

    public static void start() {
        calls.set(new LinkedHashMap<String, Long>());
        add("start");
    }

    public static void stop() {
        add("stop");
    }

    public static void clear() {
        calls.remove();
    }

    public static Map<String, Long> get() {
        return calls.get();
    }

    public static void add(String method) {
        if (calls.get() != null) {
            calls.get().put(method, System.currentTimeMillis());
        }
    }

    public static String getCallStackAsString() {
        Map<String, Long> map = calls.get();
        if (map == null) {
            return "No calls recorded, thread local is null";
        }

        Long start = map.get("start");

        String result = "Profiled Calls:\n";
        for (String key : map.keySet()) {
            Long time = map.get(key);
            Long timeSinceStart = time - start;
            result += "\t" + key + "\t" + timeSinceStart + "\n";
        }

        return result;
    }

}
