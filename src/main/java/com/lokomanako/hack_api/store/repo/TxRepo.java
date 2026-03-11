package com.lokomanako.hack_api.store.repo;

import com.lokomanako.hack_api.store.ent.Kind;
import com.lokomanako.hack_api.store.ent.Tx;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TxRepo extends JpaRepository<Tx, UUID> {

    Optional<Tx> findByIdAndUsr_Id(UUID id, UUID uid);

    long countByCat_IdAndUsr_Id(UUID catId, UUID uid);

    List<Tx> findByUsr_IdAndGoal_Id(UUID uid, UUID goalId);

    boolean existsByGoal_IdAndUsr_Id(UUID goalId, UUID uid);

    void deleteByUsr_Id(UUID uid);

    @Query("""
            select t from Tx t
            where t.usr.id = :uid
            and (:type is null or t.type = :type)
            and (:catId is null or t.cat.id = :catId)
            and (:dFrom is null or t.dt >= :dFrom)
            and (:dTo is null or t.dt <= :dTo)
            """)
    Page<Tx> findPage(
            @Param("uid") UUID uid,
            @Param("type") Kind type,
            @Param("catId") UUID catId,
            @Param("dFrom") LocalDate dFrom,
            @Param("dTo") LocalDate dTo,
            Pageable pageable
    );

    @Query("""
            select t from Tx t
            where t.usr.id = :uid
            and (:type is null or t.type = :type)
            and (:catId is null or t.cat.id = :catId)
            and (:dFrom is null or t.dt >= :dFrom)
            and (:dTo is null or t.dt <= :dTo)
            """)
    List<Tx> findList(
            @Param("uid") UUID uid,
            @Param("type") Kind type,
            @Param("catId") UUID catId,
            @Param("dFrom") LocalDate dFrom,
            @Param("dTo") LocalDate dTo,
            Pageable pageable
    );

    @Query("""
            select t from Tx t
            where t.usr.id = :uid
            and (:type is null or t.type = :type)
            and (:catId is null or t.cat.id = :catId)
            and (:dFrom is null or t.dt >= :dFrom)
            and (:dTo is null or t.dt <= :dTo)
            """)
    List<Tx> findAllForRep(
            @Param("uid") UUID uid,
            @Param("type") Kind type,
            @Param("catId") UUID catId,
            @Param("dFrom") LocalDate dFrom,
            @Param("dTo") LocalDate dTo
    );
}
