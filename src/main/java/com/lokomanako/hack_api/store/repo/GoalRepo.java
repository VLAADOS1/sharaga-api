package com.lokomanako.hack_api.store.repo;

import com.lokomanako.hack_api.store.ent.Goal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepo extends JpaRepository<Goal, UUID> {

    List<Goal> findByUsr_IdOrderByNameAsc(UUID uid);

    Optional<Goal> findByIdAndUsr_Id(UUID id, UUID uid);

    boolean existsByIdAndUsr_Id(UUID id, UUID uid);

    long countByUsr_Id(UUID uid);

    void deleteByIdAndUsr_Id(UUID id, UUID uid);
}
