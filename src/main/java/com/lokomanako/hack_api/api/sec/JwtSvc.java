package com.lokomanako.hack_api.api.sec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lokomanako.hack_api.api.config.JwtCfg;

@Service
public class JwtSvc {

    @Autowired
    private JwtCfg cfg;

    public String mkAcc(UUID uid, String login) {
        Instant now = Instant.now();
        Instant exp = now.plus(cfg.getAccessMin(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(uid.toString())
                .claim("typ", "acc")
                .claim("login", login)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key())
                .compact();
    }

    public String mkRef(UUID uid, UUID tid) {
        Instant now = Instant.now();
        Instant exp = now.plus(cfg.getRefreshDays(), ChronoUnit.DAYS);
        return Jwts.builder()
                .subject(uid.toString())
                .id(tid.toString())
                .claim("typ", "ref")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key())
                .compact();
    }

    public Claims parse(String tok) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(tok)
                .getPayload();
    }

    public long accSec() {
        return cfg.getAccessMin() * 60L;
    }

    public Instant refExp() {
        return Instant.now().plus(cfg.getRefreshDays(), ChronoUnit.DAYS);
    }

    public long refSec() {
        return cfg.getRefreshDays() * 24L * 60L * 60L;
    }

    private SecretKey key() {
        byte[] raw = cfg.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(raw);
    }
}
