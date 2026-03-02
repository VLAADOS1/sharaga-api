package com.lokomanako.hack_api.api.util;

import java.util.Locale;

public final class StrU {

    private StrU() {
    }

    public static String t(String v) {
        if (v == null) {
            return null;
        }
        return v.trim();
    }

    public static String n(String v) {
        String x = t(v);
        if (x == null) {
            return null;
        }
        return x.toLowerCase(Locale.ROOT);
    }

    public static String note(String v) {
        String x = t(v);
        if (x == null || x.isBlank()) {
            return null;
        }
        return x.replaceAll("\\p{Cntrl}", "");
    }
}
