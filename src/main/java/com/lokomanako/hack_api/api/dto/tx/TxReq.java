package com.lokomanako.hack_api.api.dto.tx;

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
@Schema(description = "создание транзакции")
public class TxReq {

    @NotNull
    @Schema(description = "Тип операции", example = "exp")
    private Kind type;

    @NotNull
    @DecimalMin(value = "0.01")
    @Schema(description = "Сумма", example = "1200.50")
    private BigDecimal sum;

    @Size(max = 80)
    @Schema(description = "Комментарий", example = "Продукты")
    private String note;

    @NotNull
    @Schema(description = "ID")
    private UUID catId;

    @NotNull
    @Schema(description = "Дата операции", example = "2026-03-03")
    private LocalDate date;
}
