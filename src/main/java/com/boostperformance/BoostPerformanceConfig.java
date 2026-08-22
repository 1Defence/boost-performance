package com.boostperformance;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("boostperformance")
public interface BoostPerformanceConfig extends Config
{
    enum INFOBOX_TYPE { CURRENT,OVERALL}

    @ConfigSection(name="General", description="General settings", position=1, closedByDefault=false)
    String generalSection = "general";

    @ConfigSection(name="Respawn", description="Respawn settings", position=2, closedByDefault=true)
    String respawnSection = "respawn";

    @ConfigSection(name="Infobox", description="Infobox settings", position=3, closedByDefault=true)
    String infoboxSection = "infobox";

    @ConfigItem(
            keyName = "isMain",
            name = "Is Main",
            description = "You should be getting the drops (multiple may be the main, only necessary if main in party)",
            position = 1,
            section = generalSection
    )
    default boolean isMain()
    {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "displayKillMessage",
            name = "Display Kill Message",
            description = "Display game message with information about the kill upon boss death.",
            section = generalSection
    )
    default boolean getDisplayKillMessage()
    {
        return false;
    }

    @ConfigItem(
            position = 3,
            keyName = "displayKillMessage",
            name = "Display Reset Message",
            description = "Display game message when a reset has occured.",
            section = generalSection
    )
    default boolean getDisplayResetMessage()
    {
        return true;
    }

    @ConfigItem(
            keyName = "preventFalloff",
            name = "Prevent Falloff",
            description = "Current & overall duration only update on kill for more accuracy.",
            position = 4,
            section = generalSection
    )
    default boolean getPreventFallOff()
    {
        return true;
    }

    @ConfigItem(
            keyName = "displayRespawns",
            name = "Display Respawns",
            description = "Displays respawns of given bosses without the memory leak issue/inconsistency of dyanmic respawn timers.",
            position = 5,
            section = respawnSection
    )
    default boolean displayStaticRespawns()
    {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "tileColor",
            name = "Tile Color",
            description = "Respawn tile color",
            position = 6,
            section = respawnSection
    )
    default Color respawnTileColor()
    {
        return new Color(255,255,255,122);
    }

    @Range(min=0,max = 16)
    @ConfigItem(
            keyName = "tileWidth",
            name = "Tile Width",
            description = "Width of tile outline",
            position = 7,
            section = respawnSection
    )
    default int respawnTileWidth()
    {
        return 2;
    }

    @Alpha
    @ConfigItem(
            keyName = "textColor",
            name = "Text Color",
            description = "Respawn ticks text color",
            position = 8,
            section = respawnSection
    )
    default Color respawnTextColor()
    {
        return Color.orange;
    }

    @Range(min=6,max=40)
    @ConfigItem(
            keyName = "textSize",
            name = "Text Size",
            description = "Respawn ticks text size",
            position = 9,
            section = respawnSection
    )
    default int respawnTextSize()
    {
        return 22;
    }

    @ConfigItem(
            keyName = "showInfobox",
            name = "Show Infobox",
            description = "Show infbox representing kills from the panel",
            position = 10,
            section = infoboxSection
    )
    default boolean showInfobox()
    {
        return false;
    }

    @ConfigItem(
            keyName = "infoboxDisplay",
            name = "Infobox Display",
            description = "Which section from the panel to display",
            position = 11,
            section = infoboxSection
    )
    default INFOBOX_TYPE infoDisplay()
    {
        return INFOBOX_TYPE.CURRENT;
    }

    @ConfigItem(
            keyName = "hideBeforeKC",
            name = "Hide Before KC",
            description = "Only displays the infobox after this many kills.",
            position = 12,
            section = infoboxSection
    )
    default int hideBeforeKC()
    {
        return 100;
    }

    @ConfigItem(
            keyName = "showWithinKCC",
            name = "Show Within KC",
            description = "Only shows when within X kc every 50, (i.e value of 5 will show between 45-55, 95-105 etc)",
            position = 13,
            section = infoboxSection
    )
    default int showWithinKC()
    {
        return 5;
    }

    @ConfigItem(
            keyName = "alwaysShowKC",
            name = "Always Show KC",
            description = "Ignores previous two settings, permanently shows kc (like staring at a clock but you do you)",
            position = 14,
            section = infoboxSection
    )
    default boolean alwaysShowKC()
    {
        return true;
    }

    @ConfigItem(
            keyName = "includeSnipes",
            name = "Include Snipes",
            description = "Include snipes as part of the kc shown (true kc)",
            position = 15,
            section = infoboxSection
    )
    default boolean includeSnipes()
    {
        return true;
    }

}
