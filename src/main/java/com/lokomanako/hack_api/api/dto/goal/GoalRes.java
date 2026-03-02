package com.lokomanako.hack_api.api.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Цель")
public class GoalRes {

    @Schema(description = "Название цели", example = "Отпуск")
    private String name;
    @Schema(description = "Текущий баланс", example = "42000.00")
    private BigDecimal current;
    @Schema(description = "Целевая сумма", example = "150000.00")
    private BigDecimal target;
    @Schema(description = "Процент прогресса", example = "28.00")
    private BigDecimal progressPercent;
}
