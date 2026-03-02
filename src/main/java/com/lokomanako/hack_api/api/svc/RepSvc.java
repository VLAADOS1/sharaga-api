package com.lokomanako.hack_api.api.svc;

import com.lokomanako.hack_api.api.dto.common.Meta;
import com.lokomanako.hack_api.api.dto.rep.CatSum;
import com.lokomanako.hack_api.api.dto.rep.RepListRes;
import com.lokomanako.hack_api.api.dto.rep.RepSumRes;
import com.lokomanako.hack_api.api.dto.tx.TxRes;
import com.lokomanako.hack_api.api.ex.ApiEx;
import com.lokomanako.hack_api.api.util.PgU;
import com.lokomanako.hack_api.store.ent.Kind;
import com.lokomanako.hack_api.store.ent.Tx;
import com.lokomanako.hack_api.store.repo.TxRepo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepSvc {

    @Autowired
    private TxRepo txRepo;

    @Transactional(readOnly = true)
    public RepSumRes sum(UUID uid, String type, UUID catId, LocalDate dFrom, LocalDate dTo) {
        Kind k = pKind(type);
        List<Tx> all = txRepo.findAllForRep(uid, k, catId, dFrom, dTo);
        Agg a = agg(all);
        return new RepSumRes(a.inSum, a.outSum, a.bal, a.byCat);
    }

    @Transactional(readOnly = true)
    public RepListRes list(UUID uid, String type, UUID catId, String sort, LocalDate dFrom, LocalDate dTo,
                           Integer page, Integer limit) {
        Kind k = pKind(type);
        int p = PgU.p(page);
        int l = PgU.l(limit);

        PageRequest pr = PageRequest.of(p - 1, l, s(sort));
        Page<Tx> pg = txRepo.findPage(uid, k, catId, dFrom, dTo, pr);
        List<Tx> all = txRepo.findAllForRep(uid, k, catId, dFrom, dTo);

        Agg a = agg(all);
        List<TxRes> list = pg.getContent().stream().map(this::map).toList();

        return new RepListRes(
                a.inSum,
                a.outSum,
                a.bal,
                a.byCat,
                list,
                new Meta(p, l, pg.getTotalElements())
        );
    }

    private Agg agg(List<Tx> all) {
        BigDecimal in = BigDecimal.ZERO;
        BigDecimal out = BigDecimal.ZERO;
        Map<UUID, CatSum> m = new LinkedHashMap<>();

        for (Tx t : all) {
            if (t.getType() == Kind.INC) {
                in = in.add(t.getSum());
            } else {
                out = out.add(t.getSum());
            }
            UUID cid = t.getCat().getId();
            CatSum cs = m.get(cid);
            if (cs == null) {
                cs = new CatSum(cid, t.getCat().getName(), t.getType(), BigDecimal.ZERO);
                m.put(cid, cs);
            }
            cs.setSum(cs.getSum().add(t.getSum()));
        }
        BigDecimal bal = in.subtract(out);
        return new Agg(in, out, bal, new ArrayList<>(m.values()));
    }

    private TxRes map(Tx t) {
        return new TxRes(
                t.getId(),
                t.getType(),
                t.getSum(),
                t.getNote(),
                t.getCat().getId(),
                t.getCat().getName(),
                t.getCat().getColor(),
                t.getDt()
        );
    }

    private Kind pKind(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return Kind.valueOf(v.trim().toUpperCase());
        } catch (Exception e) {
            throw new ApiEx(HttpStatus.BAD_REQUEST, "BAD_TYPE", "Invalid type");
        }
    }

    private Sort s(String v) {
        String x = v == null ? "new" : v.trim().toLowerCase();
        return switch (x) {
            case "old" -> Sort.by(Sort.Order.asc("dt"), Sort.Order.asc("id"));
            case "sum_desc" -> Sort.by(Sort.Order.desc("sum"), Sort.Order.desc("dt"));
            case "sum_asc" -> Sort.by(Sort.Order.asc("sum"), Sort.Order.desc("dt"));
            case "cat_asc" -> Sort.by(Sort.Order.asc("cat.name"), Sort.Order.desc("dt"));
            default -> Sort.by(Sort.Order.desc("dt"), Sort.Order.desc("id"));
        };
    }

    private record Agg(BigDecimal inSum, BigDecimal outSum, BigDecimal bal, List<CatSum> byCat) {
    }
}
