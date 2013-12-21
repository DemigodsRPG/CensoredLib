package com.censoredsoftware.censoredlib.util;

import com.censoredsoftware.censoredlib.CensoredLibPlugin;
import com.censoredsoftware.censoredlib.helper.ConfigFile;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.events.DisallowedPVPEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Custom flags will not require reflection in WorldGuard 6+, until then we'll use it.
 */
public class WorldGuards implements Listener
{
	private static boolean ENABLED;
	private static ConcurrentMap<String, CustomFlagRegionCache> cacheFiles = Maps.newConcurrentMap();
	private static ConcurrentMap<String, Flag<?>> flags = Maps.newConcurrentMap();
	private static ConcurrentMap<String, ProtoFlag> protoFlags = Maps.newConcurrentMap();
	private static ConcurrentMap<String, ProtoPVPListener> protoPVPListeners = Maps.newConcurrentMap();

	static void callOnEnable()
	{
		for(World world : Bukkit.getWorlds())
		{
			CustomFlagRegionCache cacheFile = new CustomFlagRegionCache(world);
			cacheFile.loadToData();
			if(canWorldGuard()) cacheFile.injectData(world);
		}
	}

	public static void saveCurrentCache()
	{
		for(World world : Bukkit.getWorlds())
		{
			if(!cacheFiles.containsKey(world.getName())) cacheFiles.put(world.getName(), new CustomFlagRegionCache(world));
			CustomFlagRegionCache cacheFile = cacheFiles.get(world.getName());
			if(canWorldGuard()) cacheFile.scrapeData(world);
			cacheFiles.get(world.getName()).saveToFile();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onPluginEnable(PluginEnableEvent event)
	{
		if(ENABLED || !event.getPlugin().getName().equals("WorldGuard")) return;
		try
		{
			ENABLED = event.getPlugin() instanceof WorldGuardPlugin;
			if(ENABLED)
			{
				for(Flag<?> flag : flags.values())
					registerFlag(flag);
				callOnEnable();
			}
		}
		catch(Throwable ignored)
		{}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onPluginDisable(PluginDisableEvent event)
	{
		if(!ENABLED || event.getPlugin().getName().equals("WorldGuard")) return;
		try
		{
            saveCurrentCache();
            ENABLED = false;
		}
		catch(Throwable ignored)
		{}
	}

	static
	{
		try
		{
			ENABLED = Bukkit.getPluginManager().getPlugin("WorldGuard") instanceof WorldGuardPlugin;
		}
		catch(Throwable error)
		{
			ENABLED = false;
		}

		if(CensoredLibPlugin.PLUGIN.isEnabled()) Bukkit.getScheduler().scheduleAsyncDelayedTask(CensoredLibPlugin.PLUGIN, new Runnable()
		{
			@Override
			public void run()
			{
				Bukkit.getPluginManager().registerEvents(new WorldGuards(), CensoredLibPlugin.PLUGIN);
                callOnEnable();
			}
		}, 40);
		if(CensoredLibPlugin.PLUGIN.isEnabled()) Bukkit.getScheduler().scheduleAsyncRepeatingTask(CensoredLibPlugin.PLUGIN, new Runnable()
		{
            @Override
            public void run()
            {
				// process proto-flags
				Iterator<ProtoFlag> protoFlagIterator = protoFlags.values().iterator();
				while(canWorldGuard() && protoFlagIterator.hasNext())
				{
					ProtoFlag queued = protoFlagIterator.next();
					Flag<?> flag = queued.create();
					if(flag != null)
					{
						protoFlags.remove(queued.getId());
						flags.put(queued.getId(), flag);
					}
				}

				// process proto-listeners
				Iterator<ProtoPVPListener> protoPVPListenerIterator = protoPVPListeners.values().iterator();
				while(canWorldGuard() && protoPVPListenerIterator.hasNext())
				{
					ProtoPVPListener queued = protoPVPListenerIterator.next();
					queued.register();
					protoPVPListeners.remove(queued.plugin.getName());
				}
			}
		}, 0, 5);
		if(CensoredLibPlugin.PLUGIN.isEnabled()) Bukkit.getScheduler().scheduleAsyncRepeatingTask(CensoredLibPlugin.PLUGIN, new Runnable()
		{
	@Override
			public void run()
			{
				// save the cache
				saveCurrentCache();
			}
		}, 60, 240);
	}

	public static boolean canWorldGuard()
	{
		return ENABLED;
	}

	/**
	 * @deprecated if you don't have WorldGuard installed this will error.
	 */
	public static Flag<?> getFlag(String id)
	{
		return DefaultFlag.fuzzyMatchFlag(id);
	}

	public static boolean checkForRegion(final String name, Location location)
	{
		return Iterators.any(WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location).iterator(), new Predicate<ProtectedRegion>()
		{
			@Override
			public boolean apply(ProtectedRegion region)
			{
				return region.getId().toLowerCase().contains(name);
			}
		});
	}

	public static boolean checkForFlagValue(final Flag flag, final Object value, Location location)
	{
		return Iterators.any(WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location).iterator(), new Predicate<ProtectedRegion>()
		{
			@Override
			public boolean apply(ProtectedRegion region)
			{
				try
				{
					return flag.marshal(region.getFlag(flag)).equals(value);
				}
				catch(Throwable ignored)
				{}
				return false;
			}
		});
	}

	public static boolean checkForCreatedFlagValue(final String flagId, final Object value, Location location)
	{
		if(getFlag(flagId) == null) throw new NullPointerException();
		return Iterators.any(WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location).iterator(), new Predicate<ProtectedRegion>()
		{
			@Override
			public boolean apply(ProtectedRegion region)
			{
				try
				{
					Flag flag = getFlag(flagId);
					if(flag == null) return false;
					Object data = flag.marshal(region.getFlag(flag));
					return data != null && data.equals(value);
				}
				catch(Throwable ignored)
				{}
				return false;
			}
		});
	}

