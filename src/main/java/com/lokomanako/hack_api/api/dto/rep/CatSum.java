package com.lokomanako.hack_api.api.dto.rep;

import com.lokomanako.hack_api.store.ent.Kind;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "по категории")
public class CatSum {

    @Schema(description = "ID")
    private UUID catId;
    @Schema(description = "Название")
    private String catName;
    @Schema(description = "Тип", example = "exp")
    private Kind type;
    @Schema(description = "Сумма", example = "7600.00")
    private BigDecimal sum;
}
