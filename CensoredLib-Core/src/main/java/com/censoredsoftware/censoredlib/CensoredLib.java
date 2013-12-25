package com.censoredsoftware.censoredlib;

import org.bukkit.Bukkit;

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
		// Update
		if(canUpdate()) update();
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
}
