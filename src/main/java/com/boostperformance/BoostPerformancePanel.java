package com.boostperformance;

import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

import static com.boostperformance.BoostPerformancePlugin.PERFORMANCE_SECTION;
public class BoostPerformancePanel extends PluginPanel
{
    BoostPerformancePlugin plugin;

    Utils utils;

    private JPanel panelBossHeader,panelCurrentHeader,panelOverallHeader;
    private RoundedPanel  panelCurrentInfo, panelOverallInfo;
    private InfoLabel labelCurrentInfoKPH, labelCurrentInfoKC, labelCurrentInfoSnipes, labelCurrentInfoEHB, labelCurrentInfoPB,labelCurrentInfoDuration,
            labelOverallInfoKPH, labelOverallInfoKC, labelOverallInfoSnipes, labelOverallInfoEHB, labelOverallInfoPB,labelOverallInfoDuration;
    private HeaderLabel labelBossHeader,labelCurrentHeaderTitle, labelOverallHeaderTitle;
    private JButton buttonCurrentReset, buttonCurrentPause, buttonOverallReset;
    private final Color SUB_PANEL_COLOR_MAIN = new Color(30, 30, 30);
    private final Color SUB_PANEL_COLOR_HEADER = new Color(30, 30, 30);

    private final ImageIcon resetImage = new ImageIcon(ImageUtil.loadImageResource(getClass(), "icon_Reset.png"));
    private final ImageIcon pauseImage = new ImageIcon(ImageUtil.loadImageResource(getClass(), "icon_Pause.png"));
    private final ImageIcon clockImage = new ImageIcon(ImageUtil.loadImageResource(getClass(), "icon_Clock.png"));
    private final static String baseKPHString = "KPH: ";
    private final static String baseKCString = "KC: ";
    private final static String baseSnipeString = "Snipes: ";
    private final static String baseEHBString = "EHB: ";
    private final static  String basePBString = "PB: ";

    BoostPerformancePanel(BoostPerformancePlugin plugin) {
        super();
        this.plugin = plugin;
        this.utils = plugin.utils;
        InitComponents();

        /*Boss Section Start*/
        SetUpHeaderPanel(panelBossHeader,labelBossHeader,"Kill a boss...",45,true);

        /*Current Section Start*/
        SetUpHeaderPanel(panelCurrentHeader,labelCurrentHeaderTitle,"Current",40,false);
        SetUpInfoPanel(panelCurrentInfo,labelCurrentInfoKPH,labelCurrentInfoKC,labelCurrentInfoSnipes,labelCurrentInfoEHB,labelCurrentInfoPB,labelCurrentInfoDuration,buttonCurrentReset,buttonCurrentPause);

        /*Overall Section Start*/
        SetUpHeaderPanel(panelOverallHeader,labelOverallHeaderTitle,"Overall",40,false);
        SetUpInfoPanel(panelOverallInfo,labelOverallInfoKPH,labelOverallInfoKC,labelOverallInfoSnipes,labelOverallInfoEHB,labelOverallInfoPB,labelOverallInfoDuration,buttonOverallReset,null);

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
        labelBossHeader = new HeaderLabel();
        panelCurrentHeader = new JPanel();
        labelCurrentHeaderTitle = new HeaderLabel();
        panelCurrentInfo = new RoundedPanel(16);
        labelCurrentInfoKPH = new InfoLabel();
        labelCurrentInfoKC = new InfoLabel();
        labelCurrentInfoSnipes = new InfoLabel();
        labelCurrentInfoEHB = new InfoLabel();
        labelCurrentInfoPB = new InfoLabel();
        labelCurrentInfoDuration = new InfoLabel();
        buttonCurrentReset = new JButton(resetImage);
        buttonCurrentPause = new JButton(pauseImage);
        panelOverallHeader = new JPanel();
        labelOverallHeaderTitle = new HeaderLabel();
        panelOverallInfo = new RoundedPanel(16);
        labelOverallInfoKPH = new InfoLabel();
        labelOverallInfoKC = new InfoLabel();
        labelOverallInfoSnipes = new InfoLabel();
        labelOverallInfoEHB = new InfoLabel();
        labelOverallInfoPB = new InfoLabel();
        labelOverallInfoDuration = new InfoLabel();
        buttonOverallReset = new JButton(resetImage);

        buttonCurrentReset.setFocusPainted(false);
        buttonCurrentReset.addActionListener(e ->
                plugin.ResetCurrent());
        buttonCurrentPause.setFocusPainted(false);
        buttonCurrentPause.addActionListener(e ->
                plugin.PauseCurrent());

        buttonOverallReset.setFocusPainted(false);
        buttonOverallReset.addActionListener(e ->
                plugin.ResetOverall());

    }

