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
		// Init
		CensoredLib.init();

		message("enabled");
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

		message("disabled");
	}

	private void message(String status)
	{
		getLogger().info("  ");
		getLogger().info("             888        d8b   888");
		getLogger().info("             888              888");
		getLogger().info("   .d8888b   888        888   88888b.");
		getLogger().info("  d88P\"      888        888   888 \"88b");
		getLogger().info("  Y88b.      888        888   888 d88P");
		getLogger().info("   \"Y8888P   88888888   888   88888P\"");
		getLogger().info("  ");
		getLogger().info(" ...version " + getDescription().getVersion().replaceAll("SNAPSHOT", "S") + " has " + status + " successfully!");
	}
}
