package com.lokomanako.hack_api.api.dto.rec;

import com.lokomanako.hack_api.store.ent.Freq;
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
@Schema(description = "повторяющейся операции")
public class RecRes {

    @Schema(description = "ID правила")
    private UUID id;

    @Schema(description = "Тип операции", example = "exp")
    private Kind type;

    @Schema(description = "Сумма", example = "500.00")
    private BigDecimal sum;

    @Schema(description = "Комментарий")
    private String note;

    @Schema(description = "ID категории")
    private UUID catId;

    @Schema(description = "Название категории")
    private String catName;

    @Schema(description = "Цвет категории")
    private String catColor;

    @Schema(description = "Период повтора", example = "month")
    private Freq freq;

    @Schema(description = "Дата начала", example = "2026-03-10")
    private LocalDate startDate;

    @Schema(description = "дата выполнения", example = "2026-04-10")
    private LocalDate nextDate;
}
