package com.boostperformance;

import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;

import static com.boostperformance.BoostPerformancePlugin.PERFORMANCE_SECTION;
public class BoostPerformancePanel extends PluginPanel
{
    BoostPerformancePlugin plugin;

    Utils utils;

    private JPanel panelBossHeader, panelCurrentHeader, panelCurrentInfo, panelOverallHeader, panelOverallInfo;
    private CustomLabel labelBossHeader, labelCurrentHeaderTitle, labelCurrentInfoKPH, labelCurrentInfoKC, labelCurrentInfoSnipes, labelCurrentInfoEHB, labelCurrentInfoPB, labelCurrentInfoDuration,
            labelOverallHeaderTitle, labelOverallInfoKPH, labelOverallInfoKC, labelOverallInfoSnipes, labelOverallInfoEHB, labelOverallInfoPB, labelOverallInfoDuration;
    private JSeparator separatorCurrent, separatorOverall;
    private JButton buttonCurrentReset, buttonOverallReset;

    private final Dimension SUB_PANEL_DIMENSIONS = new Dimension(155,145);
    private final Color SUB_PANEL_COLOR_MAIN = new Color(51, 51, 51);
    private final Color SUB_PANEL_COLOR_HEADER = new Color(25, 25, 25);

    private final Font titleFont = FontManager.getRunescapeBoldFont().deriveFont(20f);

    private final ImageIcon resetImage = new ImageIcon(ImageUtil.loadImageResource(getClass(), "icon_Reset.png"));

