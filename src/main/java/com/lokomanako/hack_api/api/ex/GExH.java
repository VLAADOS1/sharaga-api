package com.lokomanako.hack_api.api.ex;

import com.lokomanako.hack_api.api.dto.common.ErrObj;
import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.util.ReqIdU;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GExH {

    @ExceptionHandler(ApiEx.class)
    public ResponseEntity<ErrRes> hApi(ApiEx ex, HttpServletRequest req) {
        return ResponseEntity.status(ex.getSt()).body(new ErrRes(
                new ErrObj(ex.getCode(), ex.getMessage(), ex.getDet(), ReqIdU.get(req))
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrRes> hVal(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, Object> det = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            det.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ErrRes(
                new ErrObj("VALIDATION_ERR", "Validation failed", det, ReqIdU.get(req))
        ));
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrRes> hBad(Exception ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(new ErrRes(
                new ErrObj("BAD_REQUEST", "Bad request", Map.of(), ReqIdU.get(req))
        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrRes> hDb(DataIntegrityViolationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrRes(
                new ErrObj("DATA_CONFLICT", "Data conflict", Map.of(), ReqIdU.get(req))
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrRes> hAny(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrRes(
                new ErrObj("INTERNAL_ERR", "Internal server error", Map.of(), ReqIdU.get(req))
        ));
    }
}
