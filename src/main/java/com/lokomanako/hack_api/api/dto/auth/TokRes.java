package com.lokomanako.hack_api.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "access token")
public class TokRes {

    @Schema(description = "JWT access token")
    private String accessToken;
    @Schema(description = "Тип", example = "Bearer")
    private String tokenType;
    @Schema(description = "Время жизни", example = "900")
    private Long expiresIn;
}
