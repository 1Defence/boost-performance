package com.boostperformance;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.boostperformance.BoostPerformancePlugin.PERFORMANCE_SECTION;

public class Utils
{

    private final static String baseKPHString = "KPH: ";
    private final static String baseKCString = "KC: ";
    private final static String baseSnipeString = "Snipes: ";
    private final static String baseEHBString = "EHB: ";
    private final static String baseDurationString = "🕑 ";
    private final static  String basePBString = "PB: ";
    private final static String invalidResultString = "--";

    BoostPerformancePlugin plugin;
    public Utils(BoostPerformancePlugin plugin){
        this.plugin = plugin;
    }
    /**
     * Calc KPH based on duration and kills
     */
    public double GetKillsPerHourDouble(PERFORMANCE_SECTION section, boolean preventFall){
        double seconds = GetElapsedSeconds(section,preventFall);
        double kills = GetKC(section);
        if(kills == 0)
            return  0;
        double secondsPerKill = seconds / kills;
        return (3600d / secondsPerKill);
    }
    /**
     * Format KPH string -- used for panel
     */
    public String GetKillsPerHourString(PERFORMANCE_SECTION section, boolean preventFall){
        DecimalFormat df = new DecimalFormat("0.00");
        double kph = GetKillsPerHourDouble(section,preventFall);
        if(kph == 0)
            return baseKPHString+invalidResultString;
        return baseKPHString+df.format(kph);
    }
    /**
     * Format KPH value -- used for game message
     */
    public String GetKillsPerHourGameMessage(){
        DecimalFormat df = new DecimalFormat("0.00");
        double kph = GetKillsPerHourDouble(PERFORMANCE_SECTION.CURRENT,false);
        return df.format(kph);
    }
    /**
     * Calc Duration for given section
     * prevent falloff removes the excess time since our most recent kill
     */
    public long GetElapsedSeconds(PERFORMANCE_SECTION section, boolean preventFall){
        Duration duration = GetDuration(section,preventFall);
        if(duration == null)
            return 0;

        return duration.getSeconds();
    }
    /**
     * Calc kill speed of recent kill
     * Removes respawn time if applicable
     */
    public long GetRecentKillSpeed(){
        if(plugin.killStartTime == null)
            return  0;
        boolean sameWorldAsLast = plugin.worldOfRecentKill == plugin.worldOfPreviousKill;
        Duration elapsed = Duration.between(plugin.killStartTime, Instant.now());
        long respawnOffSet = sameWorldAsLast  && plugin.currentPartnerBosses == null ? (long) BossData.FindSpawnForm(plugin.recentKillId).getSpawnSeconds() : 0;
        return Math.max(elapsed.getSeconds()-respawnOffSet,0);
    }
    /**
     * Converts a long into a formatted kill speed string
     */
    public String GetKillSpeedFromLong(long killSpeed){
        LocalTime time = LocalTime.ofSecondOfDay(killSpeed);
        if (time.getHour() > 0)
        {
            return time.format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        return time.format(DateTimeFormatter.ofPattern("m:ss"));
    }
    /**
     * Format KC value -- used for panel
     */
    public String GetKCString(PERFORMANCE_SECTION section){
        int ignoredKC = GetIgnoredKC(section);
        return baseKCString + (GetKC(section) + ignoredKC) + (ignoredKC > 0 ? " (-"+ignoredKC+")" : "");
    }
    /**
     * Iniital kc is a burner, we still want to know how many of these burners have occued
     * for current it'll effectively be 0 or 1
     * for overall there will be 1 burner kc per current-reset or rather 1 per object in the collection
     */
    public int GetIgnoredKC(PERFORMANCE_SECTION section){
        boolean killStarted = plugin.CurrentKillHasStarted();
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        int currentIgnored = killStarted ? 1 : 0;
        int totalIgnored = currentIgnored + PerformanceStats.statsCollection.size();
        return current ? currentIgnored : totalIgnored;
    }
    /**
     * Format Snipe string -- used for panel
     * On top of displaying the number of snipes, display it as a fraction with the numerator always aiming to be 1, additionally the percentage of snipes
     */
    public String GetSnipeString(PERFORMANCE_SECTION section){
        DecimalFormat df = new DecimalFormat("0.00");
        int kills = GetKC(section) + GetIgnoredKC(section);
        int snipes = GetSnipes(section);
        if(snipes == 0)
            return baseSnipeString+"0";
        if(snipes > kills)
            return baseSnipeString+snipes;
        int snipeFractionDenominator = snipes > 0 ? (int)Math.floor((double)kills/(double)snipes) : kills;
        int snipeFractionNumerator = snipes > 0 ? 1 : 0;
        double snipePercentage = ((double)snipes/(double)kills)*100d;
        return baseSnipeString+snipes+", "+snipeFractionNumerator+"/"+snipeFractionDenominator+" ("+df.format(snipePercentage)+"%)";
    }
    /**
     * Format EHB string -- used for panel
     */
    public String GetEHBString(PERFORMANCE_SECTION section){
        DecimalFormat df = new DecimalFormat("0.00");
        double ehbGained = GetEHBGained(section);
        if(ehbGained == 0)
            return baseEHBString+invalidResultString;
        double scaledEhbRate = GetScaledEHBRate(section,ehbGained);
        return baseEHBString+df.format(ehbGained)+" ("+df.format(scaledEhbRate)+"x)";
    }
    /**
     * Calc gained EHB
     * Based on duration, EHB rate and kills of given section
     */
    public double GetEHBGained(PERFORMANCE_SECTION section){
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        int bossKills = GetKC(PERFORMANCE_SECTION.CURRENT);
        if(current && bossKills == 0)
            return 0;
        BossData boss = BossData.FindFinalForm(plugin.recentKillId);

        double currentEHBGained = 0;
        if (boss != null)
        {
             double currentEHBRate = boss.ehb;
             currentEHBGained = (double) (bossKills)/currentEHBRate;
        }


        double overallEHBGained = PerformanceStats.overallStats.ehb + currentEHBGained;
        return current ? currentEHBGained : overallEHBGained;
    }
    /**
     * Calc scaled EHB Rate - what % over or under the standard EHB rate the section is
     * Based on EHB Gained and duration of given section
     */
    public double GetScaledEHBRate(PERFORMANCE_SECTION section, double ehbGained){
        double hoursSpent = (GetElapsedSeconds(section,true)/3600d);
        return ehbGained/hoursSpent;
    }
    /**
     * Format Duration string -- used for panel
     */
    public String GetDurationString(PERFORMANCE_SECTION section, boolean preventFall) {
        Duration duration = GetDuration(section,preventFall);
        if(duration == null)
            return baseDurationString+invalidResultString;

        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        return baseDurationString+String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
    }
    /**
     * Format Personal Best string -- used for panel
     */
    public String GetPBString(PERFORMANCE_SECTION section) {
        long pb = GetPB(section);
        if(pb == -1)
            return basePBString+invalidResultString;
        return basePBString+ GetKillSpeedFromLong(pb);
    }
    /**
     * Get KC of a given section
     * overall includes current
     */
    public int GetKC(PERFORMANCE_SECTION section){
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        return current ? plugin.currentBossKills : PerformanceStats.overallStats.kc+plugin.currentBossKills;
    }
    /**
     * Get Snipes of a given section
     * overall includes current
     */
    public int GetSnipes(PERFORMANCE_SECTION section){
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        return current ? plugin.currentSnipes : PerformanceStats.overallStats.snipes+plugin.currentSnipes;
    }
    /**
     * Get Personal Best of a given section
     * overall includes current
     */
    public long GetPB(PERFORMANCE_SECTION section){
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        return current ? plugin.currentFastestKill : PerformanceStats.overallStats.pb;
    }
    /**
     * Get Duration of a given section
     * overall includes current
     */
    public Duration GetDuration(PERFORMANCE_SECTION section,boolean preventFall){
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        boolean killStarted = plugin.killStartTime != null;
        Instant start = plugin.currentStartTime;
        Instant now = (preventFall && killStarted) ? plugin.killStartTime : Instant.now();
        if(current && start == null)
            return Duration.ZERO;

        Duration currentDuration = start != null ? Duration.between(start,now) : Duration.ZERO;
        Duration totalDurationBeforeCurrent = PerformanceStats.overallStats.duration;

        return current ? currentDuration : currentDuration.plus(totalDurationBeforeCurrent);
    }

}
