package com.censoredsoftware.censoredlib.helper;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.File;
import java.util.Map;

public abstract class ConfigFile2 implements ConfigurationSerializable
{
	public abstract ConfigFile2 unserialize(ConfigurationSection conf);

	public abstract String getSavePath();

	public abstract String getSaveFile();

	public abstract Map<String, Object> serialize();

	public ConfigFile2 loadFromFile()
	{
		FileConfiguration fileData = getData();
		if(fileData != null) return unserialize(fileData);
		return null;
	}

	public FileConfiguration getData()
	{
		File dataFile = new File(getSavePath() + getSaveFile());
		if(!(dataFile.exists()))
		{
			createFile();
			return null;
		}
		return YamlConfiguration.loadConfiguration(dataFile);
	}

	public void createFile()
	{
		File dataFile = new File(getSavePath() + getSaveFile());
		if(!dataFile.exists())
		{
			try
			{
				(new File(getSavePath())).mkdirs();
				dataFile.createNewFile();
			}
			catch(Exception ignored)
			{}
		}
		saveDefaultFile(dataFile);
	}

	public boolean saveToFile()
	{
		FileConfiguration saveFile = getData();

		for(Map.Entry<String, Object> entry : serialize().entrySet())
			saveFile.set(entry.getKey(), entry.getValue());

		return saveFile(getSavePath(), getSaveFile(), saveFile);
	}

	public static boolean saveFile(String path, String resource, FileConfiguration conf)
	{
		try
		{
			conf.save(path + resource);
			return true;
		}
		catch(Exception ignored)
		{}
		return false;
	}

	private boolean saveDefaultFile(File dataFile)
	{
		try
		{
			FileConfiguration saveFile = YamlConfiguration.loadConfiguration(dataFile);
			saveFile.addDefaults(serialize());
			saveFile.options().copyDefaults(true);
			saveFile.save(dataFile);
			return true;
		}
		catch(Exception ignored)
		{}
		return false;
	}
}