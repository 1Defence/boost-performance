package com.boostperformance;

import net.runelite.api.coords.WorldPoint;

public class RespawnData
{
    public RespawnData(int id){
        this.id = id;
        BossData boss = BossData.FindSpawnForm(id);
        this.totalTicks = (int)(boss.getSpawnSeconds()/0.6d);
        this.remainingTicks = totalTicks;
        this.respawnLocation = boss.getSpawnLocation();
        this.size = boss.getSize();
    }
    int id;
    int totalTicks;
    int remainingTicks;
    WorldPoint respawnLocation;
    int size;

    public int getId() {
        return id;
    }

    void decrementRemainingTicks(){
        remainingTicks--;
        //(shouldnt happen single world) should technically be 0, but in the event of latency a changing wrong visual is better than a stalled wrong visual
        if(remainingTicks < -5){
            remainingTicks = -5;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RespawnData)) return false;
        return id == ((RespawnData) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}
