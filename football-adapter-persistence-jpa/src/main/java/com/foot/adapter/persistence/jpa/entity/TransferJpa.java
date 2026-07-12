package com.foot.adapter.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "player_transfers")
@Data
public class TransferJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "source_team_id")
    private Long sourceTeamId;

    @Column(name = "target_team_id", nullable = false)
    private Long targetTeamId;

    @Column(name = "transfer_price", nullable = false)
    private BigDecimal transferPrice;

    @Column(name = "transfer_date", nullable = false)
    private Date transferDate;
}

