package com.boostperformance;

import net.runelite.api.NPC;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BossData
{
    private final int spawnFormId;
    private final int finalFormId;

    private final Set<Integer> validPartners;
    private final String shortName;
    private final String fullName;
    private final double spawnSeconds;
    private final int deathAnimationId;
    public double ehb;
    public static final Map<Integer, BossData> spawnFormBosses = new HashMap<>();
    public static final Map<Integer, BossData> finalFormBosses = new HashMap<>();
    public static final Map<String, BossData> bossesByName = new HashMap<>();

    static
    {
        for (BossDataStatics boss : BossDataStatics.values())
        {
            BossData.AddBoss(
                    boss.getSpawnFormId(),
                    boss.getFinalFormId(),
                    boss.getValidPartners(),
                    boss.getShortName(),
                    boss.getFullName(),
                    boss.getSpawnSeconds(),
                    boss.getDeathAnimationId(),
                    boss.getEhb());
        }
    }
    BossData(int spawnFormId, int finalFormId, Set<Integer> validPartners, String shortName, String fullName, double spawnSeconds, int deathAnimationId, double ehb)
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

    public int getSpawnFormId()
    {
        return spawnFormId;
    }

    public int getFinalFormId()
    {
        return finalFormId;
    }

    public String getFullName()
    {
        return fullName;
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
     * Find Boss ID of given name in bossesByName list
     */
    public static int FindBossIDByName(String bossName)
    {
        BossData boss = bossesByName.get(bossName);
        return boss != null ? boss.finalFormId : -1;
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
     * Adds bossdata to lists of valid bosses, used both with initial hardcoded values & additional web values
     * Allows list of bosses to be up to date when new bosses are released while not creating an unnecessary amount of traffic
     */
    public static void AddBoss(int spawnFormId, int finalFormId, Set<Integer> validPartners, String shortName, String fullName, double spawnSeconds, int deathAnimationId, double ehb)
    {
        if(spawnFormBosses.containsKey(spawnFormId))
            return;

        if(spawnFormId == -1)
            return;

        BossData boss = new BossData(spawnFormId,finalFormId,validPartners,shortName,fullName,spawnSeconds,deathAnimationId,ehb);
        BossData.spawnFormBosses.put(spawnFormId,boss);
        BossData.finalFormBosses.put(finalFormId,boss);
        BossData.bossesByName.put(fullName,boss);
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
