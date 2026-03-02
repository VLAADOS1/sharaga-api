package com.lokomanako.hack_api.api.svc;

import com.lokomanako.hack_api.api.dto.cat.CatPatchReq;
import com.lokomanako.hack_api.api.dto.cat.CatReq;
import com.lokomanako.hack_api.api.dto.cat.CatRes;
import com.lokomanako.hack_api.api.ex.ApiEx;
import com.lokomanako.hack_api.api.util.StrU;
import com.lokomanako.hack_api.store.ent.AppUsr;
import com.lokomanako.hack_api.store.ent.Cat;
import com.lokomanako.hack_api.store.ent.Kind;
import com.lokomanako.hack_api.store.repo.CatRepo;
import com.lokomanako.hack_api.store.repo.RecRepo;
import com.lokomanako.hack_api.store.repo.TxRepo;
import com.lokomanako.hack_api.store.repo.UsrRepo;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatSvc {

    @Autowired
    private CatRepo catRepo;

    @Autowired
    private TxRepo txRepo;

    @Autowired
    private RecRepo recRepo;

    @Autowired
    private UsrRepo usrRepo;

    @Transactional(readOnly = true)
    public List<CatRes> list(UUID uid) {
        return catRepo.findByUsr_IdOrderByKindAscNameAsc(uid).stream().map(this::map).toList();
    }

    @Transactional
    public CatRes add(UUID uid, CatReq req) {
        AppUsr u = usrRepo.findById(uid).orElseThrow(() ->
                new ApiEx(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "Unauthorized")
        );
        String name = StrU.t(req.getName());
        String nn = StrU.n(name);
        String color = req.getColor().trim().toUpperCase(Locale.ROOT);
        if (catRepo.existsByUsr_IdAndKindAndNameNorm(uid, req.getKind(), nn)) {
            throw new ApiEx(HttpStatus.CONFLICT, "CAT_EXISTS", "Category already exists");
        }
        Cat c = new Cat();
        c.setUsr(u);
        c.setKind(req.getKind());
        c.setName(name);
        c.setNameNorm(nn);
        c.setColor(color);
        return map(catRepo.save(c));
    }

    @Transactional
    public CatRes patch(UUID uid, UUID id, CatPatchReq req) {
        Cat c = catRepo.findByIdAndUsr_Id(id, uid).orElseThrow(() ->
                new ApiEx(HttpStatus.NOT_FOUND, "CAT_NOT_FOUND", "Category not found")
        );
        Kind nk = req.getKind() == null ? c.getKind() : req.getKind();
        String nnm = req.getName() == null ? c.getName() : StrU.t(req.getName());
        if (nnm == null || nnm.length() < 2 || nnm.length() > 24) {
            throw new ApiEx(HttpStatus.BAD_REQUEST, "CAT_BAD_NAME", "Invalid category name");
        }
        String nnn = StrU.n(nnm);
        String clr = req.getColor() == null ? c.getColor() : req.getColor().trim().toUpperCase(Locale.ROOT);

        boolean pairChanged = !nk.equals(c.getKind()) || !nnn.equals(c.getNameNorm());
        if (pairChanged && catRepo.existsByUsr_IdAndKindAndNameNorm(uid, nk, nnn)) {
            throw new ApiEx(HttpStatus.CONFLICT, "CAT_EXISTS", "Category already exists");
        }

        c.setKind(nk);
        c.setName(nnm);
        c.setNameNorm(nnn);
        c.setColor(clr);
        return map(catRepo.save(c));
    }

    @Transactional
    public void del(UUID uid, UUID id) {
        Cat c = catRepo.findByIdAndUsr_Id(id, uid).orElseThrow(() ->
                new ApiEx(HttpStatus.NOT_FOUND, "CAT_NOT_FOUND", "Category not found")
        );
        long a = txRepo.countByCat_IdAndUsr_Id(c.getId(), uid);
        long b = recRepo.countByCat_IdAndUsr_Id(c.getId(), uid);
        if (a > 0 || b > 0) {
            throw new ApiEx(HttpStatus.CONFLICT, "CAT_IN_USE", "Category is used in transactions or recurring");
        }
        catRepo.delete(c);
    }

    private CatRes map(Cat c) {
        return new CatRes(c.getId(), c.getKind(), c.getName(), c.getColor());
    }
}
