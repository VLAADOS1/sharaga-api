package com.lokomanako.hack_api.api.ctrl;

import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.dto.goal.GoalReq;
import com.lokomanako.hack_api.api.dto.goal.GoalRes;
import com.lokomanako.hack_api.api.svc.GoalSvc;
import com.lokomanako.hack_api.api.util.CtxU;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/goal")
@Tag(name = "Цель", description = "Финансовая цель пользователя")
public class GoalCtl {

    @Autowired
    private GoalSvc goalSvc;

    @Autowired
    private CtxU ctxU;

    @GetMapping
    @Operation(summary = "Получить цель")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Цель получена",
                    content = @Content(schema = @Schema(implementation = GoalRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public GoalRes get() {
        return goalSvc.get(ctxU.uid());
    }

    @PutMapping
    @Operation(summary = "Создать или обновить цель")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Цель сохранена",
                    content = @Content(schema = @Schema(implementation = GoalRes.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public GoalRes put(@Valid @RequestBody GoalReq req) {
        return goalSvc.put(ctxU.uid(), req);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить цель")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Цель удалена"),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public void del() {
        goalSvc.del(ctxU.uid());
    }
}
