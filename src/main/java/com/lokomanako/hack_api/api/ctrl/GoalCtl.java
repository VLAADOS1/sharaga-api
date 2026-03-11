package com.lokomanako.hack_api.api.ctrl;

import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.dto.goal.GoalReq;
import com.lokomanako.hack_api.api.dto.goal.GoalRes;
import com.lokomanako.hack_api.api.svc.GoalSvc;
import com.lokomanako.hack_api.api.util.CtxU;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/goal")
@Tag(name = "Р¦РµР»Рё", description = "Р¤РёРЅР°РЅСЃРѕРІС‹Рµ С†РµР»Рё РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ")
public class GoalCtl {

    @Autowired
    private GoalSvc goalSvc;

    @Autowired
    private CtxU ctxU;

    @GetMapping
    @Operation(summary = "РџРѕР»СѓС‡РёС‚СЊ СЃРїРёСЃРѕРє С†РµР»РµР№")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "РЎРїРёСЃРѕРє С†РµР»РµР№",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GoalRes.class)))),
            @ApiResponse(responseCode = "401", description = "РўСЂРµР±СѓРµС‚СЃСЏ access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public List<GoalRes> list() {
        return goalSvc.list(ctxU.uid());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "РЎРѕР·РґР°С‚СЊ С†РµР»СЊ")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Р¦РµР»СЊ СЃРѕР·РґР°РЅР°",
                    content = @Content(schema = @Schema(implementation = GoalRes.class))),
            @ApiResponse(responseCode = "400", description = "РћС€РёР±РєР° РІР°Р»РёРґР°С†РёРё",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "РўСЂРµР±СѓРµС‚СЃСЏ access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public GoalRes add(@Valid @RequestBody GoalReq req) {
        return goalSvc.add(ctxU.uid(), req);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "РћР±РЅРѕРІРёС‚СЊ С†РµР»СЊ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Р¦РµР»СЊ РѕР±РЅРѕРІР»РµРЅР°",
                    content = @Content(schema = @Schema(implementation = GoalRes.class))),
            @ApiResponse(responseCode = "400", description = "РћС€РёР±РєР° РІР°Р»РёРґР°С†РёРё",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "РўСЂРµР±СѓРµС‚СЃСЏ access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "404", description = "Р¦РµР»СЊ РЅРµ РЅР°Р№РґРµРЅР°",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public GoalRes patch(
            @Parameter(description = "ID цели") @PathVariable UUID id,
            @Valid @RequestBody GoalReq req
    ) {
        return goalSvc.patch(ctxU.uid(), id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "РЈРґР°Р»РёС‚СЊ С†РµР»СЊ")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Р¦РµР»СЊ СѓРґР°Р»РµРЅР°"),
            @ApiResponse(responseCode = "401", description = "РўСЂРµР±СѓРµС‚СЃСЏ access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "404", description = "Р¦РµР»СЊ РЅРµ РЅР°Р№РґРµРЅР°",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "409", description = "Р¦РµР»СЊ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ РІ С‚СЂР°РЅР·Р°РєС†РёСЏС…",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public void del(@Parameter(description = "ID цели") @PathVariable UUID id) {
        goalSvc.del(ctxU.uid(), id);
    }
}
