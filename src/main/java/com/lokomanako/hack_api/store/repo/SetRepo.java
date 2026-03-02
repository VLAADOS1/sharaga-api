package com.lokomanako.hack_api.store.repo;

import com.lokomanako.hack_api.store.ent.UsrSet;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetRepo extends JpaRepository<UsrSet, UUID> {

    Optional<UsrSet> findByUsr_Id(UUID uid);
}