	public static boolean canBuild(Player player, Location location)
	{
		return WorldGuardPlugin.inst().canBuild(player, location);
	}

	public static boolean canPVP(Location location)
	{
		return checkForFlagValue(DefaultFlag.PVP, "allow", location);
	}

	public static Status createFlag(String type, String id, Object value, String regionGroup)
	{
		if(!canWorldGuard())
		{
			protoFlags.put(id, new ProtoFlag(type, id, value, regionGroup, false));
			return Status.IN_QUEUE;
		}
		try
		{
			flags.put(id, new StateFlag(id, Boolean.valueOf(value.toString()), RegionGroup.valueOf(regionGroup.toUpperCase())));
			return Status.SUCCESS;
		}
		catch(Throwable ignored)
		{}
		return Status.FAILED;
	}

	public static Status registerCreatedFlag(String id)
	{
		if(!canWorldGuard())
		{
			if(protoFlags.containsKey(id))
			{
				ProtoFlag protoFlag = protoFlags.get(id);
				protoFlag.setRegister(true);
				return Status.IN_QUEUE;
			}
			return Status.DOES_NOT_EXIST;
		}
		if(flags.containsKey(id)) return registerFlag(flags.get(id));
		return Status.DOES_NOT_EXIST;
	}

	private static Status registerFlag(final Flag<?> flag)
	{
		if(Iterables.any(Sets.newHashSet(DefaultFlag.getFlags()), new Predicate<Flag<?>>()
		{
			@Override
			public boolean apply(Flag<?> found)
			{
				return found.getName().equals(flag.getName());
			}
		})) return Status.ALREADY_EXISTS;
		try
		{
			Field flagsList = DefaultFlag.class.getField("flagsList");
			List<Flag<?>> registeredFlags = Lists.newArrayList(DefaultFlag.flagsList);
			registeredFlags.add(flag);
			Flag<?>[] override = new Flag<?>[registeredFlags.size()];
			for(int i = 0; i < registeredFlags.size(); i++)
				override[i] = registeredFlags.get(i);
			Reflections.setStaticValue(flagsList, override);
		}
		catch(Exception error)
		{
			return Status.FAILED; // :(
		}
		if(canWorldGuard()) for(World world : Bukkit.getWorlds())
		{
			if(!cacheFiles.containsKey(world.getName())) cacheFiles.put(world.getName(), new CustomFlagRegionCache(world));
			cacheFiles.get(world.getName()).scrapeData(world);
            cacheFiles.get(world.getName()).injectData(world);
		}
		return Status.SUCCESS;
	}

