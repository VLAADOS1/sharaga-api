package com.lokomanako.hack_api.api.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "обработанных элементов")
public class CntRes {

    @Schema(description = "Количество созданных операций", example = "3")
    private Integer count;
}
