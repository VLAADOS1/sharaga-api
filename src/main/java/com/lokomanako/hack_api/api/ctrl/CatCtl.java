package com.lokomanako.hack_api.api.ctrl;

import com.lokomanako.hack_api.api.dto.cat.CatPatchReq;
import com.lokomanako.hack_api.api.dto.cat.CatReq;
import com.lokomanako.hack_api.api.dto.cat.CatRes;
import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.dto.common.Meta;
import com.lokomanako.hack_api.api.dto.common.PageRes;
import com.lokomanako.hack_api.api.svc.CatSvc;
import com.lokomanako.hack_api.api.util.CtxU;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/categories")
@Tag(name = "Категории", description = "Категории доходов и расходов")
public class CatCtl {

    @Autowired
    private CatSvc catSvc;

    @Autowired
    private CtxU ctxU;

    @GetMapping
    @Operation(summary = "Список категорий")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список категорий",
                    content = @Content(schema = @Schema(implementation = PageRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public PageRes<CatRes> list() {
        List<CatRes> items = catSvc.list(ctxU.uid());
        return new PageRes<>(items, new Meta(1, items.size(), (long) items.size()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать категорию")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Категория создана",
                    content = @Content(schema = @Schema(implementation = CatRes.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "409", description = "Категория с таким name уже существует",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public CatRes add(@Valid @RequestBody CatReq req) {
        return catSvc.add(ctxU.uid(), req);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Изменить категорию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория обновлена",
                    content = @Content(schema = @Schema(implementation = CatRes.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "404", description = "Категория не найдена",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "409", description = "Конфликт уникальности",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public CatRes patch(@PathVariable UUID id, @Valid @RequestBody CatPatchReq req) {
        return catSvc.patch(ctxU.uid(), id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить категорию")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Категория удалена"),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "404", description = "Категория не найдена",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "409", description = "Категория используется и не может быть удалена",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public void del(@PathVariable UUID id) {
        catSvc.del(ctxU.uid(), id);
    }
}
