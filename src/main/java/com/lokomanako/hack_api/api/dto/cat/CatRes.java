package com.lokomanako.hack_api.api.dto.cat;

import com.lokomanako.hack_api.store.ent.Kind;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Категория")
public class CatRes {

    @Schema(description = "ID категории")
    private UUID id;
    @Schema(description = "Вид категории", example = "exp")
    private Kind kind;
    @Schema(description = "Название", example = "Еда")
    private String name;
    @Schema(description = "HEX-цвет", example = "#4CAF50")
    private String color;
}
