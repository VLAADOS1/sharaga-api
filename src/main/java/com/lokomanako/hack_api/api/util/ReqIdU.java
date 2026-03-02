package com.lokomanako.hack_api.api.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

public final class ReqIdU {

    private ReqIdU() {
    }

    public static String get(HttpServletRequest req) {
        Object v = req.getAttribute("rid");
        if (v != null) {
            return v.toString();
        }
        return UUID.randomUUID().toString();
    }
}
