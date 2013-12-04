package com.censoredsoftware.censoredlib.helper;

import com.censoredsoftware.censoredlib.CensoredLibPlugin;
import com.censoredsoftware.censoredlib.data.TimedData;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileCriteria;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class MojangIdGrabber
{
	private static final String AGENT = "minecraft";
	private static HttpProfileRepository repository;
	private static File file;

	public static void load(Plugin plugin)
	{
		repository = new HttpProfileRepository();
		file = (new File());
		file.loadToData();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new BukkitRunnable()
		{
			@Override
			public void run()
			{
				// Save data
				MojangIdGrabber.file.saveToFile();
			}
		}, 20, 300 * 20);
	}

	/**
	 * This method requires an OfflinePlayer as an extra step to prevent just passing in random String names.
	 * 
	 * @param player The offline player that we are checking the UUID of.
	 * @return The Mojang UUID.
	 */
	public static String getUUID(OfflinePlayer player)
	{
		// Get the player's name.
		String playerName = player.getName();

		// Check if we already know this Id, of if the name actually belongs to a player.
		if(File.hasTimed(playerName, "mojangAccount")) return File.getTimedValue(playerName, "mojangAccount").toString();
		if(File.hasTimed(playerName, "fakePlayer")) return null;

		// Get the Id from Mojang.
		IdGetTask task = new IdGetTask(playerName);
		task.run();
		if(!task.getStatus())
		{
			File.saveTimed(playerName, "fakePlayer", true, 60);
			return null;
		}
		String id = task.getUUID();

		// Put the player in the known Ids, and return the found Id.
		File.saveTimedWeek(playerName, "mojangAccount", id);
		return id;
	}

	private static class IdGetTask implements Runnable
	{
		private String playerName;
		private String playerId;
		private Boolean status;

		private IdGetTask(String playerName)
		{
			this.playerName = playerName;
		}

		@Override
		public void run()
		{
			Profile[] profiles = repository.findProfilesByCriteria(new ProfileCriteria(playerName, AGENT));
			if(profiles.length == 1)
			{
				playerId = profiles[0].getId();
				status = true;
			}
			else status = false;
		}

		private String getUUID()
		{
			return playerId;
		}

		private Boolean getStatus()
		{
			return status;
		}
	}

	private static class File extends ConfigFile<UUID, TimedData>
	{
		private static ConcurrentMap<UUID, TimedData> timedData = Maps.newConcurrentMap();

		public static TimedData get(UUID id)
		{
			return timedData.get(id);
		}

		public static Set<TimedData> getAll()
		{
			return Sets.newHashSet(timedData.values());
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
			timedData.remove(data.getId());
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
			TimedData timedData = new TimedData();
			timedData.generateId();
			timedData.setKey(key);
			timedData.setSubKey(subKey);
			timedData.setData(data.toString());
			timedData.setSeconds(seconds);
			File.timedData.put(timedData.getId(), timedData);
		}

		public static void saveTimedWeek(String key, String subKey, Object data)
		{
			// Remove the data if it exists already
			remove(key, subKey);

			// Create and save the timed data
			TimedData timedData = new TimedData();
			timedData.generateId();
			timedData.setKey(key);
			timedData.setSubKey(subKey);
			timedData.setData(data.toString());
			timedData.setHours(168);
			File.timedData.put(timedData.getId(), timedData);
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

		@Override
		public TimedData create(UUID uuid, ConfigurationSection conf)
		{
			return new TimedData(uuid, conf);
		}

		@Override
		public ConcurrentMap<UUID, TimedData> getLoadedData()
		{
			return timedData;
		}

		@Override
		public String getSavePath()
		{
			return CensoredLibPlugin.SAVE_PATH;
		}

		@Override
		public String getSaveFile()
		{
			return "timedData.yml";
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
			timedData = loadFromFile();
		}
	}

	;
}
