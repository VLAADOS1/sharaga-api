package com.lokomanako.hack_api.api.util;

import com.lokomanako.hack_api.api.ex.ApiEx;
import com.lokomanako.hack_api.api.sec.AuthUsr;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CtxU {

    public UUID uid() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !(a.getPrincipal() instanceof AuthUsr au)) {
            throw new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Unauthorized");
        }
        return au.getId();
    }
}
