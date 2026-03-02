package com.lokomanako.hack_api.api.dto.rep;

import com.lokomanako.hack_api.api.dto.common.Meta;
import com.lokomanako.hack_api.api.dto.tx.TxRes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Отчет")
public class RepListRes {

    @Schema(description = "Сумма доходов", example = "150000.00")
    private BigDecimal inSum;
    @Schema(description = "Сумма расходов", example = "48000.00")
    private BigDecimal outSum;
    @Schema(description = "Баланс", example = "102000.00")
    private BigDecimal bal;
    @Schema(description = "Разбивка")
    private List<CatSum> byCat;
    @Schema(description = "Список")
    private List<TxRes> list;
    @Schema(description = "данны")
    private Meta meta;
}
