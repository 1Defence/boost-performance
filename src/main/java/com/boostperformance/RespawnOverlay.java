/*
 * Copyright (c) 2026, 1Defence https://github.com/1Defence
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.boostperformance;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

public class RespawnOverlay extends Overlay
{
    private final Client client;
    private final BoostPerformancePlugin plugin;
    private final BoostPerformanceConfig config;

    private boolean display;
    private Color tileColor;
    private Color textColor;
    private int textSize;
    private int tileWidth;

    @Inject
    private RespawnOverlay(Client client, BoostPerformancePlugin plugin, BoostPerformanceConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if(!display)
            return null;

        int world = client.getWorld();
        Set<RespawnData> respawns = plugin.respawnsActive.get(world);
        if(respawns != null){
            for (RespawnData respawn : respawns)
            {
                if(respawn.respawnLocation == null)
                    continue;

                int size = respawn.size;
                WorldPoint respawnPoint = respawn.respawnLocation;
                LocalPoint lp = LocalPoint.fromWorld(client, respawnPoint.getX(), respawnPoint.getY());
                if(lp != null)
                {
                    final LocalPoint centerLp = new LocalPoint(
                            lp.getX() + Perspective.LOCAL_TILE_SIZE * (size - 1) / 2,
                            lp.getY() + Perspective.LOCAL_TILE_SIZE * (size - 1) / 2);

                    Shape tilePoly = Perspective.getCanvasTileAreaPoly(client, centerLp, size);
                    renderPoly(graphics,tileColor,tilePoly);
                    renderText(graphics,respawn.remainingTicks+"",centerLp,respawnPoint.getPlane());
                }
            }
        }

        return null;
    }

    protected void CacheConfigs(){
        display = config.displayStaticRespawns();
        tileColor = config.respawnTileColor();
        tileWidth = config.respawnTileWidth();
        textColor = config.respawnTextColor();
        textSize = config.respawnTextSize();
    }

    private void renderText(Graphics2D graphics,String text, LocalPoint center,int plane)
    {
        graphics.setFont(new Font("Arial", Font.BOLD, textSize));

        final Point canvasPoint = Perspective
                .localToCanvas(client, center, plane);

        final int textWidth = graphics.getFontMetrics().stringWidth(text);
        final int textHeight = graphics.getFontMetrics().getAscent();

        final Point canvasCenterPoint = new Point(
                canvasPoint.getX() - textWidth / 2,
                canvasPoint.getY() + textHeight / 2);

        int x = canvasCenterPoint.getX();
        int y = canvasPoint.getY();

        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 1);

        graphics.setColor(ColorUtil.colorWithAlpha(textColor, 0xFF));
        graphics.drawString(text, x, y);
    }

    private void renderPoly(Graphics2D graphics, Color outlineColor, Shape polygon)
    {
        if (polygon != null)
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), outlineColor.getAlpha()));
            graphics.setStroke(new BasicStroke((float) tileWidth));
            graphics.draw(polygon);
        }
    }
}