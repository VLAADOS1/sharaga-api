package com.lokomanako.hack_api.store.repo;

import com.lokomanako.hack_api.store.ent.AppUsr;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrRepo extends JpaRepository<AppUsr, UUID> {

    Optional<AppUsr> findByLoginNorm(String loginNorm);

    Optional<AppUsr> findByEmailNorm(String emailNorm);

    boolean existsByLoginNorm(String loginNorm);

    boolean existsByEmailNorm(String emailNorm);
}
