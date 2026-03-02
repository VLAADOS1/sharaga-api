package com.lokomanako.hack_api.api.ctrl;

import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.dto.rep.RepListRes;
import com.lokomanako.hack_api.api.dto.rep.RepSumRes;
import com.lokomanako.hack_api.api.svc.RepSvc;
import com.lokomanako.hack_api.api.util.CtxU;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Отчеты", description = "Отчеты")
public class RepCtl {

    @Autowired
    private RepSvc repSvc;

    @Autowired
    private CtxU ctxU;

    @GetMapping("/summary")
    @Operation(summary = "Сводный отчет")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сводка сформирована",
                    content = @Content(schema = @Schema(implementation = RepSumRes.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public RepSumRes sum(
            @Parameter(description = "Дата начала периода, формат YYYY-MM-DD")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @Parameter(description = "Дата конца периода, формат YYYY-MM-DD")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @Parameter(description = "ID категории")
            @RequestParam(required = false) UUID catId,
            @Parameter(description = "Тип: inc или exp")
            @RequestParam(required = false) String type
    ) {
        return repSvc.sum(ctxU.uid(), type, catId, dateFrom, dateTo);
    }

    @GetMapping("/list")
    @Operation(summary = "Отчет списком")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отчет сформирован",
                    content = @Content(schema = @Schema(implementation = RepListRes.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public RepListRes list(
            @Parameter(description = "Дата начала периода, формат YYYY-MM-DD")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @Parameter(description = "Дата конца периода, формат YYYY-MM-DD")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @Parameter(description = "ID категории")
            @RequestParam(required = false) UUID catId,
            @Parameter(description = "Тип: inc или exp")
            @RequestParam(required = false) String type,
            @Parameter(description = "Сортировка: new|old|sum_desc|sum_asc|cat_asc")
            @RequestParam(required = false) String sort,
            @Parameter(description = "Номер страницы, начиная с 1")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Размер страницы, максимум 100")
            @RequestParam(required = false) Integer limit
    ) {
        return repSvc.list(ctxU.uid(), type, catId, sort, dateFrom, dateTo, page, limit);
    }
}
