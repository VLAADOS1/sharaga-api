package com.lokomanako.hack_api.api.svc;

import com.lokomanako.hack_api.store.ent.UsrSet;
import com.lokomanako.hack_api.store.repo.SetRepo;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeSvc {

    @Autowired
    private SetRepo setRepo;

    public LocalDate today(UUID uid) {
        ZoneId z = zone(uid);
        return LocalDate.now(z);
    }

    public ZoneId zone(UUID uid) {
        UsrSet s = setRepo.findByUsr_Id(uid).orElse(null);
        if (s == null || s.getTz() == null || s.getTz().isBlank()) {
            return ZoneId.of("UTC");
        }
        try {
            return ZoneId.of(s.getTz());
        } catch (Exception e) {
            return ZoneId.of("UTC");
        }
    }
}
