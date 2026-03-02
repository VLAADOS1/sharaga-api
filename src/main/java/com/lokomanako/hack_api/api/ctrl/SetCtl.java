package com.lokomanako.hack_api.api.ctrl;

import com.lokomanako.hack_api.api.dto.set.SetReq;
import com.lokomanako.hack_api.api.dto.set.SetRes;
import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.svc.SetSvc;
import com.lokomanako.hack_api.api.util.CtxU;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settings")
@Tag(name = "Настройки", description = "Пользовательские настройки интерфейса")
public class SetCtl {

    @Autowired
    private SetSvc setSvc;

    @Autowired
    private CtxU ctxU;

    @GetMapping
    @Operation(summary = "Получить настройки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Настройки получены",
                    content = @Content(schema = @Schema(implementation = SetRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public SetRes get() {
        return setSvc.get(ctxU.uid());
    }

    @PatchMapping
    @Operation(summary = "Обновить настройки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Настройки обновлены",
                    content = @Content(schema = @Schema(implementation = SetRes.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public SetRes patch(@Valid @RequestBody SetReq req) {
        return setSvc.patch(ctxU.uid(), req);
    }
}
