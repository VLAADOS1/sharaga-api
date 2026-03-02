package com.lokomanako.hack_api.api.dto.rec;

import com.lokomanako.hack_api.store.ent.Freq;
import com.lokomanako.hack_api.store.ent.Kind;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(description = "создание повтора")
public class RecReq {

    @NotNull
    @Schema(description = "Тип операции", example = "exp")
    private Kind type;

    @NotNull
    @DecimalMin(value = "0.01")
    @Schema(description = "Сумма операции", example = "500")
    private BigDecimal sum;

    @Size(max = 80)
    @Schema(description = "Комментарий", example = "Абонемент")
    private String note;

    @NotNull
    @Schema(description = "ID категории")
    private UUID catId;

    @NotNull
    @Schema(description = "Период повтора", example = "month")
    private Freq freq;

    @NotNull
    @Schema(description = "Дата старта", example = "2026-03-10")
    private LocalDate startDate;
}
