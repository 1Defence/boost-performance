package com.boostperformance;

import java.awt.*;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.overlay.infobox.InfoBox;

final class KillcountInfoBox extends InfoBox
{
    private BoostPerformancePlugin plugin;

    KillcountInfoBox(BufferedImage image, BoostPerformancePlugin plugin)
    {
        super(image, plugin);
        this.plugin = plugin;
    }

    @Override
    public String getText()
    {
        int kc = plugin.utils.GetKC(plugin.configInfoDisplay,plugin.configIncludeSnipe);
        if(plugin.configAlwaysShow || kc >= plugin.configHideBeforeKC)
            return Integer.toString(kc);

        return "*";
    }

    @Override
    public Color getTextColor()
    {
        return Color.WHITE;
    }
}
