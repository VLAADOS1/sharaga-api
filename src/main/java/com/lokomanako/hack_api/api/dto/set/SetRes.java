package com.lokomanako.hack_api.api.dto.set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Настройки")
public class SetRes {

    @Schema(description = "суммы в интерфейсе", example = "false")
    private Boolean hideAmounts;
}
