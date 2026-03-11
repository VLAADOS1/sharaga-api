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
@Schema(description = "РўСЂР°РЅР·Р°РєС†РёСЏ")
public class TxRes {

    @Schema(description = "ID С‚СЂР°РЅР·Р°РєС†РёРё")
    private UUID id;

    @Schema(description = "РўРёРї РѕРїРµСЂР°С†РёРё", example = "exp")
    private Kind type;

    @Schema(description = "РЎСѓРјРјР° РѕРїРµСЂР°С†РёРё", example = "1200.50")
    private BigDecimal sum;

    @Schema(description = "РљРѕРјРјРµРЅС‚Р°СЂРёР№", example = "РџСЂРѕРґСѓРєС‚С‹")
    private String note;

    @Schema(description = "ID РєР°С‚РµРіРѕСЂРёРё")
    private UUID catId;

    @Schema(description = "РќР°Р·РІР°РЅРёРµ РєР°С‚РµРіРѕСЂРёРё", example = "Р•РґР°")
    private String catName;

    @Schema(description = "Р¦РІРµС‚ РєР°С‚РµРіРѕСЂРёРё", example = "#4CAF50")
    private String catColor;

    @Schema(description = "ID цели")
    private UUID goalId;

    @Schema(description = "Название цели")
    private String goalName;

    @Schema(description = "Р”Р°С‚Р° РѕРїРµСЂР°С†РёРё", example = "2026-03-03")
    private LocalDate date;
}
