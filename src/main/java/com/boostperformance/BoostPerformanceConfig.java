package com.boostperformance;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("boostperformance")
public interface BoostPerformanceConfig extends Config
{
    @ConfigItem(
            keyName = "isMain",
            name = "Is Main",
            description = "You should be getting the drops (multiple may be the main, only necessary if main in party)",
            position = 1
    )
    default boolean isMain()
    {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "displayKillMessage",
            name = "Display Kill Message",
            description = "Display game message with information about the kill upon boss death."
    )
    default boolean getDisplayKillMessage()
    {
        return false;
    }

    @ConfigItem(
            position = 3,
            keyName = "displayKillMessage",
            name = "Display Reset Message",
            description = "Display game message when a reset has occured."
    )
    default boolean getDisplayResetMessage()
    {
        return true;
    }

    @ConfigItem(
            keyName = "preventFalloff",
            name = "Prevent Falloff",
            description = "Current & overall duration only update on kill for more accuracy.",
            position = 4
    )
    default boolean getPreventFallOff()
    {
        return true;
    }

    @ConfigItem(
            keyName = "displayRespawns",
            name = "Display Respawns",
            description = "Displays respawns of given bosses without the memory leak issue/inconsistency of dyanmic respawn timers.",
            position = 5
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
            position = 5
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
            position = 6
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
            position = 7
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
            position = 8
    )
    default int respawnTextSize()
    {
        return 22;
    }

}
