package com.lokomanako.hack_api.api.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ответ для списков")
public class PageRes<T> {

    @Schema(description = "Элементы списка")
    private List<T> items;
    @Schema(description = "данные")
    private Meta meta;
}
