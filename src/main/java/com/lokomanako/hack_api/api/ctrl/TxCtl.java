package com.lokomanako.hack_api.api.ctrl;

import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.dto.common.PageRes;
import com.lokomanako.hack_api.api.dto.tx.TxPatchReq;
import com.lokomanako.hack_api.api.dto.tx.TxReq;
import com.lokomanako.hack_api.api.dto.tx.TxRes;
import com.lokomanako.hack_api.api.svc.TxSvc;
import com.lokomanako.hack_api.api.util.CtxU;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Транзакции", description = "Операции доходов и расходов")
public class TxCtl {

    @Autowired
    private TxSvc txSvc;

    @Autowired
    private CtxU ctxU;

    @GetMapping
    @Operation(summary = "Список транзакций")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список транзакций",
                    content = @Content(schema = @Schema(implementation = PageRes.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public PageRes<TxRes> list(
            @Parameter(description = "Тип операции")
            @RequestParam(required = false) String type,
            @Parameter(description = "ID категории")
            @RequestParam(required = false) UUID catId,
            @Parameter(description = "Сортировка")
            @RequestParam(required = false) String sort,
            @Parameter(description = "Дата начала периода")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @Parameter(description = "Дата конца периода")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @Parameter(description = "Номер страницы")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Размер страницы")
            @RequestParam(required = false) Integer limit
    ) {
        return txSvc.list(ctxU.uid(), type, catId, sort, dateFrom, dateTo, page, limit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать транзакцию")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Транзакция создана",
                    content = @Content(schema = @Schema(implementation = TxRes.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public TxRes add(@Valid @RequestBody TxReq req) {
        return txSvc.add(ctxU.uid(), req);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Изменить транзакцию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Транзакция обновлена",
                    content = @Content(schema = @Schema(implementation = TxRes.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public TxRes patch(
            @Parameter(description = "ID транзакции") @PathVariable UUID id,
            @Valid @RequestBody TxPatchReq req
    ) {
        return txSvc.patch(ctxU.uid(), id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить транзакцию")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Транзакция удалена"),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public void del(@Parameter(description = "ID транзакции") @PathVariable UUID id) {
        txSvc.del(ctxU.uid(), id);
    }
}
