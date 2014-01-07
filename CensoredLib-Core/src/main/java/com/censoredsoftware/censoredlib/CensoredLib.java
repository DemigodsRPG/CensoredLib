package com.censoredsoftware.censoredlib;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.censoredsoftware.censoredlib.commands.MainCommands;
import com.censoredsoftware.censoredlib.util.WorldGuards;
import com.google.common.collect.Sets;

/**
 * The main class for the CensoredLib plugin.
 * All init and plugin related tasks are done here, this is not part of any API.
 */
public class CensoredLib
{
	private static final CensoredLibPlugin PLUGIN;
	private static final String SAVE_PATH;

	private CensoredLib()
	{}

	static
	{
		PLUGIN = (CensoredLibPlugin) Bukkit.getPluginManager().getPlugin("CensoredLib");
		SAVE_PATH = PLUGIN.getDataFolder() + "/data/";

		// Handle config and oauth
		plugin().getConfig().options().copyDefaults(true);
		plugin().saveConfig();
	}

	public static CensoredLibPlugin plugin()
	{
		return PLUGIN;
	}

	public static String savePath()
	{
		return SAVE_PATH;
	}

	static void init()
	{
		// Commands
		new MainCommands();

		// Update
		if(canUpdate()) update();

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

	private static void update()
	{
		// TODO
		if(canUpdate()) PLUGIN.getLogger().warning("You shouldn't be seeing this!");
	}

	private static boolean canUpdate()
	{
		// TODO
		return false;
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
