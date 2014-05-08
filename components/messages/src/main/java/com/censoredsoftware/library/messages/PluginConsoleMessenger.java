/*
 * Copyright 2014 Alex Bennett & Alexander Chauncey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
