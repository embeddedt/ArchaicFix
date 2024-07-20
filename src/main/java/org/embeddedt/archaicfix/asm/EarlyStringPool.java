package org.embeddedt.archaicfix.asm;

import java.util.HashMap;
import java.util.function.Function;

public class EarlyStringPool {
    private static final HashMap<String, String> POOL = new HashMap<>();

    public static String canonicalize(String str) {
        synchronized (POOL) {
            return POOL.computeIfAbsent(str, Function.identity());
        }
    }

    public static void clear() {
        synchronized (POOL) {
            POOL.clear();
        }
    }
}
