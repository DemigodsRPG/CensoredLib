package com.censoredsoftware.censoredlib;

import com.censoredsoftware.censoredlib.util.WorldGuards;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class CensoredLibPlugin extends JavaPlugin
{
	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		CensoredLib.init();
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		// Save WorldGuard Cache
		WorldGuards.saveCurrentCache();

		// Unload anything else
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}
}
