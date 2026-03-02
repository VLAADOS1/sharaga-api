package com.lokomanako.hack_api.store.repo;

import com.lokomanako.hack_api.store.ent.Rtok;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RtokRepo extends JpaRepository<Rtok, UUID> {

    Optional<Rtok> findByIdAndRevFalse(UUID id);

    Optional<Rtok> findByIdAndUsr_Id(UUID id, UUID uid);

    void deleteByUsr_Id(UUID uid);

    long deleteByExpAtBefore(Instant ts);
}
