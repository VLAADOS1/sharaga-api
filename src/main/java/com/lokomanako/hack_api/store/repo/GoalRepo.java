package com.lokomanako.hack_api.store.repo;

import com.lokomanako.hack_api.store.ent.Goal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepo extends JpaRepository<Goal, UUID> {

    Optional<Goal> findByUsr_Id(UUID uid);

    boolean existsByUsr_Id(UUID uid);

    long countByUsr_Id(UUID uid);

    void deleteByUsr_Id(UUID uid);
}
