package com.boostperformance;
import com.google.common.collect.ImmutableMap;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;

import java.util.Map;
import java.util.Set;

enum BossData
{
    CORPOREAL_BEAST(NpcID.CORPOREAL_BEAST,-1,null,"Corp", 30.0, 1676, 60.0),
    GENERAL_GRAARDOR(NpcID.GENERAL_GRAARDOR,-1,null,"Bandos", 90.0, 7020, 58.0),
    KRIL_TSUTSAROTH(NpcID.KRIL_TSUTSAROTH,-1,null,"Zammy", 90.0, 6949, 65.0),
    KREEARRA(NpcID.KREEARRA,-1,null,"Arma", 90.0, 6979, 40.0),
    COMMANDER_ZILYANA(NpcID.COMMANDER_ZILYANA,-1,null,"Sara", 90.0, 6968, 60.0),
    SARACHNIS(NpcID.SARACHNIS,-1,null,"Sarachnis", 9.6, 8318, 80.0),
    SCORPIA(NpcID.SCORPIA,-1,null,"Scorpia", 9.6, 6256, 130.0),
    GIANT_MOLE(NpcID.GIANT_MOLE,-1,null,"Mole", 9.0,3310, 125.0),
    KING_BLACK_DRAGON(NpcID.KING_BLACK_DRAGON,-1,null,"KBD", 9.0, 92, 130.0),
    CHAOS_ELEMENTAL(NpcID.CHAOS_ELEMENTAL,-1,null,"Chaos Ele", 60.0, 3147, 120.0),
    KALPHITE_QUEEN(NpcID.KALPHITE_QUEEN_963,NpcID.KALPHITE_QUEEN_965,null,"KQ", 30.0, 6233, 55.0),
    CERBERUS(NpcID.CERBERUS_5863,NpcID.CERBERUS,null,"Cerberus", 9.0, 4495, 65.0),
    ABYSSAL_SIRE(NpcID.ABYSSAL_SIRE,NpcID.ABYSSAL_SIRE_5908,null,"Sire", 0.6, 7100, 45.0),
    THERMONUCLEAR_SMOKE_DEVIL(NpcID.THERMONUCLEAR_SMOKE_DEVIL,-1,null,"Thermy", 0.6, 3849, 150.0),
    KRAKEN(NpcID.WHIRLPOOL_496,NpcID.KRAKEN,null,"Kraken", 8.4, 3993, 100.0),
    SCURRIUS(NpcID.SCURRIUS,-1,null,"Scurrius",18.0, 10705, 85.0),
    DAGANNOTH_SUPREME(NpcID.DAGANNOTH_SUPREME,-1,Set.of(NpcID.DAGANNOTH_REX, NpcID.DAGANNOTH_PRIME),"Supreme", 90.0, 2856, 105.0),
    DAGANNOTH_PRIME(NpcID.DAGANNOTH_PRIME,-1,Set.of(NpcID.DAGANNOTH_REX, NpcID.DAGANNOTH_SUPREME),"Prime", 90.0, 2856, 105.0),
    DAGANNOTH_REX(NpcID.DAGANNOTH_REX,-1,Set.of(NpcID.DAGANNOTH_SUPREME, NpcID.DAGANNOTH_PRIME),"Rex", 90.0, 2856, 105.0);

    private static final Map<Integer, BossData> spawnFormBosses;
    private static final Map<Integer, BossData> finalFormBosses;

    private final int spawnFormId;
    private final int finalFormId;

    private final Set<Integer> validPartners;
    private final String shortName;
    private final double spawnSeconds;
    private final int deathAnimationId;
    public double ehb;

    static
    {
        ImmutableMap.Builder<Integer, BossData> spawnFormBuilder = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<Integer, BossData> finalFormBuilder = new ImmutableMap.Builder<>();

        for (BossData boss : values())
        {
            spawnFormBuilder.put(boss.getSpawnFormId(), boss);
            finalFormBuilder.put(boss.getFinalFormId(),boss);
        }

        spawnFormBosses = spawnFormBuilder.build();
        finalFormBosses = finalFormBuilder.build();
    }

    /**
     * Set Boss data based on static values
     * set final form to be identical to spawn form if no final form specified
     * EHB WILL BE OVERWRITTEN VIA SUCCESSFUL WEB CALL
     */
    BossData(int spawnFormId, int finalFormId, Set<Integer> validPartners, String shortName, double spawnSeconds, int deathAnimationId, double ehb)
    {
        this.spawnFormId = spawnFormId;
        this.finalFormId = finalFormId == -1 ? spawnFormId : finalFormId;
        this.spawnSeconds = spawnSeconds;
        this.validPartners = validPartners;
        this.shortName = shortName;
        this.deathAnimationId = deathAnimationId;
        this.ehb = ehb;
    }

    public int getSpawnFormId()
    {
        return spawnFormId;
    }

    public int getFinalFormId()
    {
        return finalFormId;
    }
    /**
     * Check if passed boss ID is a partner of the current boss (DKS)
     */
    public boolean hasPartner(int partnerId){
        if(validPartners == null)
            return false;
        return validPartners.contains(partnerId);
    }

    public double getSpawnSeconds()
    {
        return spawnSeconds;
    }
    /**
     * Find Boss of given ID in the spawnForm list
     */
    public static BossData FindSpawnForm(int id)
    {
        return spawnFormBosses.get(id);
    }
    /**
     * Find Boss of given ID in the finalForm list
     */
    public static BossData FindFinalForm(int id)
    {
        return finalFormBosses.get(id);
    }
    /**
     * Validate the death of an NPC - ONLY CALLED DURING DESPAWN EVENT
     * ensure boss is in its final form and animation is the death animation
     * this prevents overkill issues and rendering issues from causing false positives
     */
    public static boolean IsValidDeath(NPC npc){
        BossData boss = FindFinalForm(npc.getId());
        if(boss != null)
            return (npc.getAnimation() == boss.deathAnimationId);

        return false;
    }
    /**
     * Validate the spawn of an NPC
     * ensure boss is in its starting form
     */
    public static boolean IsValidBossSpawn(NPC npc){
        return FindSpawnForm(npc.getId()) != null;
    }
    /**
     * Set EHB of a bosses spawn and final forms
     * Used to update the existing static values from web-generated data
     */
    public static void SetBossEHB(int spawnFormId, double ehb){
        BossData boss = FindSpawnForm(spawnFormId);
        spawnFormBosses.get(boss.spawnFormId).ehb = ehb;
        finalFormBosses.get(boss.finalFormId).ehb = ehb;
    }
    /**
     * Get combined boss name based on current partners
     * For dks and other potential partner bosses, we generate a name based on the current partners short-names
     * EX dks multi: Dagannoth Rex and Dagannoth Prime would be "Rex,Prime"
     * EX dks single: Dagannoth Rex would be "Dagannoth Rex"
     */
    public static String GetBossName(Set<Integer> partners){
        StringBuilder conName = new StringBuilder();
        if(partners != null){
            for (int partnerId : partners)
            {
                conName.append(FindSpawnForm(partnerId).shortName).append(",");
            }
            conName.deleteCharAt(conName.length() - 1);
        }
        return conName.toString();
    }



}

