package com.lokomanako.hack_api.api.sec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokomanako.hack_api.api.config.AuthCfg;
import com.lokomanako.hack_api.api.dto.common.ErrObj;
import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.util.ReqIdU;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthRlFlt extends OncePerRequestFilter {

    @Autowired
    private AuthCfg cfg;

    private final ConcurrentHashMap<String, Slot> map = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws ServletException, IOException {
        String p = req.getRequestURI();
        if (!p.startsWith("/api/v1/auth")) {
            fc.doFilter(req, res);
            return;
        }
        long now = System.currentTimeMillis() / 1000;
        String ip = ip(req);
        String key = ip + ":" + p;
        Slot s = map.computeIfAbsent(key, k -> new Slot(now + cfg.getRlSec()));
        boolean over;
        synchronized (s) {
            if (now > s.till) {
                s.till = now + cfg.getRlSec();
                s.cnt = 0;
            }
            s.cnt++;
            over = s.cnt > cfg.getRlMax();
        }
        if (over) {
            res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ErrRes body = new ErrRes(new ErrObj("AUTH_RATE_LIMIT", "Too many requests", Map.of(), ReqIdU.get(req)));
            new ObjectMapper().writeValue(res.getOutputStream(), body);
            return;
        }
        fc.doFilter(req, res);
    }

    private String ip(HttpServletRequest req) {
        String x = req.getHeader("X-Forwarded-For");
        if (x != null && !x.isBlank()) {
            return x.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    private static class Slot {

        long till;
        int cnt;

        Slot(long till) {
            this.till = till;
            this.cnt = 0;
        }
    }
}
