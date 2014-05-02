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