    BoostPerformancePanel(BoostPerformancePlugin plugin) {
        super();
        this.plugin = plugin;
        this.utils = plugin.utils;
        InitComponents();

        /*Boss Section Start*/
        SetUpHeaderPanel(panelBossHeader,labelBossHeader,"Kill a boss...",66);

        /*Current Section Start*/
        SetUpHeaderPanel(panelCurrentHeader,labelCurrentHeaderTitle,"Current",40);
        SetUpInfoPanel(panelCurrentInfo,separatorCurrent,labelCurrentInfoKPH,labelCurrentInfoKC,labelCurrentInfoSnipes,labelCurrentInfoEHB,labelCurrentInfoPB,labelCurrentInfoDuration,buttonCurrentReset);

        /*Overall Section Start*/
        SetUpHeaderPanel(panelOverallHeader,labelOverallHeaderTitle,"Overall",40);
        SetUpInfoPanel(panelOverallInfo,separatorOverall,labelOverallInfoKPH,labelOverallInfoKC,labelOverallInfoSnipes,labelOverallInfoEHB,labelOverallInfoPB,labelOverallInfoDuration,buttonOverallReset);

        /*Add all to screen*/
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(panelOverallInfo)
                                        .addComponent(panelOverallHeader)
                                        .addComponent(panelBossHeader)
                                        .addComponent(panelCurrentInfo)
                                        .addComponent(panelCurrentHeader))
                                .addContainerGap()

        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelBossHeader)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelCurrentHeader)
                        .addComponent(panelCurrentInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelOverallHeader)
                        .addComponent(panelOverallInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()
        );
    }

    /**
     * Initiate Components
     *
     */
    void InitComponents(){

        panelBossHeader = new JPanel();
        labelBossHeader = new CustomLabel();
        panelCurrentHeader = new JPanel();
        labelCurrentHeaderTitle = new CustomLabel();
        panelCurrentInfo = new JPanel();
        labelCurrentInfoKPH = new CustomLabel();
        labelCurrentInfoKC = new CustomLabel();
        labelCurrentInfoSnipes = new CustomLabel();
        labelCurrentInfoEHB = new CustomLabel();
        labelCurrentInfoPB = new CustomLabel();
        separatorCurrent = new JSeparator();
        labelCurrentInfoDuration = new CustomLabel();
        buttonCurrentReset = new JButton(resetImage);
        panelOverallHeader = new JPanel();
        labelOverallHeaderTitle = new CustomLabel();
        panelOverallInfo = new JPanel();
        labelOverallInfoKPH = new CustomLabel();
        labelOverallInfoKC = new CustomLabel();
        labelOverallInfoSnipes = new CustomLabel();
        labelOverallInfoEHB = new CustomLabel();
        labelOverallInfoPB = new CustomLabel();
        separatorOverall = new JSeparator();
        labelOverallInfoDuration = new CustomLabel();
        buttonOverallReset = new JButton(resetImage);

        buttonCurrentReset.setFocusPainted(false);
        buttonCurrentReset.addActionListener(e ->
                plugin.ResetCurrent());

        buttonOverallReset.setFocusPainted(false);
        buttonOverallReset.addActionListener(e ->
                plugin.ResetOverall());

    }

    /**
     * Initial setup of Header panel
     * Sets color,label data and grid layout settings
     *
     */
    private void SetUpHeaderPanel(JPanel headerPanel, CustomLabel titleLabel, String header, int headerSizeV){
        headerPanel.setBackground(SUB_PANEL_COLOR_HEADER);
        titleLabel.setData(SwingConstants.CENTER,header,titleFont);

        GroupLayout layout = new GroupLayout(headerPanel);
        headerPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, headerSizeV, Short.MAX_VALUE)
                        )
        );
    }
    /**
     * Initial setup of Info panel
     * Sets color,dimension,label defaults and grid layout settings
     *
     */
    private void SetUpInfoPanel(JPanel infoPanel, Component separator, CustomLabel kph, CustomLabel kc, CustomLabel snipes, CustomLabel ehb, CustomLabel pb, CustomLabel duration, JButton button){

        infoPanel.setBackground(SUB_PANEL_COLOR_MAIN);
        infoPanel.setPreferredSize(SUB_PANEL_DIMENSIONS);


        kph.setText("KPH: --");
        kc.setText("KC: 0");
        snipes.setText("Snipes: 0");
        ehb.setText("EHB: --");
        duration.setText("🕑 0:00:00");
        pb.setText("PB: --");

        GroupLayout gridLayout = new GroupLayout(infoPanel);
        infoPanel.setLayout(gridLayout);

        int buttonSize = 24;

        gridLayout.setHorizontalGroup(
                gridLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(separator)
                        .addGroup(gridLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(gridLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(gridLayout.createSequentialGroup()
                                                .addGroup(gridLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(kph)
                                                        .addComponent(kc)
                                                        .addComponent(snipes)
                                                        .addComponent(ehb)
                                                        .addComponent(pb)
                                                ))
                                        .addGroup(gridLayout.createSequentialGroup()
                                                .addComponent(duration, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(button, GroupLayout.PREFERRED_SIZE, buttonSize, GroupLayout.PREFERRED_SIZE))))
        );
        gridLayout.setVerticalGroup(
                gridLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gridLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(kph)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(kc)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(snipes)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ehb)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pb)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(gridLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(gridLayout.createSequentialGroup()
                                                .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(duration))
                                        .addComponent(button, GroupLayout.PREFERRED_SIZE, buttonSize, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    /**
     * Set KPH Text of given section
     */
    public void SetKPH(PERFORMANCE_SECTION section){
        String kphText = utils.GetKillsPerHourString(section,true);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoKPH.setText(kphText);
        }else{
            labelOverallInfoKPH.setText(kphText);
        }
    }
    /**
     * Set KC Text of given section
     */
    public void SetKC(PERFORMANCE_SECTION section){
        String kcText = utils.GetKCString(section);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoKC.setText(kcText);
        }else{
            labelOverallInfoKC.setText(kcText);
        }
    }
    /**
     * Set Snipe Text of given section
     */
    public void SetSnipes(PERFORMANCE_SECTION section){
        String snipeText = utils.GetSnipeString(section);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoSnipes.setText(snipeText);
        }else{
            labelOverallInfoSnipes.setText(snipeText);
        }
    }
    /**
     * Set EHB Text of given section
     */
    public void SetEHB(PERFORMANCE_SECTION section){
        String ehbText = utils.GetEHBString(section);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoEHB.setText(ehbText);
        }else{
            labelOverallInfoEHB.setText(ehbText);
        }
    }
    /**
     * Set Duration Text of given section
     */
    public void SetDuration(PERFORMANCE_SECTION section, boolean preventFall){
        String durationText = utils.GetDurationString(section,preventFall);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoDuration.setText(durationText);
        }else{
            labelOverallInfoDuration.setText(durationText);
        }
    }
    /**
     * Set PB Text of given section
     */
    public void SetPB(PERFORMANCE_SECTION section){
        String pbText = utils.GetPBString(section);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoPB.setText(pbText);
        }else{
            labelOverallInfoPB.setText(pbText);
        }
    }

    /**
     * Set Boss header to the recent boss name
     * For dks and other potential partner bosses, we generate a name based on the current partners short-names
     * EX dks multi: Dagannoth Rex and Dagannoth Prime would be "Rex,Prime"
     * EX dks single: Dagannoth Rex would be "Dagannoth Rex"
     */
    public void SetBossName(){
        if(plugin.currentPartnerBosses != null){
            labelBossHeader.setText(BossData.GetBossName(plugin.currentPartnerBosses));
        }else
        {
            labelBossHeader.setText(plugin.recentKillName);
        }
    }

    /**
     * Sets Overall section to be invalid/mixed
     * Differing bosses that aren't partners(dks) are tracked in overall
     * To prevent confusion/fake inflated values, indicate to the user that overall tracking has multiple different sessions
     */
    public void SetInvalidOverall(boolean invalid){
        labelOverallHeaderTitle.setText(invalid ? "Mixed Overall" : "Overall");
    }


}
