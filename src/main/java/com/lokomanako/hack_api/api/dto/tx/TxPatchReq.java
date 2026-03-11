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
@Schema(description = "РѕР±РЅРѕРІР»РµРЅРёРµ С‚СЂР°РЅР·Р°РєС†РёРё")
public class TxPatchReq {

    @Schema(description = "РўРёРї", example = "inc")
    private Kind type;

    @DecimalMin(value = "0.01")
    @Schema(description = "РЎСѓРјРјР°", example = "5000.00")
    private BigDecimal sum;

    @Size(max = 80)
    @Schema(description = "РљРѕРјРјРµРЅС‚Р°СЂРёР№", example = "Р—Р°СЂРїР»Р°С‚Р°")
    private String note;

    @Schema(description = "ID категории")
    private UUID catId;

    @Schema(description = "ID цели")
    private UUID goalId;

    @Schema(description = "Р”Р°С‚Р° РѕРїРµСЂР°С†РёРё", example = "2026-03-03")
    private LocalDate date;
}
