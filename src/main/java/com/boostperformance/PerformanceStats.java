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

    int totalPausedKc = 0;

    Duration totalPausedDuration = Duration.ZERO;

    int bossId;
    double kph;
    int kc;
    int snipes;
    double ehb;
    long pb;
    Duration duration;
    int pausedKc;

    Duration pausedDuration;

    public PerformanceStats(){
        this.bossId = 0;
        this.kph = 0;
        this.kc = 0;
        this.snipes = 0;
        this.ehb = 0;
        this.pb = -1;
        this.duration = Duration.ZERO;
        this.pausedKc = 0;
    }
    public PerformanceStats(int bossId,double kph,int kc,int snipes,double ehb, long pb,Duration duration, int pausedKc, Duration pausedDuration){
        this.bossId = bossId;
        this.kph = kph;
        this.kc = kc;
        this.snipes = snipes;
        this.ehb = ehb;
        this.pb = pb;
        this.duration = duration;
        this.pausedKc = pausedKc;
        this.pausedDuration = pausedDuration;

        overallStats.kc += kc;
        overallStats.snipes += snipes;
        overallStats.ehb += ehb;

        overallStats.totalPausedDuration = overallStats.totalPausedDuration.plus(pausedDuration);
        overallStats.duration = overallStats.duration.plus(duration).minus(pausedDuration);

        if(pb != -1 && pb < overallStats.pb)
            overallStats.pb = pb;

        double secondsPerKill = (double)overallStats.duration.getSeconds() / (double)overallStats.kc;
        overallStats.kph = (3600d / secondsPerKill);
        overallStats.totalPausedKc += pausedKc;
    }

    public static void Add(int bossId, double kph, int kc, int snipes, double ehb, long pb, Instant startTime, Instant killStartTime, int pausedKc,Duration pausedDuration){
        statsCollection.add(new PerformanceStats(bossId,kph,kc,snipes,ehb,pb,Duration.between(startTime,killStartTime),pausedKc,pausedDuration));
    }

    public static void Clear(BoostPerformancePlugin plugin){
        statsCollection.clear();
        overallStats = new PerformanceStats();
        overallStats.pb = plugin.currentFastestKill;
    }


}
