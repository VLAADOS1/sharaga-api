package com.lokomanako.hack_api.api.dto.set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "обновление настроек")
public class SetReq {

    @NotNull
    @Schema(description = "Скрывать суммы", example = "false")
    private Boolean hideAmounts;
}
