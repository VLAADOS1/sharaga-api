package com.lokomanako.hack_api.api.ctrl;

import com.lokomanako.hack_api.api.dto.common.CntRes;
import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.dto.common.Meta;
import com.lokomanako.hack_api.api.dto.common.PageRes;
import com.lokomanako.hack_api.api.dto.rec.RecReq;
import com.lokomanako.hack_api.api.dto.rec.RecRes;
import com.lokomanako.hack_api.api.svc.RecSvc;
import com.lokomanako.hack_api.api.util.CtxU;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recurring")
@Tag(name = "Повторы", description = "Повторяющиеся операции")
public class RecCtl {

    @Autowired
    private RecSvc recSvc;

    @Autowired
    private CtxU ctxU;

    @GetMapping
    @Operation(summary = "Список повторов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список повторов",
                    content = @Content(schema = @Schema(implementation = PageRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public PageRes<RecRes> list() {
        List<RecRes> items = recSvc.list(ctxU.uid());
        return new PageRes<>(items, new Meta(1, items.size(), (long) items.size()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать повтор")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Правило создано",
                    content = @Content(schema = @Schema(implementation = RecRes.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или бизнес-правил",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public RecRes add(@Valid @RequestBody RecReq req) {
        return recSvc.add(ctxU.uid(), req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить повтор")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Правило удалено"),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "404", description = "Правило не найдено",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public void del(@Parameter(description = "ID правила повтора") @PathVariable UUID id) {
        recSvc.del(ctxU.uid(), id);
    }

    @PostMapping("/run")
    @Operation(summary = "Запустить повторы")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Повторы обработаны",
                    content = @Content(schema = @Schema(implementation = CntRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public CntRes run() {
        return new CntRes(recSvc.run(ctxU.uid()));
    }
}
