package com.lokomanako.hack_api.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на восстановление пароля")
public class PwdRecReq {

    @NotBlank
    @Email
    @Size(max = 120)
    @Schema(description = "Email", example = "user@example.com")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Ответ на вопрос 1", example = "Барсик")
    private String securityAnswer1;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Ответ на вопрос 2", example = "Иван")
    private String securityAnswer2;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Ответ на вопрос 3", example = "Синий")
    private String securityAnswer3;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,40}$")
    @Schema(description = "Новый пароль", example = "NewPass123")
    private String newPassword;

    @NotBlank
    @Schema(description = "Повтор нового пароля", example = "NewPass123")
    private String newPasswordConfirm;
}
