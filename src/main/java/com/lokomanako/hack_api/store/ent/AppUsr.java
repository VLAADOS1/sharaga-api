package com.lokomanako.hack_api.store.ent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_usr", uniqueConstraints = {
        @UniqueConstraint(name = "uk_app_usr_login_norm", columnNames = {"login_norm"}),
        @UniqueConstraint(name = "uk_app_usr_email_norm", columnNames = {"email_norm"})
})
@Getter
@Setter
@NoArgsConstructor
public class AppUsr {

    @Id
    private UUID id;

    @Column(nullable = false, length = 18)
    private String login;

    @Column(name = "login_norm", nullable = false, length = 18)
    private String loginNorm;

    @Column(name = "pass_hash", nullable = false, length = 120)
    private String passHash;

    @Column(length = 120)
    private String email;

    @Column(name = "email_norm", length = 120)
    private String emailNorm;

    @Column(name = "sq1_hash", length = 120)
    private String sq1Hash;

    @Column(name = "sq2_hash", length = 120)
    private String sq2Hash;

    @Column(name = "sq3_hash", length = 120)
    private String sq3Hash;

    @Column(name = "fail_cnt", nullable = false)
    private Integer failCnt;

    @Column(name = "lock_till")
    private Instant lockTill;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void pp() {
        Instant now = Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (failCnt == null) {
            failCnt = 0;
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void pu() {
        updatedAt = Instant.now();
    }
}
