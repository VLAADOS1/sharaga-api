package com.lokomanako.hack_api.api.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Р¦РµР»СЊ")
public class GoalRes {

    @Schema(description = "ID цели")
    private UUID id;

    @Schema(description = "РќР°Р·РІР°РЅРёРµ С†РµР»Рё", example = "РћС‚РїСѓСЃРє")
    private String name;

    @Schema(description = "РўРµРєСѓС‰РёР№ Р±Р°Р»Р°РЅСЃ", example = "42000.00")
    private BigDecimal current;

    @Schema(description = "Р¦РµР»РµРІР°СЏ СЃСѓРјРјР°", example = "150000.00")
    private BigDecimal target;

    @Schema(description = "РџСЂРѕС†РµРЅС‚ РїСЂРѕРіСЂРµСЃСЃР°", example = "28.00")
    private BigDecimal progressPercent;
}
