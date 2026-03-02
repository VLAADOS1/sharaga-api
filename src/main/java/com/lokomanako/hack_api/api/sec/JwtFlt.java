package com.lokomanako.hack_api.api.sec;

import com.lokomanako.hack_api.store.ent.AppUsr;
import com.lokomanako.hack_api.store.repo.UsrRepo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFlt extends OncePerRequestFilter {

    @Autowired
    private JwtSvc jwtSvc;

    @Autowired
    private UsrRepo usrRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String tok = h.substring(7);
            try {
                Claims c = jwtSvc.parse(tok);
                String typ = c.get("typ", String.class);
                if ("acc".equals(typ) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UUID uid = UUID.fromString(c.getSubject());
                    AppUsr u = usrRepo.findById(uid).orElse(null);
                    if (u != null) {
                        AuthUsr au = new AuthUsr(u.getId(), u.getLoginNorm(), u.getPassHash());
                        UsernamePasswordAuthenticationToken at = new UsernamePasswordAuthenticationToken(
                                au, null, au.getAuthorities());
                        at.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(at);
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        fc.doFilter(req, res);
    }
}