	public static void setWhenToOverridePVP(Plugin plugin, Predicate<EntityDamageByEntityEvent> checkPVP)
	{
		if(!canWorldGuard()) protoPVPListeners.put(plugin.getName(), new ProtoPVPListener(plugin, checkPVP));
		else new WorldGuardPVPListener(plugin, checkPVP);
	}

	static class ProtoFlag
	{
		private String flagType;
		private String id;
		private Object value;
		private String regionGroup;
		private boolean register;

		ProtoFlag(String flagType, String id, Object value, String regionGroup, boolean register)
		{
			this.flagType = flagType;
			this.id = id;
			this.value = value;
			this.regionGroup = regionGroup;
			this.register = register;
		}

		String getFlagType()
		{
			return flagType;
		}

		String getId()
		{
			return id;
		}

		Object getValue()
		{
			return value;
		}

		String getRegionGroup()
		{
			return regionGroup;
		}

		void setRegister(boolean register)
		{
			this.register = register;
		}

		Flag<?> create()
		{
			try
			{
				Flag<?> flag = FlagType.valueOf(flagType.toUpperCase()).convert(this);
				if(flag != null && register) registerFlag(flag);
				return flag;
			}
			catch(Throwable ignored)
			{}
			return null;
		}
	}

	static class ProtoPVPListener
	{
		private Plugin plugin;
		private Predicate<EntityDamageByEntityEvent> checkPVP;

		ProtoPVPListener(Plugin plugin, Predicate<EntityDamageByEntityEvent> checkPVP)
		{
			this.plugin = plugin;
			this.checkPVP = checkPVP;
		}

		void register()
		{
			new WorldGuardPVPListener(plugin, checkPVP);
		}
	}

	public static class WorldGuardPVPListener implements Listener
	{
		private Predicate<EntityDamageByEntityEvent> checkPVP;

		WorldGuardPVPListener(Plugin plugin, Predicate<EntityDamageByEntityEvent> checkPVP)
		{
			this.checkPVP = checkPVP;
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		void onDisallowedPVP(DisallowedPVPEvent event)
		{
			if(checkPVP.apply(event.getCause())) event.setCancelled(true);
		}
	}

	public static enum Status
	{
		FAILED, SUCCESS, IN_QUEUE, ALREADY_EXISTS, DOES_NOT_EXIST
	}

	enum FlagType // TODO Add the rest of the types.
	{
		STATE(new Function<ProtoFlag, Flag<?>>()
		{
			@Override
			public StateFlag apply(ProtoFlag protoFlag)
			{
				try
				{
					return new StateFlag(protoFlag.getId(), Boolean.valueOf(protoFlag.getValue().toString()), RegionGroup.valueOf(protoFlag.getRegionGroup().toUpperCase()));
				}
				catch(Throwable ignored)
				{}
				return null;
			}
		});

		private Function<ProtoFlag, Flag<?>> convert;

		private FlagType(Function<ProtoFlag, Flag<?>> convert)
		{
			this.convert = convert;
		}

		Flag<?> convert(ProtoFlag protoFlag)
		{
			return convert.apply(protoFlag);
		}
	}

    static class RegionCustomFlags implements ConfigurationSerializable
    {
        String regionId;
        String world;
        Map<String, Object> flags;

        RegionCustomFlags(ProtectedRegion region, World world)
        {
            try
            {
                regionId = region.getId();
                this.world = world.getName();
				this.flags = Maps.newHashMap();

				preserveInvalidFlags();

				for(Flag flag : region.getFlags().keySet())
					if(WorldGuards.flags.containsKey(flag.getName())) this.flags.put(flag.getName(), flag.marshal(region.getFlag(flag)));
			}
			catch(Throwable ignored)
			{}
			if(!isEmpty()) addToCache();
		}

