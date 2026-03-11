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
    public List<GoalRes> list(UUID uid) {
        return goalRepo.findByUsr_IdOrderByNameAsc(uid).stream()
                .map(g -> map(uid, g))
                .toList();
    }

    @Transactional
    public GoalRes add(UUID uid, GoalReq req) {
        AppUsr u = usrRepo.findById(uid).orElseThrow(() ->
                new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Unauthorized")
        );
        Goal g = new Goal();
        g.setUsr(u);
        g.setName(StrU.t(req.getName()));
        g.setTarget(req.getTarget());
        g = goalRepo.save(g);
        return map(uid, g);
    }

    @Transactional
    public GoalRes patch(UUID uid, UUID id, GoalReq req) {
        Goal g = goalRepo.findByIdAndUsr_Id(id, uid).orElseThrow(() ->
                new ApiEx(HttpStatus.NOT_FOUND, "GOAL_NOT_FOUND", "Goal not found")
        );
        g.setName(StrU.t(req.getName()));
        g.setTarget(req.getTarget());
        g = goalRepo.save(g);
        return map(uid, g);
    }

    @Transactional
    public void del(UUID uid, UUID id) {
        Goal g = goalRepo.findByIdAndUsr_Id(id, uid).orElseThrow(() ->
                new ApiEx(HttpStatus.NOT_FOUND, "GOAL_NOT_FOUND", "Goal not found")
        );
        if (txRepo.existsByGoal_IdAndUsr_Id(g.getId(), uid)) {
            throw new ApiEx(HttpStatus.CONFLICT, "GOAL_IN_USE", "Goal has transactions");
        }
        goalRepo.delete(g);
    }

    private GoalRes map(UUID uid, Goal g) {
        BigDecimal cur = bal(uid, g.getId());
        return new GoalRes(g.getId(), g.getName(), cur, g.getTarget(), pct(cur, g.getTarget()));
    }

    private BigDecimal bal(UUID uid, UUID gid) {
        List<Tx> list = txRepo.findByUsr_IdAndGoal_Id(uid, gid);
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
