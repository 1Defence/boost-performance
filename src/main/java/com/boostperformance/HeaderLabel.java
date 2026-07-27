package com.boostperformance;

import net.runelite.client.ui.FontManager;

import javax.swing.*;
import java.awt.*;

public class HeaderLabel extends CustomLabel
{
    private final Font headerFont = FontManager.getRunescapeBoldFont().deriveFont(20f);
    public HeaderLabel(){
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(headerFont);
    }

}
