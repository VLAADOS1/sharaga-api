package com.lokomanako.hack_api.api.dto.rep;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "отчет")
public class RepSumRes {

    @Schema(description = "доходов", example = "150000.00")
    private BigDecimal inSum;
    @Schema(description = "расходов", example = "48000.00")
    private BigDecimal outSum;
    @Schema(description = "Баланс", example = "102000.00")
    private BigDecimal bal;
    @Schema(description = "по категориям")
    private List<CatSum> byCat;
}
