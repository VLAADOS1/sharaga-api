package com.lokomanako.hack_api.store.ent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usr_set", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usr_set_usr", columnNames = {"usr_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class UsrSet {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usr_id", nullable = false)
    private AppUsr usr;

    @Column(name = "hide_amt", nullable = false)
    private Boolean hideAmt;

    @Column(name = "cat_seed")
    private Boolean catSeed;

    @Column(nullable = false, length = 40)
    private String tz;

    @PrePersist
    public void pp() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (hideAmt == null) {
            hideAmt = false;
        }
        if (catSeed == null) {
            catSeed = false;
        }
        if (tz == null || tz.isBlank()) {
            tz = "UTC";
        }
    }
}
