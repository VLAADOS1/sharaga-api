package com.lokomanako.hack_api.api.dto.cat;

import com.lokomanako.hack_api.store.ent.Kind;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание категории")
public class CatReq {

    @NotNull
    @Schema(description = "Вид категории", example = "exp")
    private Kind kind;

    @NotBlank
    @Size(min = 2, max = 24)
    @Schema(description = "Название категории", example = "Еда")
    private String name;

    @NotBlank
    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$")
    @Schema(description = "HEX-цвет", example = "#4CAF50")
    private String color;
}
