package com.lokomanako.hack_api.api.svc;

import com.lokomanako.hack_api.api.util.StrU;
import com.lokomanako.hack_api.store.ent.AppUsr;
import com.lokomanako.hack_api.store.ent.Cat;
import com.lokomanako.hack_api.store.ent.Kind;
import com.lokomanako.hack_api.store.ent.UsrSet;
import com.lokomanako.hack_api.store.repo.CatRepo;
import com.lokomanako.hack_api.store.repo.SetRepo;
import com.lokomanako.hack_api.store.repo.UsrRepo;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefCatSvc {

    @Autowired
    private UsrRepo usrRepo;

    @Autowired
    private SetRepo setRepo;

    @Autowired
    private CatRepo catRepo;

    @Transactional
    public void runAll() {
        List<AppUsr> us = usrRepo.findAll();
        for (AppUsr u : us) {
            run(u);
        }
    }

    @Transactional
    public void runOne(UUID uid) {
        AppUsr u = usrRepo.findById(uid).orElse(null);
        if (u == null) {
            return;
        }
        run(u);
    }

    private void run(AppUsr u) {
        ren(u);
        UsrSet s = setRepo.findByUsr_Id(u.getId()).orElse(null);
        if (s != null && Boolean.TRUE.equals(s.getCatSeed())) {
            return;
        }
        add(u, Kind.INC, "Зарплата", "#2E7D32");
        add(u, Kind.INC, "Премия", "#388E3C");
        add(u, Kind.INC, "Подарок", "#43A047");
        add(u, Kind.INC, "Кэшбэк", "#4CAF50");
        add(u, Kind.INC, "Инвестиции", "#66BB6A");
        add(u, Kind.INC, "Продажа", "#81C784");
        add(u, Kind.INC, "Прочее доход", "#A5D6A7");

        add(u, Kind.EXP, "Еда", "#E53935");
        add(u, Kind.EXP, "Транспорт", "#F4511E");
        add(u, Kind.EXP, "Дом", "#FB8C00");
        add(u, Kind.EXP, "Здоровье", "#FFB300");
        add(u, Kind.EXP, "Развлечения", "#8E24AA");
        add(u, Kind.EXP, "Покупки", "#5E35B1");
        add(u, Kind.EXP, "Прочее расход", "#546E7A");

        if (s == null) {
            s = new UsrSet();
            s.setUsr(u);
            s.setHideAmt(false);
            s.setTz("UTC");
        }
        s.setCatSeed(true);
        setRepo.save(s);
    }

    private void add(AppUsr u, Kind k, String n, String c) {
        String name = StrU.t(n);
        String nn = StrU.n(name);
        if (catRepo.existsByUsr_IdAndKindAndNameNorm(u.getId(), k, nn)) {
            return;
        }
        Cat x = new Cat();
        x.setUsr(u);
        x.setKind(k);
        x.setName(name);
        x.setNameNorm(nn);
        x.setColor(c.toUpperCase(Locale.ROOT));
        catRepo.save(x);
    }

    private void ren(AppUsr u) {
        List<Cat> list = catRepo.findByUsr_Id(u.getId());
        if (list.isEmpty()) {
            return;
        }
        Map<String, Cat> by = new HashMap<>();
        for (Cat c : list) {
            by.put(k(c.getKind(), c.getNameNorm()), c);
        }
        renOne(by, Kind.INC, "salary", "Зарплата");
        renOne(by, Kind.INC, "bonus", "Премия");
        renOne(by, Kind.INC, "gift", "Подарок");
        renOne(by, Kind.INC, "cashback", "Кэшбэк");
        renOne(by, Kind.INC, "invest", "Инвестиции");
        renOne(by, Kind.INC, "sell", "Продажа");
        renOne(by, Kind.INC, "otherin", "Прочее доход");

        renOne(by, Kind.EXP, "food", "Еда");
        renOne(by, Kind.EXP, "transport", "Транспорт");
        renOne(by, Kind.EXP, "home", "Дом");
        renOne(by, Kind.EXP, "health", "Здоровье");
        renOne(by, Kind.EXP, "fun", "Развлечения");
        renOne(by, Kind.EXP, "shopping", "Покупки");
        renOne(by, Kind.EXP, "otherex", "Прочее расход");
    }

    private void renOne(Map<String, Cat> by, Kind kind, String oldNorm, String newName) {
        Cat old = by.get(k(kind, oldNorm));
        if (old == null) {
            return;
        }
        String newNorm = StrU.n(newName);
        if (by.containsKey(k(kind, newNorm))) {
            return;
        }
        old.setName(newName);
        old.setNameNorm(newNorm);
        catRepo.save(old);
        by.remove(k(kind, oldNorm));
        by.put(k(kind, newNorm), old);
    }

    private String k(Kind kind, String nameNorm) {
        return kind.name() + ":" + nameNorm;
    }
}
