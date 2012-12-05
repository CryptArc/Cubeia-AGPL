package com.cubeia.game.poker.challenge.api;


import java.io.Serializable;
import java.math.BigDecimal;

public class ChallengeConfiguration implements Serializable {
    private static final long serialVersionUID = 6067181594984848562L;

    private final Integer id;
    private final BigDecimal buyIn;

    private final BigDecimal fee;
    private final String name;


    public ChallengeConfiguration(Integer id,  String name, BigDecimal buyIn, BigDecimal fee) {
        this.id = id;
        this.buyIn = buyIn;
        this.fee = fee;
        this.name = name;
    }


    public Integer getId() {
        return id;
    }

    public BigDecimal getBuyIn() {
        return buyIn;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public String getName() {
        return name;
    }





}
