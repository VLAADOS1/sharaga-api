package com.lokomanako.hack_api.store.repo;

import com.lokomanako.hack_api.store.ent.Rec;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecRepo extends JpaRepository<Rec, UUID> {

    List<Rec> findByUsr_IdOrderByNextDtAsc(UUID uid);

    List<Rec> findByUsr_IdAndNextDtLessThanEqualOrderByNextDtAsc(UUID uid, LocalDate dt);

    Optional<Rec> findByIdAndUsr_Id(UUID id, UUID uid);

    long countByCat_IdAndUsr_Id(UUID catId, UUID uid);

    void deleteByUsr_Id(UUID uid);
}
