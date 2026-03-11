package com.lokomanako.hack_api.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Контрольные вопросы для восстановления")
public class SecQRes {

    @Schema(description = "Вопрос 1")
    private String question1;

    @Schema(description = "Вопрос 2")
    private String question2;

    @Schema(description = "Вопрос 3")
    private String question3;
}
