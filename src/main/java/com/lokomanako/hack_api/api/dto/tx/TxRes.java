package com.lokomanako.hack_api.api.dto.tx;

import com.lokomanako.hack_api.store.ent.Kind;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Транзакция")
public class TxRes {

    @Schema(description = "ID транзакции")
    private UUID id;
    @Schema(description = "Тип операции", example = "exp")
    private Kind type;
    @Schema(description = "Сумма операции", example = "1200.50")
    private BigDecimal sum;
    @Schema(description = "Комментарий", example = "Продукты")
    private String note;
    @Schema(description = "ID категории")
    private UUID catId;
    @Schema(description = "Название категории", example = "Еда")
    private String catName;
    @Schema(description = "Цвет категории", example = "#4CAF50")
    private String catColor;
    @Schema(description = "Дата операции", example = "2026-03-03")
    private LocalDate date;
}
