package com.lokomanako.hack_api.store.ent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rtok")
@Getter
@Setter
@NoArgsConstructor
public class Rtok {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usr_id", nullable = false)
    private AppUsr usr;

    @Column(name = "exp_at", nullable = false)
    private Instant expAt;

    @Column(nullable = false)
    private Boolean rev;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void pp() {
        if (rev == null) {
            rev = false;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
