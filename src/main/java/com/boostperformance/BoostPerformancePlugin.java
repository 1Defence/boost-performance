package com.boostperformance;

import com.boostperformance.messages.BoostPerformanceMemberUpdate;
import com.boostperformance.messages.BoostPerformanceDespawnUpdate;
import com.boostperformance.messages.BoostPerformanceSnipeUpdate;
import com.boostperformance.messages.BoostPerformanceSpawnUpdate;
import com.google.gson.Gson;
import com.google.inject.Provides;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.party.events.UserJoin;
import net.runelite.client.party.events.UserPart;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ExecutorServiceExceptionLogger;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;


@Slf4j
@PluginDescriptor(
	name = "Boost Performance",
	description = "Track performance of a boost session or clan event as a participant or bystander"
)
public class BoostPerformancePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private BoostPerformanceConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private PartyService partyService;

	@Inject
	private WSClient wsClient;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private Gson gson;

	final Color BAD_HIGHLIGHT = new Color(90,90,90);
	final Color GOOD_HIGHLIGHT = new Color(239,16,32);

	final int SECONDS_FOR_GOOD_ESTIMATE = 1200;
	private final String BOSS_DATA_URL = "https://1defence.github.io/resources/data.json";
	private ExecutorService bossDataExecutorService;
	BossDataJson[] fetchedData = null;

	BiMap<Long, String> partyMembers = HashBiMap.create();
	public HashMap<Integer, Set<Integer>> worldsActive = new HashMap<>();

	Instant killStartTime,
			currentStartTime = null;

	String recentKillName;

	long currentFastestKill = -1;
	int currentBossKills,currentSnipes = 0;
	int worldOfPreviousKill,worldOfRecentKill,recentKillId = -1;
	Set<Integer> currentPartnerBosses = null;
	BoostPerformancePanel boostPerformancePanel;
	private NavigationButton navButton;

	public enum PERFORMANCE_SECTION {CURRENT,OVERALL}

	public Utils utils;

	private boolean queuedUpdate = false;
	private String currentLocalUsername;

	@Provides
	BoostPerformanceConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BoostPerformanceConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		currentStartTime = null;
		currentPartnerBosses = null;
		currentBossKills = currentSnipes = 0;
		currentFastestKill = recentKillId = -1;
		worldOfRecentKill = -1;
		worldOfPreviousKill = -1;
		queuedUpdate = true;
		PerformanceStats.Clear(this);
		utils = new Utils(this);

		wsClient.registerMessage(BoostPerformanceSpawnUpdate.class);
		wsClient.registerMessage(BoostPerformanceDespawnUpdate.class);
		wsClient.registerMessage(BoostPerformanceSnipeUpdate.class);
		wsClient.registerMessage(BoostPerformanceMemberUpdate.class);

		bossDataExecutorService = new ExecutorServiceExceptionLogger(Executors.newSingleThreadScheduledExecutor());
		bossDataExecutorService.execute(this::FetchBossData);

		//request a sync from party members, dont inform join, the queuedUpdate will do this.
		SendMemberUpdate(false,false,true,null);
	}

	@Override
	protected void shutDown() throws Exception
	{
		//inform party members to remove us, in future add a heart-beat check to handle X-ing out on the client
		SendMemberUpdate(false,true,false,null);

		wsClient.unregisterMessage(BoostPerformanceSpawnUpdate.class);
		wsClient.unregisterMessage(BoostPerformanceDespawnUpdate.class);
		wsClient.unregisterMessage(BoostPerformanceSnipeUpdate.class);
		wsClient.unregisterMessage(BoostPerformanceMemberUpdate.class);

		clientToolbar.removeNavigation(navButton);

		bossDataExecutorService.shutdown();
		bossDataExecutorService = null;
	}
	/**
	 * Grab up to date EHB rates from github IO page
	 * allows data to stay up to date without redundant update PRs
	 * will in future include all boss data to allow new bosses to be added seemlessly
	 */
	private void FetchBossData(){
		SwingUtilities.invokeLater(() ->
		{
			BufferedReader reader;
			try
			{
				reader = new BufferedReader(new InputStreamReader(new URL(BOSS_DATA_URL).openStream()));
				fetchedData = gson.fromJson(reader, BossDataJson[].class);
				for (BossDataJson data : fetchedData)
				{
					BossData.SetBossEHB(data.getBossSpawnFormId(),data.getEhb());
				}

				//data gathered, can initiate panel.
				InitiatePanel();

			} catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		});


	}

	/**
	 * Build and register the panel
	 */
	void InitiatePanel(){
		boostPerformancePanel = new BoostPerformancePanel(this);

		final BufferedImage icon_panel = ImageUtil.loadImageResource(getClass(), "icon_Panel.png");
		navButton = NavigationButton.builder()
				.tooltip("Boost Performance")
				.icon(icon_panel)
				.priority(1)
				.panel(boostPerformancePanel)
				.build();

		clientToolbar.addNavigation(navButton);
	}
	/**
	 * Party left, reset tracked members
	 */
	@Subscribe
	public void onPartyChanged(PartyChanged partyChanged)
	{
		partyMembers.clear();
	}
	/**
	 * Party member left, remove from list of tracked members
	 */
	@Subscribe
	public void onUserPart(final UserPart event) {
		partyMembers.remove(event.getMemberId());
	}
	/**
	 * Party member joined, request an update for the next registered game tick
	 */
	@Subscribe
	public void onUserJoin(final UserJoin message)
	{
		queuedUpdate = true;
	}
	/**
	 * User changed accounts, request an update for the next registered game tick
	 */
	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged runeScapeProfileChanged)
	{
		queuedUpdate = true;
	}
	/**
	 * Party member requested an update, either joining leaving or syncing, this custom packet ensures the party member is using this plugin.
	 * A traditonal party-leave event is normally good enough for leaves, but this will also cover turning the plugin off
	 */
	@Subscribe
	public void onBoostPerformanceMemberUpdate(final BoostPerformanceMemberUpdate update){
		if(update.isUserJoining())
		{
			partyMembers.put(update.getMemberId(), update.getName());
		}
		else if(update.isUserLeaving())
		{
			partyMembers.remove(update.getMemberId());
		}
		else if(update.isUserRequestingSync()){
			queuedUpdate = true;
		}
	}

	void SendMemberUpdate(boolean userJoining, boolean userLeaving, boolean userRequestingSync, String name){
		if(partyService.getLocalMember() != null)
		{
			partyService.send(new BoostPerformanceMemberUpdate(userJoining,userLeaving,userRequestingSync,name));
		}
	}

	/**
	 * Update personal best kill
	 * returns boolean indicating whether or not the passed killspeed is the new personal best
	 */
	public boolean UpdateFastest(long killSpeed)
	{
		boolean isFastest = false;
		if((killSpeed < currentFastestKill) || currentFastestKill == -1)
		{
			currentFastestKill = killSpeed;
			isFastest = true;
		}
		if((killSpeed < PerformanceStats.overallStats.pb) || PerformanceStats.overallStats.pb  == -1)
		{
			PerformanceStats.overallStats.pb = killSpeed;
			isFastest = true;
		}
		return isFastest;
	}

	/**
	 * Convenience function to send a game message when resetting.
	 */
	void SendResetGameMessage(String contents){
		if(config.getDisplayResetMessage())
		{

			String message = new ChatMessageBuilder()
					.append(ChatColorType.HIGHLIGHT)
					.append(contents)
					.build();
			chatMessageManager.queue(QueuedMessage.builder()
					.type(ChatMessageType.CONSOLE)
					.runeLiteFormattedMessage(message)
					.build());
		}
	}
	/**
	 * Add current to overall stored data
	 * Reset current data and update panel
	 */
	public void ResetCurrent()
	{
		if(!CurrentKillHasStarted())
			return;

		if(currentBossKills != 0)
		{
			PerformanceStats.Add(recentKillId,
					utils.GetKillsPerHourDouble(PERFORMANCE_SECTION.CURRENT, true),
					currentBossKills,
					currentSnipes,
					utils.GetEHBGained(PERFORMANCE_SECTION.CURRENT),
					currentFastestKill,
					currentStartTime,
					killStartTime
					);
		}

		currentStartTime = null;
		currentPartnerBosses = null;
		currentBossKills = currentSnipes = 0;
		currentFastestKill = recentKillId = -1;

		UpdateCurrent(true);
		UpdateOverall(true);
		SendResetGameMessage("Current Kill Speed resetting...");
	}
	/**
	 * Reset overall data and updates panel
	 * Only resets the historical stored data
	 * Doesn't reset current so overall will still be equal to current if present.
	 */
	public void ResetOverall()
	{
		PerformanceStats.Clear(this);
		UpdateCurrent(false);
		UpdateOverall(true);
		SendResetGameMessage("Total Kill Speed resetting...");
		boostPerformancePanel.SetInvalidOverall(false);

	}
	/**
	 * Update panel fields with CURRENT Information
	 */
	public void UpdateCurrent(boolean forceDuration)
	{
		boostPerformancePanel.SetKC(PERFORMANCE_SECTION.CURRENT);
		boostPerformancePanel.SetKPH(PERFORMANCE_SECTION.CURRENT);
		boostPerformancePanel.SetSnipes(PERFORMANCE_SECTION.CURRENT);
		boostPerformancePanel.SetEHB(PERFORMANCE_SECTION.CURRENT);
		boostPerformancePanel.SetPB(PERFORMANCE_SECTION.CURRENT);
		if(config.getPreventFallOff() || forceDuration)
		{
			boostPerformancePanel.SetDuration(PERFORMANCE_SECTION.CURRENT,true);
		}
	}
	/**
	 * Update panel fields with OVERALL Information
	 */
	public void UpdateOverall(boolean forceDuration)
	{
		boostPerformancePanel.SetKC(PERFORMANCE_SECTION.OVERALL);
		boostPerformancePanel.SetKPH(PERFORMANCE_SECTION.OVERALL);
		boostPerformancePanel.SetSnipes(PERFORMANCE_SECTION.OVERALL);
		boostPerformancePanel.SetEHB(PERFORMANCE_SECTION.OVERALL);
		boostPerformancePanel.SetPB(PERFORMANCE_SECTION.OVERALL);
		if(config.getPreventFallOff() || forceDuration)
		{
			boostPerformancePanel.SetDuration(PERFORMANCE_SECTION.OVERALL,true);
		}
	}
	/**
	 * Inform all party members that boss has spawned
	 */
	public void SendBossSpawn(int world, int spawnId)
	{
		if(partyService.getLocalMember() != null)
		{
			partyService.send(new BoostPerformanceSpawnUpdate(world,spawnId));
		}
	}
	/**
	 * Party member saw the boss spawn, send off to attempt processing
	 */
	@Subscribe
	public void onBoostPerformanceSpawnUpdate(BoostPerformanceSpawnUpdate update)
	{
		ProcessBossSpawn(update.getWorld(),update.getSpawnId());
	}
	/**
	 * Checks if a spawn event is allowed to occur
	 * Event can occur if the current world isn't present or the current world isn't tracking this boss in specific
	 */
	public boolean CanSpawn(int world, int spawnId)
	{
		Set<Integer> activeBossIds = worldsActive.get(world);
		if(activeBossIds == null)
			return true;
		return !activeBossIds.contains(spawnId);
	}
	/**
	 * Process spawn packet
	 * Set world active
	 */
	public void ProcessBossSpawn(int world, int spawnId)
	{
		if(CanSpawn(world,spawnId))
		{
			SetWorldActive(world,spawnId);
			//System.out.println("spawn "+world+":"+spawnId);
		}
	}
	/**
	 * Inform all party members that boss has died
	 */
	public void SendBossDeath(int world, String bossName, int spawnId)
	{
		if(partyService.getLocalMember() != null)
		{
			partyService.send(new BoostPerformanceDespawnUpdate(world,bossName,spawnId));
		}
	}
	/**
	 * Party member saw the boss die, send off to attempt processing
	 */
	@Subscribe
	public void onBoostPerformanceDespawnUpdate(BoostPerformanceDespawnUpdate update)
	{
		ProcessBossDeath(update.getWorld(),update.getBossName(),update.getBossSpawnId());
	}
	/**
	 * Checks if a despawn event is allowed to occur
	 * Event can occur if the current world has the current boss spawn tracked
	 */
	public boolean CanDespawn(int world, int spawnId)
	{
		Set<Integer> activeBossIds = worldsActive.get(world);
		if(activeBossIds == null)
			return false;
		return activeBossIds.contains(spawnId);
	}
	/**
	 * Process death packet
	 * First kill is a burner, tracking occurs after this
	 * Update current statistics
	 * Set world inactive
	 */
	public void ProcessBossDeath(int world, String bossName, int spawnId)
	{
		//world already dead
		if(!CanDespawn(world,spawnId))
			return;

		//System.out.println("death "+world+":"+spawnId);

		if(recentKillId != -1 && recentKillId != spawnId){
			BossData boss = BossData.FindSpawnForm(recentKillId);

			boolean hasPartner = boss.hasPartner(spawnId);
			/*Almost all of the following partner code is due to DKS, to handle single dk, or any combination of the 3, as thats what players with >0/3 pets will be doing*/
			if(hasPartner){
				boolean partnerSetUpdated = false;
				if(currentPartnerBosses == null)
				{
					//this kill is a partner of our previous kill, set partner bosses
					//this defaults the set to the previous boss and the current boss, future partner kills will add to this list
					currentPartnerBosses = new HashSet<>(Arrays.asList(recentKillId, spawnId));
					partnerSetUpdated = true;
				}
				else
				{
					if(!currentPartnerBosses.contains(spawnId)){
						//new partner was killed
						currentPartnerBosses.add(spawnId);
						partnerSetUpdated = true;
					}
				}
				if(partnerSetUpdated){
					//set of killed partners has increased, update name to reflect current kill set
					boostPerformancePanel.SetBossName();
				}
			}
			//end of DKS checking
			if(!hasPartner){
				//previously killed boss has no partner therefor this kill requires a reset
				//when a new boss is killed we add the current to overall, and set the header to reflect that it's now "mixed"
				boolean hadKillsTracked = currentBossKills > 0;
				ResetCurrent();
				if(hadKillsTracked)
				{
					boostPerformancePanel.SetInvalidOverall(true);
				}
			}
		}

		this.recentKillName = bossName;
		this.recentKillId = spawnId;
		this.worldOfRecentKill = world;

		SetWorldInactive(world,spawnId);

		String killMessage = "";

		Color highlight = utils.GetElapsedSeconds(PERFORMANCE_SECTION.CURRENT, false) > SECONDS_FOR_GOOD_ESTIMATE ? GOOD_HIGHLIGHT : BAD_HIGHLIGHT;

		if(currentStartTime == null)
		{

			currentStartTime = Instant.now();
			currentBossKills = 0;
			boostPerformancePanel.SetBossName();
			killMessage = new ChatMessageBuilder()
					.append(ChatColorType.HIGHLIGHT)
					.append("Kill Speed now tracking, kill "+bossName+" again to estimate the rate")
					.build();

		}
		else
		{
			currentBossKills++;

			long killSpeedL = utils.GetRecentKillSpeed();
			String killSpeedS = utils.GetKillSpeedFromLong(killSpeedL);
			boolean newFastest = UpdateFastest(killSpeedL);
			String killsPerHour = utils.GetKillsPerHourGameMessage();

			if(config.getDisplayKillMessage())
			{
				killMessage = new ChatMessageBuilder()
						.append(ChatColorType.NORMAL)
						.append("Kills per hour: ")
						.append(highlight, killsPerHour)
						.append(ChatColorType.NORMAL)
						.append(", Fight duration: ")
						.append(ChatColorType.HIGHLIGHT)
						.append(killSpeedS)
						.append(ChatColorType.NORMAL)
						.append(newFastest ? " (new personal best)" : "").build();
			}

		}

		if(!killMessage.isEmpty())
		{
			chatMessageManager.queue(QueuedMessage.builder()
					.type(ChatMessageType.CONSOLE)
					.runeLiteFormattedMessage(killMessage)
					.build());
		}

		killStartTime = Instant.now();
		this.worldOfPreviousKill = world;

		UpdateCurrent(true);
		UpdateOverall(true);
	}

	/**
	 * Only one present player should report the spawn and death of the boss to prevent unnecessary packets
	 * Checks what party members are close enough to see the Boss
	 * Of said close players, determines the lowest PID player
	 * If we're the lowest PID player, we send the packet.
	 */
	boolean ShouldSendPacket(NPC npc)
	{
		Player localPlayer = client.getLocalPlayer();

		if(localPlayer == null)
			return false;
		int lowestCheckedId = Integer.MAX_VALUE;
		BiMap<String, Long> partyMembersByName = partyMembers.inverse();


		for (Player player : client.getPlayers())
		{
			String name = SanitizeName(player.getName());
			if(!partyMembersByName.containsKey(name))
				continue;
			if(!CanSeeNpc(player,npc))
				continue;
			int checkId = player.getId();
			if(checkId >= lowestCheckedId)
				continue;
			lowestCheckedId = checkId;
		}
		if(lowestCheckedId == Integer.MAX_VALUE)
		{
			//the player has just logged in for the very first time on their client, allow a potential extra packet...
			//rare situation, remove if issues in future.
			lowestCheckedId = localPlayer.getId();
		}
		return lowestCheckedId == localPlayer.getId();
	}
	/**
	 * If a player is within 15 tiles of an NPC, they are renderring the boss and can see death/spawn events
	 */
	boolean CanSeeNpc(Player player,NPC npc)
	{
		return IsWithinDistance(player,npc,15);
	}

	/**
	 * Checks the distance accounting for npc size, that a player is away from a given boss.
	 */
	public static boolean IsWithinDistance(Player player, NPC npc, int distance)
	{
		WorldPoint positionA = player.getWorldLocation();
		WorldPoint positionB = npc.getWorldLocation();

		int npcSizeOffSet = npc.getComposition().getSize();
		if(npcSizeOffSet <= 1)
		{
			npcSizeOffSet = 0;
		}

		int deltaX;
		int deltaY;

		if (positionA.getX() >= positionB.getX())
		{
			// Player is positioned in positive X direction, use size
			deltaX = Math.abs(positionA.getX() - (positionB.getX() + npcSizeOffSet));
		}
		else
		{
			// Player is positioned in negative X direction, don't use size
			deltaX = Math.abs(positionA.getX() - positionB.getX());
		}

		if (positionA.getY() >= positionB.getY())
		{
			// Player is positioned in positive Y direction, use size
			deltaY = Math.abs(positionA.getY() - (positionB.getY() + npcSizeOffSet));
		}
		else
		{
			// Player is positioned in negative Y direction, don't use size
			deltaY = Math.abs(positionA.getY() - positionB.getY());
		}

		return deltaX <= distance && deltaY <= distance;
	}

	/**
	 * Indicates that the burner kill has occured and current is tracking statistics.
	 */
	public boolean CurrentKillHasStarted(){
		return currentStartTime != null;
	}
	/**
	 * Remove tags and convert to Jagex name
	 * @param name Local players raw name
	 */
	String SanitizeName(String name)
	{
		return Text.removeTags(Text.toJagexName(name));
	}
	/**
	 * Track that a boss has spawned and is currently present in a specific world
	 */
	public void SetWorldActive(int worldId, int id) {
		worldsActive.computeIfAbsent(worldId, k -> new HashSet<>()).add(id);
	}
	/**
	 * Track that a boss has died and is no longer present in a specific world
	 */
	public void SetWorldInactive(int worldId, int id) {
		worldsActive.computeIfAbsent(worldId, k -> new HashSet<>()).remove(id);
	}

	/**
	 * Under valid conditions, attempt to send packet of a boss dying
	 */
	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();
		if(npc == null)
			return;

		int world = client.getWorld();


		BossData finalBoss = BossData.FindFinalForm(npc.getId());
		if(finalBoss == null)
			return;


		int spawnId = finalBoss.getSpawnFormId();

		if(!CanDespawn(world,spawnId))
			return;

		if(!BossData.IsValidDeath(npc))
			return;

		if(!ShouldSendPacket(npc))
			return;

		SendBossDeath(world, npc.getName(), spawnId);

	}
	/**
	 * Under valid conditions, attempt to send packet of a boss spawning
	 */
	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if(npc == null)
			return;

		int world = client.getWorld();

		int spawnId = npc.getId();

		if(!CanSpawn(world,spawnId))
			return;

		if(!BossData.IsValidBossSpawn(npc))
			return;

		if(!ShouldSendPacket(npc))
			return;

		SendBossSpawn(client.getWorld(),spawnId);

	}

	/**
	 * Inform all party members that someone has sniped
	 */
	public void SendSnipe()
	{
		if(partyService.getLocalMember() != null)
		{
			partyService.send(new BoostPerformanceSnipeUpdate());
		}
	}
	/**
	 * Party member sniped, increment snipes
	 */
	@Subscribe
	public void onBoostPerformanceSnipeUpdate(BoostPerformanceSnipeUpdate update)
	{
		currentSnipes++;
		boostPerformancePanel.SetSnipes(PERFORMANCE_SECTION.CURRENT);
		boostPerformancePanel.SetSnipes(PERFORMANCE_SECTION.OVERALL);
	}
	/**
	 * User gained kill credit, when config dicates that user is not the intended recipient. send packet informing party of our snipe
	 */
	@Subscribe
	public void onChatMessage(ChatMessage event)
	{

		if (event.getType() == ChatMessageType.GAMEMESSAGE)
		{
			if (event.getMessage().contains("kill count is"))
			{
				if (!config.isMain())
				{
					//account has sniped..
					SendSnipe();
				}
			}

		}
	}

	/**
	 * Assuming user wants falloff, we update the current and overall durations
	 */
	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (queuedUpdate && client.getLocalPlayer() != null && partyService.isInParty() && partyService.getLocalMember() != null)
		{
			currentLocalUsername = SanitizeName(client.getLocalPlayer().getName());
			String partyName = partyService.getMemberById(partyService.getLocalMember().getMemberId()).getDisplayName();
			//dont send unless the partyname has updated to the local name
			if (currentLocalUsername != null && currentLocalUsername.equals(partyName))
			{
				//inform party members we have joined, update in the list
				SendMemberUpdate(true,false,false,currentLocalUsername);
				queuedUpdate = false;
			}
		}

		if(!config.getPreventFallOff() && currentStartTime != null)
		{
			boostPerformancePanel.SetDuration(PERFORMANCE_SECTION.CURRENT,false);
			boostPerformancePanel.SetDuration(PERFORMANCE_SECTION.OVERALL,false);
		}
	}
	/**
	 * User changed Falloff settings, update duration
	 */
	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("boostperformance"))
		{
			return;
		}

		if(configChanged.getKey().equals("preventFalloff")){
			boostPerformancePanel.SetDuration(PERFORMANCE_SECTION.CURRENT,config.getPreventFallOff());
			boostPerformancePanel.SetDuration(PERFORMANCE_SECTION.OVERALL,config.getPreventFallOff());
		}

	}

}
