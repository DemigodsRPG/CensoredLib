package com.censoredsoftware.censoredlib;

import com.censoredsoftware.censoredlib.data.Cache;
import com.censoredsoftware.censoredlib.util.WorldGuards;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class CensoredLibPlugin extends JavaPlugin
{
	public static String SAVE_PATH;
	public static CensoredLibPlugin PLUGIN;

	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		// Access
		SAVE_PATH = getDataFolder() + "/data/";
		PLUGIN = this;

		// Load cache
		Cache.load(this);
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		// Unload cache
		Cache.unload();

		// Save WorldGuard Cache
		WorldGuards.saveCurrentCache();

		// Unload anything else
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}
}
