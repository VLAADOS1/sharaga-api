package com.lokomanako.hack_api.api.dto.tx;

import com.lokomanako.hack_api.store.ent.Kind;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(description = "обновление транзакции")
public class TxPatchReq {

    @Schema(description = "Тип", example = "inc")
    private Kind type;

    @DecimalMin(value = "0.01")
    @Schema(description = "Сумма", example = "5000.00")
    private BigDecimal sum;

    @Size(max = 80)
    @Schema(description = "Комментарий", example = "Зарплата")
    private String note;

    @Schema(description = "ID")
    private UUID catId;

    @Schema(description = "Дата операции", example = "2026-03-03")
    private LocalDate date;
}
