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
@Schema(description = "СЃРѕР·РґР°РЅРёРµ С‚СЂР°РЅР·Р°РєС†РёРё")
public class TxReq {

    @NotNull
    @Schema(description = "РўРёРї РѕРїРµСЂР°С†РёРё", example = "exp")
    private Kind type;

    @NotNull
    @DecimalMin(value = "0.01")
    @Schema(description = "РЎСѓРјРјР°", example = "1200.50")
    private BigDecimal sum;

    @Size(max = 80)
    @Schema(description = "РљРѕРјРјРµРЅС‚Р°СЂРёР№", example = "РџСЂРѕРґСѓРєС‚С‹")
    private String note;

    @NotNull
    @Schema(description = "ID категории")
    private UUID catId;

    @NotNull
    @Schema(description = "ID цели")
    private UUID goalId;

    @NotNull
    @Schema(description = "Р”Р°С‚Р° РѕРїРµСЂР°С†РёРё", example = "2026-03-03")
    private LocalDate date;
}