    /**
     * Initial setup of Header panel
     * Sets color,label data and grid layout settings
     *
     */
    private void SetUpHeaderPanel(JPanel headerPanel, HeaderLabel titleLabel, String header, int headerSizeV, boolean hasBorder){
        titleLabel.setText(header);
        if(hasBorder)
        {
            headerPanel.setBackground(SUB_PANEL_COLOR_HEADER);
            titleLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }

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
    private void SetUpInfoPanel(RoundedPanel infoPanel, InfoLabel kph, InfoLabel kc,
                                InfoLabel snipes, InfoLabel ehb, InfoLabel pb,
                                InfoLabel duration, JButton resetButton, JButton pauseButton) {

        infoPanel.setBackground(SUB_PANEL_COLOR_MAIN);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 2, 12));

        kph.setInfo(baseKPHString,"--");
        kc.setInfo(baseKCString,"0");

        snipes.setInfo(baseSnipeString,"0");

        ehb.setInfo(baseEHBString,"--");
        pb.setInfo(basePBString,"--");

        duration.setInfoNoLabel("0:00:00");

        addInfoRow(infoPanel,kph);
        addInfoRow(infoPanel,kc);
        addInfoRow(infoPanel,snipes);
        addInfoRow(infoPanel,ehb);
        addInfoRow(infoPanel,pb);


        //Bottom info bar
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel(clockImage);
        icon.setAlignmentY(Component.CENTER_ALIGNMENT);
        duration.setAlignmentY(Component.CENTER_ALIGNMENT);

        row.add(icon);
        row.add(Box.createHorizontalStrut(6));
        row.add(duration);
        row.add(Box.createHorizontalGlue());

        if(pauseButton != null){
            SwingUtil.removeButtonDecorations(pauseButton);
            row.add(pauseButton);
        }

        SwingUtil.removeButtonDecorations(resetButton);
        row.add(resetButton);

        infoPanel.add(row);

    }

    public void addInfoRow(JPanel panel, JComponent label){
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(label);

        panel.add(row);
        panel.add(Box.createVerticalStrut(4));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(4));
    }

    /**
     * Set KPH Text of given section
     */
    public void SetKPH(PERFORMANCE_SECTION section){
        String kphText = utils.GetKillsPerHourString(section,true);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoKPH.setValue(kphText);
        }else{
            labelOverallInfoKPH.setValue(kphText);
        }
    }
    /**
     * Set KC Text of given section
     */
    public void SetKC(PERFORMANCE_SECTION section){
        String kcText = utils.GetKCString(section);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoKC.setValue(kcText);
        }else{
            labelOverallInfoKC.setValue(kcText);
        }
    }
    /**
     * Set Snipe Text of given section
     */
    public void SetSnipes(PERFORMANCE_SECTION section){
        String snipeText = utils.GetSnipeString(section);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoSnipes.setValue(snipeText);
        }else{
            labelOverallInfoSnipes.setValue(snipeText);
        }
    }
    /**
     * Set EHB Text of given section
     */
    public void SetEHB(PERFORMANCE_SECTION section){
        String ehbText = utils.GetEHBString(section);
        boolean current = section == PERFORMANCE_SECTION.CURRENT;
        if(current){
            labelCurrentInfoEHB.setValue(ehbText);
        }else{
            labelOverallInfoEHB.setValue(ehbText);
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
            labelCurrentInfoPB.setValue(pbText);
        }else{
            labelOverallInfoPB.setValue(pbText);
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
