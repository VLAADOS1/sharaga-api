package com.lokomanako.hack_api.api.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "цели")
public class GoalReq {

    @NotBlank
    @Size(min = 2, max = 32)
    @Schema(description = "Название цели", example = "Отпуск")
    private String name;

    @NotNull
    @DecimalMin(value = "0.01")
    @Schema(description = "Целевая сумма", example = "150000")
    private BigDecimal target;
}
