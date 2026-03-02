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
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "goal_item", uniqueConstraints = {
        @UniqueConstraint(name = "uk_goal_usr", columnNames = {"usr_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class Goal {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usr_id", nullable = false)
    private AppUsr usr;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal target;

    @PrePersist
    public void pp() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
