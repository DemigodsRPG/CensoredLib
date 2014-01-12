package com.censoredsoftware.censoredlib.helper;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class ConfigFile4<DATA>
{
	public abstract ConcurrentMap<String, DATA> getLoadedData();

	public abstract String getSavePath();

	public abstract String getSaveFile();

	public abstract DATA convertDataFromObject(Object data);

	public ConcurrentMap<String, DATA> loadFromFile()
	{
		final FileConfiguration data = Util.getData(getSavePath(), getSaveFile());
		ConcurrentHashMap<String, DATA> map = new ConcurrentHashMap<>();
		for(String stringId : data.getKeys(false))
		{
			DATA converted = convertDataFromObject(data.get(stringId));
			if(converted != null) map.put(stringId, converted);
		}
		return map;
	}

	public boolean saveToFile()
	{
		FileConfiguration saveFile = Util.getData(getSavePath(), getSaveFile());
		final Map<String, DATA> currentFile = loadFromFile();

		for(String id : Collections2.filter(getLoadedData().keySet(), new Predicate<String>()
		{
			@Override
			public boolean apply(String id)
			{
				return !currentFile.keySet().contains(id) || !currentFile.get(id).equals(getLoadedData().get(id));
			}
		}))
			saveFile.set(id, getLoadedData().get(id));

		for(String id : Collections2.filter(currentFile.keySet(), new Predicate<String>()
		{
			@Override
			public boolean apply(String id)
			{
				return !getLoadedData().keySet().contains(id);
			}
		}))
			saveFile.set(id, null);

		return Util.saveFile(getSavePath(), getSaveFile(), saveFile);
	}

	public abstract void loadToData();

	public static class Util
	{
		public static FileConfiguration getData(String path, String resource)
		{
			File dataFile = new File(path + resource);
			if(!(dataFile.exists())) createFile(path, resource);
			return YamlConfiguration.loadConfiguration(dataFile);
		}

		public static void createFile(String path, String resource)
		{
			File dataFile = new File(path + resource);
			if(!dataFile.exists())
			{
				try
				{
					(new File(path)).mkdirs();
					dataFile.createNewFile();
				}
				catch(Exception ignored)
				{}
			}
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
	}
}
