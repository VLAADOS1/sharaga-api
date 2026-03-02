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
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cat", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cat_usr_kind_name", columnNames = {"usr_id", "kind", "name_norm"})
})
@Getter
@Setter
@NoArgsConstructor
public class Cat {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usr_id", nullable = false)
    private AppUsr usr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Kind kind;

    @Column(nullable = false, length = 24)
    private String name;

    @Column(name = "name_norm", nullable = false, length = 24)
    private String nameNorm;

    @Column(nullable = false, length = 7)
    private String color;

    @PrePersist
    public void pp() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
