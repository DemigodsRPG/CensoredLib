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

package com.censoredsoftware.plugin;

import com.censoredsoftware.library.command.CommandManager;
import com.censoredsoftware.library.util.WorldGuards;
import com.censoredsoftware.plugin.command.MainCommands;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

/**
 * The main class for the CensoredLib plugin.
 * All init and plugin related tasks are done here, this is not part of any API.
 */
public class CensoredLib
{
	private static final CensoredLibPlugin PLUGIN;
	private static final CommandManager.Registry COMMAND_REGISTRY;

	private CensoredLib()
	{
	}

	static
	{
		PLUGIN = (CensoredLibPlugin) Bukkit.getPluginManager().getPlugin("CensoredLib");
		COMMAND_REGISTRY = new CommandManager.Registry(PLUGIN);

		// Handle config and oauth
		plugin().getConfig().options().copyDefaults(true);
		plugin().saveConfig();
	}

	public static CensoredLibPlugin plugin()
	{
		return PLUGIN;
	}

	static void init()
	{
		// Enable WorldGuard Util
		new WorldGuards(PLUGIN);

		// Commands
		COMMAND_REGISTRY.registerManager(new MainCommands());

		// Permissions
		loadPermissions(true);
	}

	static void uninit()
	{
		// Save WorldGuard Cache
		WorldGuards.saveCurrentCache();

		// Unload anything else
		HandlerList.unregisterAll(plugin());
		Bukkit.getScheduler().cancelTasks(plugin());

		// Permissions
		loadPermissions(false);
	}

	private static void loadPermissions(boolean load)
	{
		// Plugin manager
		PluginManager manager = Bukkit.getPluginManager();

		// Define permissions
		Set<Permission> permissions = Sets.newHashSet();
		permissions.add(new Permission("censoredlib.basic", PermissionDefault.TRUE));
		permissions.add(new Permission("censoredlib.admin", PermissionDefault.OP));

		// Register/unregister the permission
		for(Permission permission : permissions)
		{
			if(load)
			{
				manager.addPermission(permission);
			}
			else
			{
				manager.removePermission(permission);
			}
		}
	}
}
