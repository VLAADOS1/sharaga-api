package com.lokomanako.hack_api.api.svc;

import com.lokomanako.hack_api.api.dto.rec.RecReq;
import com.lokomanako.hack_api.api.dto.rec.RecRes;
import com.lokomanako.hack_api.api.ex.ApiEx;
import com.lokomanako.hack_api.api.util.StrU;
import com.lokomanako.hack_api.store.ent.AppUsr;
import com.lokomanako.hack_api.store.ent.Cat;
import com.lokomanako.hack_api.store.ent.Freq;
import com.lokomanako.hack_api.store.ent.Rec;
import com.lokomanako.hack_api.store.ent.Tx;
import com.lokomanako.hack_api.store.repo.CatRepo;
import com.lokomanako.hack_api.store.repo.RecRepo;
import com.lokomanako.hack_api.store.repo.TxRepo;
import com.lokomanako.hack_api.store.repo.UsrRepo;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecSvc {

    @Autowired
    private RecRepo recRepo;

    @Autowired
    private CatRepo catRepo;

    @Autowired
    private TxRepo txRepo;

    @Autowired
    private UsrRepo usrRepo;

    @Autowired
    private TimeSvc timeSvc;

    @Transactional(readOnly = true)
    public List<RecRes> list(UUID uid) {
        return recRepo.findByUsr_IdOrderByNextDtAsc(uid).stream().map(this::map).toList();
    }

    @Transactional
    public RecRes add(UUID uid, RecReq req) {
        Cat c = catRepo.findByIdAndUsr_Id(req.getCatId(), uid).orElseThrow(() ->
                new ApiEx(HttpStatus.BAD_REQUEST, "REC_BAD_CATEGORY", "Category not found")
        );
        if (!c.getKind().equals(req.getType())) {
            throw new ApiEx(HttpStatus.BAD_REQUEST, "REC_TYPE_KIND_MISMATCH", "Type must match category kind");
        }
        AppUsr u = usrRepo.findById(uid).orElseThrow(() ->
                new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Unauthorized")
        );
        Rec r = new Rec();
        r.setUsr(u);
        r.setCat(c);
        r.setType(req.getType());
        r.setSum(req.getSum());
        r.setNote(StrU.note(req.getNote()));
        r.setFreq(req.getFreq());
        r.setStartDt(req.getStartDate());
        r.setNextDt(req.getStartDate());
        return map(recRepo.save(r));
    }

    @Transactional
    public void del(UUID uid, UUID id) {
        Rec r = recRepo.findByIdAndUsr_Id(id, uid).orElseThrow(() ->
                new ApiEx(HttpStatus.NOT_FOUND, "REC_NOT_FOUND", "Recurring not found")
        );
        recRepo.delete(r);
    }

    @Transactional
    public int run(UUID uid) {
        LocalDate td = timeSvc.today(uid);
        List<Rec> list = recRepo.findByUsr_IdAndNextDtLessThanEqualOrderByNextDtAsc(uid, td);
        if (list.isEmpty()) {
            return 0;
        }
        AppUsr u = usrRepo.findById(uid).orElseThrow(() ->
                new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Unauthorized")
        );
        int cnt = 0;
        for (Rec r : list) {
            Tx t = new Tx();
            t.setUsr(u);
            t.setCat(r.getCat());
            t.setType(r.getType());
            t.setSum(r.getSum());
            t.setNote(r.getNote());
            t.setDt(td);
            txRepo.save(t);

            r.setNextDt(next(td, r.getFreq()));
            recRepo.save(r);
            cnt++;
        }
        return cnt;
    }

    private LocalDate next(LocalDate d, Freq f) {
        if (f == Freq.DAY) {
            return d.plusDays(1);
        }
        if (f == Freq.WEEK) {
            return d.plusWeeks(1);
        }
        if (f == Freq.YEAR) {
            return d.plusYears(1);
        }
        return d.plusMonths(1);
    }

    private RecRes map(Rec r) {
        return new RecRes(
                r.getId(),
                r.getType(),
                r.getSum(),
                r.getNote(),
                r.getCat().getId(),
                r.getCat().getName(),
                r.getCat().getColor(),
                r.getFreq(),
                r.getStartDt(),
                r.getNextDt()
        );
    }
}
