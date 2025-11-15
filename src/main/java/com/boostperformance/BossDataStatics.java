package com.boostperformance;
import lombok.Getter;
import net.runelite.api.NpcID;

import java.util.Set;

enum BossDataStatics
{
    CORPOREAL_BEAST(NpcID.CORPOREAL_BEAST,-1,null,"Corp","Corporeal Beast",30.0, 1676, 60.0),
    GENERAL_GRAARDOR(NpcID.GENERAL_GRAARDOR,-1,null,"Bandos","General Graardor",90.0, 7020, 58.0),
    KRIL_TSUTSAROTH(NpcID.KRIL_TSUTSAROTH,-1,null,"Zammy","K'ril Tsutsaroth", 90.0, 6949, 65.0),
    KREEARRA(NpcID.KREEARRA,-1,null,"Arma","Kree'arra", 90.0, 6979, 40.0),
    COMMANDER_ZILYANA(NpcID.COMMANDER_ZILYANA,-1,null,"Sara","Commander Zilyana", 90.0, 6968, 60.0),
    SARACHNIS(NpcID.SARACHNIS,-1,null,"Sarachnis","Sarachnis", 9.6, 8318, 80.0),
    SCORPIA(NpcID.SCORPIA,-1,null,"Scorpia","Scorpia", 9.6, 6256, 130.0),
    GIANT_MOLE(NpcID.GIANT_MOLE,-1,null,"Mole","Giant Mole", 9.0,3310, 125.0),
    KING_BLACK_DRAGON(NpcID.KING_BLACK_DRAGON,-1,null,"KBD","King Black Dragon",9.0, 92, 130.0),
    CHAOS_ELEMENTAL(NpcID.CHAOS_ELEMENTAL,-1,null,"Chaos Ele","Chaos Elemental", 60.0, 3147, 120.0),
    KALPHITE_QUEEN(NpcID.KALPHITE_QUEEN_963,NpcID.KALPHITE_QUEEN_965,null,"KQ","Kalphite Queen", 30.0, 6233, 55.0),
    CERBERUS(NpcID.CERBERUS_5863,NpcID.CERBERUS,null,"Cerberus","Cerberus", 9.0, 4495, 65.0),
    ABYSSAL_SIRE(NpcID.ABYSSAL_SIRE,NpcID.ABYSSAL_SIRE_5908,null,"Sire","Abyssal Sire", 0.6, 7100, 45.0),
    THERMONUCLEAR_SMOKE_DEVIL(NpcID.THERMONUCLEAR_SMOKE_DEVIL,-1,null,"Thermy","Thermonuclear smoke devil", 0.6, 3849, 150.0),
    KRAKEN(NpcID.WHIRLPOOL_496,NpcID.KRAKEN,null,"Kraken","Kraken", 8.4, 3993, 100.0),
    SCURRIUS(NpcID.SCURRIUS,-1,null,"Scurrius","Scurrius",18.0, 10705, 85.0),
    DAGANNOTH_SUPREME(NpcID.DAGANNOTH_SUPREME,-1,Set.of(NpcID.DAGANNOTH_REX, NpcID.DAGANNOTH_PRIME),"Supreme","Dagannoth Supreme", 90.0, 2856, 105.0),
    DAGANNOTH_PRIME(NpcID.DAGANNOTH_PRIME,-1,Set.of(NpcID.DAGANNOTH_REX, NpcID.DAGANNOTH_SUPREME),"Prime","Dagannoth Prime", 90.0, 2856, 105.0),
    DAGANNOTH_REX(NpcID.DAGANNOTH_REX,-1,Set.of(NpcID.DAGANNOTH_SUPREME, NpcID.DAGANNOTH_PRIME),"Rex","Dagannoth Rex", 90.0, 2856, 105.0);

    @Getter
    private final int spawnFormId;
    @Getter
    private final int finalFormId;
    @Getter
    private final Set<Integer> validPartners;
    @Getter
    private final String shortName;
    @Getter
    private final String fullName;
    @Getter
    private final double spawnSeconds;
    @Getter
    private final int deathAnimationId;
    @Getter
    private final double ehb;
    /**
     * Set Boss data based on static values
     * set final form to be identical to spawn form if no final form specified
     * EHB WILL BE OVERWRITTEN VIA SUCCESSFUL WEB CALL
     */
    BossDataStatics(int spawnFormId, int finalFormId, Set<Integer> validPartners, String shortName, String fullName, double spawnSeconds, int deathAnimationId, double ehb)
    {
        this.spawnFormId = spawnFormId;
        this.finalFormId = finalFormId == -1 ? spawnFormId : finalFormId;
        this.spawnSeconds = spawnSeconds;
        this.validPartners = validPartners;
        this.shortName = shortName;
        this.fullName = fullName;
        this.deathAnimationId = deathAnimationId;
        this.ehb = ehb;
    }
}

