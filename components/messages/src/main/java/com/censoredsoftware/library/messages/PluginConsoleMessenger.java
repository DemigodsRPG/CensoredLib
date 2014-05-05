package com.censoredsoftware.library.messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public class PluginConsoleMessenger implements Messenger
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final String PLUGIN_NAME;

	private PluginConsoleMessenger(String pluginName)
	{
		PLUGIN_NAME = "[" + pluginName + "] ";
	}

	public static PluginConsoleMessenger of(String pluginName)
	{
		return new PluginConsoleMessenger(pluginName);
	}

	@Override
	public final PluginConsoleMessenger info(String info)
	{
		LOGGER.info(PLUGIN_NAME + info);
		return this;
	}

	@Override
	public final PluginConsoleMessenger warning(String warning)
	{
		LOGGER.warning(PLUGIN_NAME + warning);
		return this;
	}

	@Override
	public final PluginConsoleMessenger severe(String severe)
	{
		LOGGER.severe(PLUGIN_NAME + severe);
		return this;
	}

	@Override
	public PluginConsoleMessenger broadcast(String broadcast)
	{
		Bukkit.broadcastMessage(PLUGIN_NAME + broadcast);
		return this;
	}

	@Override
	public PluginConsoleMessenger send(CommandSender target, String severe)
	{
		target.sendMessage(PLUGIN_NAME + severe);
		return this;
	}
}
