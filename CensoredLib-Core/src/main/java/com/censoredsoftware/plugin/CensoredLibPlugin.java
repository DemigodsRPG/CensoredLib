package com.censoredsoftware.plugin;

import com.censoredsoftware.library.helper.CensoredJavaPlugin;

public class CensoredLibPlugin extends CensoredJavaPlugin
{
	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		// TODO Submit pull request to Bukkit/Spigot about this:
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

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
		// Uninitialize plugin
		CensoredLib.uninit();

		message("disabled");
	}

	private void message(String status)
	{
		getLogger().info("  ");
		getLogger().info("            888        d8b   888");
		getLogger().info("            888              888");
		getLogger().info("  .d8888b   888        888   88888b.");
		getLogger().info(" d88P\"      888        888   888 \"88b");
		getLogger().info(" Y88b.      888        888   888 d88P");
		getLogger().info("  \"Y8888P   88888888   888   88888P\"");
		getLogger().info("  ");
		getLogger().info(" ...version " + getDescription().getVersion().replaceAll("SNAPSHOT", "S") + " has " + status + " successfully!");
	}
}
