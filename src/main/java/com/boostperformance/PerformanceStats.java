package com.boostperformance;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PerformanceStats
{
    //collection of tracked currents.
    public static List<PerformanceStats> statsCollection = new ArrayList<>();
    //overall sum of collection
    public static PerformanceStats overallStats = new PerformanceStats();

    int bossId;
    double kph;
    int kc;
    int snipes;
    double ehb;
    long pb;
    Duration duration;

    public PerformanceStats(){
        this.bossId = 0;
        this.kph = 0;
        this.kc = 0;
        this.snipes = 0;
        this.ehb = 0;
        this.pb = -1;
        this.duration = Duration.ZERO;
    }
    public PerformanceStats(int bossId,double kph,int kc,int snipes,double ehb, long pb,Duration duration){
        this.bossId = bossId;
        this.kph = kph;
        this.kc = kc;
        this.snipes = snipes;
        this.ehb = ehb;
        this.pb = pb;
        this.duration = duration;

        overallStats.kc += kc;
        overallStats.snipes += snipes;
        overallStats.ehb += ehb;
        overallStats.duration = overallStats.duration.plus(duration);

        if(pb != -1 && pb < overallStats.pb)
            overallStats.pb = pb;

        double secondsPerKill = (double)overallStats.duration.getSeconds() / (double)overallStats.kc;
        overallStats.kph = (3600d / secondsPerKill);

    }

    public static void Add(int bossId, double kph, int kc, int snipes, double ehb, long pb, Instant startTime, Instant killStartTime){
        statsCollection.add(new PerformanceStats(bossId,kph,kc,snipes,ehb,pb,Duration.between(startTime,killStartTime)));
    }

    public static void Clear(BoostPerformancePlugin plugin){
        statsCollection.clear();
        overallStats = new PerformanceStats();
        overallStats.pb = plugin.currentFastestKill;
    }


}
