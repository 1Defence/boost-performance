package com.boostperformance;

import javax.swing.*;
import net.runelite.client.ui.FontManager;

import java.awt.*;

public class CustomLabel extends JLabel
{
    final Font boldFont  = FontManager.getRunescapeBoldFont();
    public CustomLabel(String text)
    {
        super(text);
        this.setFont(boldFont);
    }

    public CustomLabel()
    {
        super("");
        this.setFont(boldFont);
    }

}
