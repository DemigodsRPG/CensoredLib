package com.censoredsoftware.censoredlib.data;

import com.censoredsoftware.censoredlib.CensoredLibPlugin;
import com.censoredsoftware.censoredlib.helper.ConfigFile;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class Cache
{
	private static ConcurrentMap<UUID, TimedData> cache = Maps.newConcurrentMap();
	private static CacheFile file;
	private static Integer task, timed;

	public static void load(Plugin plugin)
	{
		file = (new CacheFile());
		file.loadToData();
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new BukkitRunnable()
		{
			@Override
			public void run()
			{
				// Save data
				file.saveToFile();
			}
		}, 20, 300 * 20);

		timed = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new BukkitRunnable()
		{
			@Override
			public void run()
			{
				// Timed
				updateCachedData();
			}
		}, 0, 1);
	}

	public static void unload()
	{
		Bukkit.getScheduler().cancelTask(task);
        Bukkit.getScheduler().cancelTask(timed);
		file.saveToFile();
	}

	public static TimedData get(UUID id)
	{
		return cache.get(id);
	}

	public static Set<TimedData> getAll()
	{
		return Sets.newHashSet(cache.values());
	}

	public static TimedData find(String key, String subKey)
	{
		if(findByKey(key) == null) return null;

		for(TimedData data : findByKey(key))
			if(data.getSubKey().equals(subKey)) return data;

		return null;
	}

	public static Set<TimedData> findByKey(final String key)
	{
		return Sets.newHashSet(Collections2.filter(getAll(), new Predicate<TimedData>()
		{
			@Override
			public boolean apply(TimedData serverData)
			{
				return serverData.getKey().equals(key);
			}
		}));
	}

	public static void delete(TimedData data)
	{
		cache.remove(data.getId());
	}

	public static void remove(String key, String subKey)
	{
		if(find(key, subKey) != null) delete(find(key, subKey));
	}

	public static void saveTimed(String key, String subKey, Object data, Integer seconds)
	{
		// Remove the data if it exists already
		remove(key, subKey);

		// Create and save the timed data
		TimedData cache = new TimedData();
		cache.generateId();
		cache.setKey(key);
		cache.setSubKey(subKey);
		cache.setData(data.toString());
		cache.setSeconds(seconds);
		Cache.cache.put(cache.getId(), cache);
	}

	public static void saveTimedDay(String key, String subKey, Object data)
	{
		// Remove the data if it exists already
		remove(key, subKey);

		// Create and save the timed data
		TimedData cache = new TimedData();
		cache.generateId();
		cache.setKey(key);
		cache.setSubKey(subKey);
		cache.setData(data.toString());
		cache.setHours(24);
		Cache.cache.put(cache.getId(), cache);
	}

	public static void saveTimedWeek(String key, String subKey, Object data)
	{
		// Remove the data if it exists already
		remove(key, subKey);

		// Create and save the timed data
		TimedData cache = new TimedData();
		cache.generateId();
		cache.setKey(key);
		cache.setSubKey(subKey);
		cache.setData(data.toString());
		cache.setHours(168);
		Cache.cache.put(cache.getId(), cache);
	}

	public static void removeTimed(String key, String subKey)
	{
		remove(key, subKey);
	}

	public static boolean hasTimed(String key, String subKey)
	{
		return find(key, subKey) != null;
	}

	public static Object getTimedValue(String key, String subKey)
	{
		return find(key, subKey).getData();
	}

    /**
     * Updates all timed data.
     */
	public static void updateCachedData()
	{
		for(TimedData data : Collections2.filter(getAll(), new Predicate<TimedData>()
		{
			@Override
			public boolean apply(TimedData data)
			{
				return data.getExpiration() <= System.currentTimeMillis();
			}
		}))
			delete(data);
	}

	private static class CacheFile extends ConfigFile<UUID, TimedData>
	{
		@Override
		public TimedData create(UUID uuid, ConfigurationSection conf)
		{
			return new TimedData(uuid, conf);
		}

		@Override
		public ConcurrentMap<UUID, TimedData> getLoadedData()
		{
			return cache;
		}

		@Override
		public String getSavePath()
		{
			return CensoredLibPlugin.SAVE_PATH;
		}

		@Override
		public String getSaveFile()
		{
			return "cache.yml";
		}

		@Override
		public Map<String, Object> serialize(UUID uuid)
		{
			return getLoadedData().get(uuid).serialize();
		}

		@Override
		public UUID convertFromString(String stringId)
		{
			return UUID.fromString(stringId);
		}

		@Override
		public void loadToData()
		{
			cache = loadFromFile();
		}
	}
}
