package com.censoredsoftware.censoredlib;

import com.censoredsoftware.censoredlib.data.Cache;
import org.bukkit.plugin.java.JavaPlugin;

public class CensoredLibPlugin extends JavaPlugin
{
	public static String SAVE_PATH;

	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		SAVE_PATH = getDataFolder() + "/data/";

		// Load Mojang Id Grabber
		Cache.load(this);
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		Cache.unload();
	}
}
