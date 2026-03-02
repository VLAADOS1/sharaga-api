package com.lokomanako.hack_api.api.sec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokomanako.hack_api.api.dto.common.ErrObj;
import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.util.ReqIdU;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class SecDny implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex)
            throws IOException, ServletException {
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrRes body = new ErrRes(new ErrObj("AUTH_FORBIDDEN", "Forbidden", Map.of(), ReqIdU.get(req)));
        new ObjectMapper().writeValue(res.getOutputStream(), body);
    }
}
