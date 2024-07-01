package com.boostperformance;

import lombok.Getter;

import java.util.Set;

public class AdditionalBossDataJson
{
    @Getter
    private int spawnFormId;

    @Getter
    private int finalFormId;

    @Getter
    private Set<Integer> validPartners;

    @Getter
    private String shortName;

    @Getter
    private String fullName;

    @Getter
    private double spawnSeconds;

    @Getter
    private int deathAnimationId;

    @Getter
    private double ehb;

}