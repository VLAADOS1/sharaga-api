package com.lokomanako.hack_api.api.svc;

import com.lokomanako.hack_api.api.dto.goal.GoalReq;
import com.lokomanako.hack_api.api.dto.goal.GoalRes;
import com.lokomanako.hack_api.api.ex.ApiEx;
import com.lokomanako.hack_api.api.util.StrU;
import com.lokomanako.hack_api.store.ent.AppUsr;
import com.lokomanako.hack_api.store.ent.Goal;
import com.lokomanako.hack_api.store.ent.Kind;
import com.lokomanako.hack_api.store.ent.Tx;
import com.lokomanako.hack_api.store.repo.GoalRepo;
import com.lokomanako.hack_api.store.repo.TxRepo;
import com.lokomanako.hack_api.store.repo.UsrRepo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoalSvc {

    @Autowired
    private GoalRepo goalRepo;

    @Autowired
    private TxRepo txRepo;

    @Autowired
    private UsrRepo usrRepo;

    @Transactional(readOnly = true)
    public GoalRes get(UUID uid) {
        Goal g = goalRepo.findByUsr_Id(uid).orElse(null);
        BigDecimal cur = bal(uid);
        if (g == null) {
            return new GoalRes(null, cur, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return new GoalRes(g.getName(), cur, g.getTarget(), pct(cur, g.getTarget()));
    }

    @Transactional
    public GoalRes put(UUID uid, GoalReq req) {
        Goal g = goalRepo.findByUsr_Id(uid).orElse(null);
        if (g == null) {
            AppUsr u = usrRepo.findById(uid).orElseThrow(() ->
                    new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Unauthorized")
            );
            g = new Goal();
            g.setUsr(u);
        }
        g.setName(StrU.t(req.getName()));
        g.setTarget(req.getTarget());
        goalRepo.save(g);
        BigDecimal cur = bal(uid);
        return new GoalRes(g.getName(), cur, g.getTarget(), pct(cur, g.getTarget()));
    }

    @Transactional
    public void del(UUID uid) {
        goalRepo.deleteByUsr_Id(uid);
    }

    private BigDecimal bal(UUID uid) {
        List<Tx> list = txRepo.findAllForRep(uid, null, null, null, null);
        BigDecimal b = BigDecimal.ZERO;
        for (Tx t : list) {
            if (t.getType() == Kind.INC) {
                b = b.add(t.getSum());
            } else {
                b = b.subtract(t.getSum());
            }
        }
        return b;
    }

    private BigDecimal pct(BigDecimal cur, BigDecimal trg) {
        if (trg == null || trg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return cur.multiply(BigDecimal.valueOf(100)).divide(trg, 2, RoundingMode.HALF_UP);
    }
}
