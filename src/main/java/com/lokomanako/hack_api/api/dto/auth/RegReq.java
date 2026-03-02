package com.lokomanako.hack_api.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию")
public class RegReq {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,18}$")
    @Schema(description = "Логин", example = "adm")
    private String login;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,40}$")
    @Schema(description = "Пароль (6-40, минимум 1 буква и 1 цифра)", example = "Pass123")
    private String password;

    @NotBlank
    @Schema(description = "Повтор пароля", example = "Pass123")
    private String passwordConfirm;
}