		RegionCustomFlags(String regionId, ConfigurationSection conf)
		{
			this.regionId = regionId;
			world = conf.getString("world");
			flags = conf.getConfigurationSection("flags").getValues(false);
		}

		boolean isEmpty()
		{
			return flags.isEmpty();
		}

		Map<Flag<?>, Object> getFlags()
		{
			Map<Flag<?>, Object> map = Maps.newHashMap();
			for(String flagId : flags.keySet())
			{
				Flag<?> found = getFlag(flagId);
				if(found != null) map.put(found, getValue(found));
			}
			return map;
		}

		Object getValue(Flag flag)
		{
			try
			{
				return flag.unmarshal(flags.get(flag.getName()));
			}
			catch(Throwable ignored)
			{}
			return null;
		}

		void preserveInvalidFlags()
		{
			if(cacheFiles.get(world).getLoadedData().containsKey(regionId))
			{
				for(Map.Entry<String, Object> entry : cacheFiles.get(world).getLoadedData().get(regionId).flags.entrySet())
					if(getFlag(entry.getKey()) == null) this.flags.put(entry.getKey(), entry.getValue());
			}
		}

		ProtectedRegion injectIntoRegion(ProtectedRegion region)
		{
			Map<Flag<?>, Object> map = Maps.newHashMap(region.getFlags());
			map.putAll(getFlags());
			region.setFlags(map);
			return region;
		}

		@Override
		public Map<String, Object> serialize()
		{
			Map<String, Object> map = Maps.newHashMap();
			map.put("world", world);
			map.put("flags", flags);
			return map;
		}

		void addToCache()
		{
            CustomFlagRegionCache fdsafdafdsf = cacheFiles.get(world);
            fdsafdafdsf.cache.put(regionId, this);
            cacheFiles.put(world, fdsafdafdsf);
		}
	}

	static class CustomFlagRegionCache extends ConfigFile<String, RegionCustomFlags>
	{
		private ConcurrentMap<String, RegionCustomFlags> cache = Maps.newConcurrentMap();

		private String world;

		CustomFlagRegionCache(World world)
		{
			this.world = world.getName();
		}

		@Override
		public RegionCustomFlags create(String s, ConfigurationSection conf)
		{
			return new RegionCustomFlags(s, conf);
		}

		@Override
		public ConcurrentMap<String, RegionCustomFlags> getLoadedData()
		{
			return cache;
		}

		@Override
		public String getSavePath()
		{
			return WorldGuardPlugin.inst().getDataFolder() + "/worlds/" + world + "/";
		}

		@Override
		public String getSaveFile()
		{
			return "clib.yml";
		}

		@Override
		public Map<String, Object> serialize(String s)
		{
			return cache.get(s).serialize();
		}

		@Override
		public String convertFromString(String stringId)
		{
			return stringId;
		}

		@Override
		public void loadToData()
		{
			cache = loadFromFile();
			cacheFiles.put(world, this);
		}

		protected void scrapeData(World world)
		{
			for(Map.Entry<String, ProtectedRegion> entry : WorldGuardPlugin.inst().getRegionManager(world).getRegions().entrySet())
			{
				if(Iterables.any(entry.getValue().getFlags().keySet(), new Predicate<Flag<?>>()
				{
					@Override
					public boolean apply(Flag<?> flag)
					{
						return flags.containsKey(flag.getName());
					}
				})) new RegionCustomFlags(entry.getValue(), world);
			}
		}

		protected void injectData(World world)
		{
			for(Map.Entry<String, ProtectedRegion> entry : WorldGuardPlugin.inst().getRegionManager(world).getRegions().entrySet())
				if(cache.containsKey(entry.getKey())) cache.get(entry.getKey()).injectIntoRegion(entry.getValue());
		}
	}
}
