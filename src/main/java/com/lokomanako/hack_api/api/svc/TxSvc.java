package com.lokomanako.hack_api.api.svc;

import com.lokomanako.hack_api.api.dto.common.Meta;
import com.lokomanako.hack_api.api.dto.common.PageRes;
import com.lokomanako.hack_api.api.dto.tx.TxPatchReq;
import com.lokomanako.hack_api.api.dto.tx.TxReq;
import com.lokomanako.hack_api.api.dto.tx.TxRes;
import com.lokomanako.hack_api.api.ex.ApiEx;
import com.lokomanako.hack_api.api.util.PgU;
import com.lokomanako.hack_api.api.util.StrU;
import com.lokomanako.hack_api.store.ent.AppUsr;
import com.lokomanako.hack_api.store.ent.Cat;
import com.lokomanako.hack_api.store.ent.Kind;
import com.lokomanako.hack_api.store.ent.Tx;
import com.lokomanako.hack_api.store.repo.CatRepo;
import com.lokomanako.hack_api.store.repo.TxRepo;
import com.lokomanako.hack_api.store.repo.UsrRepo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TxSvc {

    @Autowired
    private TxRepo txRepo;

    @Autowired
    private CatRepo catRepo;

    @Autowired
    private UsrRepo usrRepo;

    @Transactional(readOnly = true)
    public PageRes<TxRes> list(UUID uid, String type, UUID catId, String sort, LocalDate dFrom, LocalDate dTo,
                               Integer page, Integer limit) {
        Kind k = pKind(type);
        int p = PgU.p(page);
        int l = PgU.l(limit);
        PageRequest pr = PageRequest.of(p - 1, l, s(sort));
        Page<Tx> data = txRepo.findPage(uid, k, catId, dFrom, dTo, pr);
        return new PageRes<>(
                data.getContent().stream().map(this::map).toList(),
                new Meta(p, l, data.getTotalElements())
        );
    }

    @Transactional
    public TxRes add(UUID uid, TxReq req) {
        Cat c = catRepo.findByIdAndUsr_Id(req.getCatId(), uid).orElseThrow(() ->
                new ApiEx(HttpStatus.BAD_REQUEST, "TX_BAD_CATEGORY", "Category not found")
        );
        if (!c.getKind().equals(req.getType())) {
            throw new ApiEx(HttpStatus.BAD_REQUEST, "TX_TYPE_KIND_MISMATCH", "Type must match category kind");
        }
        AppUsr u = usrRepo.findById(uid).orElseThrow(() ->
                new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Unauthorized")
        );
        Tx t = new Tx();
        t.setUsr(u);
        t.setCat(c);
        t.setType(req.getType());
        t.setSum(req.getSum());
        t.setNote(StrU.note(req.getNote()));
        t.setDt(req.getDate());
        return map(txRepo.save(t));
    }

    @Transactional
    public TxRes patch(UUID uid, UUID id, TxPatchReq req) {
        Tx t = txRepo.findByIdAndUsr_Id(id, uid).orElseThrow(() ->
                new ApiEx(HttpStatus.NOT_FOUND, "TX_NOT_FOUND", "Transaction not found")
        );
        Cat c = t.getCat();
        if (req.getCatId() != null) {
            c = catRepo.findByIdAndUsr_Id(req.getCatId(), uid).orElseThrow(() ->
                    new ApiEx(HttpStatus.BAD_REQUEST, "TX_BAD_CATEGORY", "Category not found")
            );
        }
        Kind k = req.getType() == null ? t.getType() : req.getType();
        BigDecimal sum = req.getSum() == null ? t.getSum() : req.getSum();
        String note = req.getNote() == null ? t.getNote() : StrU.note(req.getNote());
        LocalDate dt = req.getDate() == null ? t.getDt() : req.getDate();

        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiEx(HttpStatus.BAD_REQUEST, "TX_BAD_SUM", "Sum must be greater than zero");
        }
        if (!c.getKind().equals(k)) {
            throw new ApiEx(HttpStatus.BAD_REQUEST, "TX_TYPE_KIND_MISMATCH", "Type must match category kind");
        }

        t.setCat(c);
        t.setType(k);
        t.setSum(sum);
        t.setNote(note);
        t.setDt(dt);
        return map(txRepo.save(t));
    }

    @Transactional
    public void del(UUID uid, UUID id) {
        Tx t = txRepo.findByIdAndUsr_Id(id, uid).orElseThrow(() ->
                new ApiEx(HttpStatus.NOT_FOUND, "TX_NOT_FOUND", "Transaction not found")
        );
        txRepo.delete(t);
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
}
