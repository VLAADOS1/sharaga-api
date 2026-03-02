package com.lokomanako.hack_api.api.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Метаданные пагинации")
public class Meta {

    @Schema(description = "страница", example = "1")
    private Integer page;
    @Schema(description = "Размер", example = "20")
    private Integer limit;
    @Schema(description = "число записей", example = "153")
    private Long total;
}
