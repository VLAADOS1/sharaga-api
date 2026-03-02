package com.lokomanako.hack_api.api.util;

public final class PgU {

    private PgU() {
    }

    public static int p(Integer v) {
        if (v == null || v < 1) {
            return 1;
        }
        return v;
    }

    public static int l(Integer v) {
        if (v == null || v < 1) {
            return 20;
        }
        if (v > 100) {
            return 100;
        }
        return v;
    }
}
