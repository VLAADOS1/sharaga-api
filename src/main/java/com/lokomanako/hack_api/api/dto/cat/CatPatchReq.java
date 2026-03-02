package com.lokomanako.hack_api.api.dto.cat;

import com.lokomanako.hack_api.store.ent.Kind;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос обновление")
public class CatPatchReq {

    @Schema(description = "Вид категории", example = "exp")
    private Kind kind;

    @Size(min = 2, max = 24)
    @Schema(description = "Название категории", example = "Транспорт")
    private String name;

    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$")
    @Schema(description = "HEX-цвет", example = "#FF5722")
    private String color;
}
