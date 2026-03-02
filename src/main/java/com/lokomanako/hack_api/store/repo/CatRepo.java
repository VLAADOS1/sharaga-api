package com.lokomanako.hack_api.store.repo;

import com.lokomanako.hack_api.store.ent.Cat;
import com.lokomanako.hack_api.store.ent.Kind;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatRepo extends JpaRepository<Cat, UUID> {

    List<Cat> findByUsr_IdOrderByKindAscNameAsc(UUID uid);

    List<Cat> findByUsr_Id(UUID uid);

    Optional<Cat> findByIdAndUsr_Id(UUID id, UUID uid);

    boolean existsByUsr_IdAndKindAndNameNorm(UUID uid, Kind kind, String nameNorm);

    void deleteByUsr_Id(UUID uid);
}
