package com.lokomanako.hack_api.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Профиль текущего пользователя")
public class MeRes {

    @Schema(description = "ID пользователя", example = "3b3ec6ef-0605-42ec-8be5-86a46c87c4fa")
    private String id;
    @Schema(description = "Логин", example = "adm")
    private String login;
}
