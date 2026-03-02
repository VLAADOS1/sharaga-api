package com.lokomanako.hack_api.api.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "объект ошибки")
public class ErrObj {

    @Schema(description = "код ошибки", example = "AUTH_BAD_CRED")
    private String code;
    @Schema(description = "сообщение", example = "Invalid login or password")
    private String message;
    @Schema(description = "детали ошибки")
    private Map<String, Object> details;
    @Schema(description = "Идентификатор", example = "f05c7ce1-5db6-40ba-a53a-ec6bdbf7eaf3")
    private String requestId;
}
