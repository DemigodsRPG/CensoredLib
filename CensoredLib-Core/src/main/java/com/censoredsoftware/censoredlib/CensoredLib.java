package com.censoredsoftware.censoredlib;

import org.bukkit.Bukkit;

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
	{}
}
