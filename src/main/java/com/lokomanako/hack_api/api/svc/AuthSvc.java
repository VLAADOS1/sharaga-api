package com.lokomanako.hack_api.api.svc;

import com.lokomanako.hack_api.api.dto.auth.AuthPack;
import com.lokomanako.hack_api.api.dto.auth.LogReq;
import com.lokomanako.hack_api.api.dto.auth.MeRes;
import com.lokomanako.hack_api.api.dto.auth.RegReq;
import com.lokomanako.hack_api.api.ex.ApiEx;
import com.lokomanako.hack_api.api.sec.JwtSvc;
import com.lokomanako.hack_api.api.util.StrU;
import com.lokomanako.hack_api.store.ent.AppUsr;
import com.lokomanako.hack_api.store.ent.Rtok;
import com.lokomanako.hack_api.store.ent.UsrSet;
import com.lokomanako.hack_api.store.repo.RtokRepo;
import com.lokomanako.hack_api.store.repo.SetRepo;
import com.lokomanako.hack_api.store.repo.UsrRepo;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthSvc {

    @Autowired
    private UsrRepo usrRepo;

    @Autowired
    private SetRepo setRepo;

    @Autowired
    private RtokRepo rtokRepo;

    @Autowired
    private JwtSvc jwtSvc;

    @Autowired
    private PasswordEncoder pe;

    @Autowired
    private DefCatSvc defCatSvc;

    @Transactional
    public AuthPack reg(RegReq req) {
        String login = StrU.t(req.getLogin());
        String norm = StrU.n(req.getLogin());
        if (!req.getPassword().equals(req.getPasswordConfirm())) {
            throw new ApiEx(HttpStatus.BAD_REQUEST, "AUTH_PASS_CONFIRM", "Password confirm mismatch");
        }
        if (usrRepo.existsByLoginNorm(norm)) {
            throw new ApiEx(HttpStatus.CONFLICT, "AUTH_LOGIN_EXISTS", "Login already exists");
        }
        AppUsr u = new AppUsr();
        u.setLogin(login);
        u.setLoginNorm(norm);
        u.setPassHash(pe.encode(req.getPassword()));
        u.setFailCnt(0);
        usrRepo.save(u);

        UsrSet s = new UsrSet();
        s.setUsr(u);
        s.setHideAmt(false);
        s.setTz("UTC");
        setRepo.save(s);
        defCatSvc.runOne(u.getId());

        return pack(u);
    }

    @Transactional(noRollbackFor = ApiEx.class)
    public AuthPack log(LogReq req) {
        String norm = StrU.n(req.getLogin());
        AppUsr u = usrRepo.findByLoginNorm(norm).orElse(null);
        if (u == null) {
            throw new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_BAD_CRED", "Invalid login or password");
        }
        Instant now = Instant.now();
        if (u.getLockTill() != null && u.getLockTill().isAfter(now)) {
            throw new ApiEx(HttpStatus.LOCKED, "AUTH_LOCKED", "Too many failed attempts", Map.of(
                    "lockUntil", u.getLockTill().toString()
            ));
        }
        if (u.getLockTill() != null && u.getLockTill().isBefore(now)) {
            u.setLockTill(null);
            u.setFailCnt(0);
        }
        if (!pe.matches(req.getPassword(), u.getPassHash())) {
            int c = u.getFailCnt() + 1;
            if (c >= 5) {
                u.setFailCnt(0);
                u.setLockTill(now.plus(15, ChronoUnit.MINUTES));
            } else {
                u.setFailCnt(c);
            }
            usrRepo.save(u);
            throw new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_BAD_CRED", "Invalid login or password");
        }
        u.setFailCnt(0);
        u.setLockTill(null);
        usrRepo.save(u);
        return pack(u);
    }

    @Transactional
    public AuthPack ref(String tok) {
        if (tok == null || tok.isBlank()) {
            throw new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_BAD_REFRESH", "Invalid refresh token");
        }
        Claims c;
        try {
            c = jwtSvc.parse(tok);
        } catch (Exception e) {
            throw new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_BAD_REFRESH", "Invalid refresh token");
        }
        String typ = c.get("typ", String.class);
        if (!"ref".equals(typ)) {
            throw new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_BAD_REFRESH", "Invalid refresh token");
        }
        UUID uid = UUID.fromString(c.getSubject());
        UUID tid = UUID.fromString(c.getId());
        Rtok r = rtokRepo.findByIdAndRevFalse(tid).orElse(null);
        if (r == null || !r.getUsr().getId().equals(uid) || r.getExpAt().isBefore(Instant.now())) {
            throw new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_BAD_REFRESH", "Invalid refresh token");
        }
        r.setRev(true);
        rtokRepo.save(r);

        AppUsr u = usrRepo.findById(uid).orElseThrow(() ->
                new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_BAD_REFRESH", "Invalid refresh token")
        );
        return pack(u);
    }

    @Transactional
    public void out(String tok) {
        if (tok == null || tok.isBlank()) {
            return;
        }
        try {
            Claims c = jwtSvc.parse(tok);
            if (!"ref".equals(c.get("typ", String.class))) {
                return;
            }
            UUID uid = UUID.fromString(c.getSubject());
            UUID tid = UUID.fromString(c.getId());
            Rtok r = rtokRepo.findByIdAndUsr_Id(tid, uid).orElse(null);
            if (r != null) {
                r.setRev(true);
                rtokRepo.save(r);
            }
        } catch (Exception e) {
        }
    }

    @Transactional(readOnly = true)
    public MeRes me(UUID uid) {
        AppUsr u = usrRepo.findById(uid).orElseThrow(() ->
                new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Unauthorized")
        );
        return new MeRes(u.getId().toString(), u.getLogin());
    }

    private AuthPack pack(AppUsr u) {
        rtokRepo.deleteByExpAtBefore(Instant.now());
        UUID tid = UUID.randomUUID();
        Rtok r = new Rtok();
        r.setId(tid);
        r.setUsr(u);
        r.setExpAt(jwtSvc.refExp());
        r.setRev(false);
        rtokRepo.save(r);

        String acc = jwtSvc.mkAcc(u.getId(), u.getLoginNorm());
        String ref = jwtSvc.mkRef(u.getId(), tid);
        return new AuthPack(acc, "Bearer", jwtSvc.accSec(), ref, jwtSvc.refSec());
    }
}
