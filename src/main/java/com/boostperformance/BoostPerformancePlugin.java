package com.boostperformance;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

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

	@Override
	protected void startUp() throws Exception
	{

	}

	@Override
	protected void shutDown() throws Exception
	{

	}

	@Provides
	BoostPerformanceConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BoostPerformanceConfig.class);
	}
}
