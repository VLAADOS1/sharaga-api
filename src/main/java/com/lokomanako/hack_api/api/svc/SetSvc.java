package com.lokomanako.hack_api.api.svc;

import com.lokomanako.hack_api.api.dto.set.SetReq;
import com.lokomanako.hack_api.api.dto.set.SetRes;
import com.lokomanako.hack_api.api.ex.ApiEx;
import com.lokomanako.hack_api.store.ent.AppUsr;
import com.lokomanako.hack_api.store.ent.UsrSet;
import com.lokomanako.hack_api.store.repo.SetRepo;
import com.lokomanako.hack_api.store.repo.UsrRepo;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetSvc {

    @Autowired
    private SetRepo setRepo;

    @Autowired
    private UsrRepo usrRepo;

    @Transactional
    public SetRes get(UUID uid) {
        UsrSet s = getOrCreate(uid);
        return new SetRes(s.getHideAmt());
    }

    @Transactional
    public SetRes patch(UUID uid, SetReq req) {
        UsrSet s = getOrCreate(uid);
        s.setHideAmt(req.getHideAmounts());
        setRepo.save(s);
        return new SetRes(s.getHideAmt());
    }

    private UsrSet getOrCreate(UUID uid) {
        UsrSet s = setRepo.findByUsr_Id(uid).orElse(null);
        if (s != null) {
            return s;
        }
        AppUsr u = usrRepo.findById(uid).orElseThrow(() ->
                new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Unauthorized")
        );
        s = new UsrSet();
        s.setUsr(u);
        s.setHideAmt(false);
        s.setTz("UTC");
        return setRepo.save(s);
    }
}
