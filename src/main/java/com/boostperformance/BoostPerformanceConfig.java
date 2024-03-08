package com.boostperformance;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

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

}
