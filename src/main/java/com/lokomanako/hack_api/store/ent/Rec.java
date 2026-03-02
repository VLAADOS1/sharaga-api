package com.lokomanako.hack_api.store.ent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rec_item")
@Getter
@Setter
@NoArgsConstructor
public class Rec {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usr_id", nullable = false)
    private AppUsr usr;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Kind type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal sum;

    @Column(length = 80)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Freq freq;

    @Column(name = "start_dt", nullable = false)
    private LocalDate startDt;

    @Column(name = "next_dt", nullable = false)
    private LocalDate nextDt;

    @PrePersist
    public void pp() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (nextDt == null) {
            nextDt = startDt;
        }
    }
}
