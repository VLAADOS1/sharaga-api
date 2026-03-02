package com.lokomanako.hack_api.api.ex;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiEx extends RuntimeException {

    private final HttpStatus st;
    private final String code;
    private final Map<String, Object> det;

    public ApiEx(HttpStatus st, String code, String msg) {
        super(msg);
        this.st = st;
        this.code = code;
        this.det = Map.of();
    }

    public ApiEx(HttpStatus st, String code, String msg, Map<String, Object> det) {
        super(msg);
        this.st = st;
        this.code = code;
        this.det = det == null ? Map.of() : det;
    }
}
