package com.censoredsoftware.censoredlib.util;

import com.censoredsoftware.censoredlib.CensoredLibPlugin;
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
import java.util.concurrent.ConcurrentMap;

public class WorldGuards implements Listener
{
	private static boolean ENABLED;
	private static ConcurrentMap<String, Flag<?>> flags = Maps.newConcurrentMap();
	private static ConcurrentMap<String, ProtoFlag> protoFlags = Maps.newConcurrentMap();
	private static ConcurrentMap<String, ProtoPVPListener> protoPVPListeners = Maps.newConcurrentMap();

	@EventHandler(priority = EventPriority.MONITOR)
	void onPluginEnable(PluginEnableEvent event)
	{
		if(ENABLED || !event.getPlugin().getName().equals("WorldGuard")) return;
		try
		{
			ENABLED = !(event.getPlugin() instanceof WorldGuardPlugin);
			if(ENABLED) for(Flag<?> flag : flags.values())
				registerFlag(flag);
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
			ENABLED = event.getPlugin() instanceof WorldGuardPlugin;
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

		Bukkit.getScheduler().scheduleAsyncDelayedTask(CensoredLibPlugin.PLUGIN, new Runnable()
		{
			@Override
			public void run()
			{
				Bukkit.getPluginManager().registerEvents(new WorldGuards(), CensoredLibPlugin.PLUGIN);
			}
		}, 40);
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(CensoredLibPlugin.PLUGIN, new Runnable()
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
	}

	public static boolean canWorldGuard()
	{
		return ENABLED;
	}

	/**
	 * @deprecated if you don't have WorldGuard installed this will error.
	 */
	public static Flag<?> getCreatedFlag(String id)
	{
		if(flags.containsKey(id)) return flags.get(id);
		return null;
	}

	/**
	 * @deprecated if you don't have WorldGuard installed this will error.
	 */
	public static Flag<?> getDefaultFlag(String id)
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
		if(getCreatedFlag(flagId) == null) throw new NullPointerException();
		return Iterators.any(WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location).iterator(), new Predicate<ProtectedRegion>()
		{
			@Override
			public boolean apply(ProtectedRegion region)
			{
				try
				{
					Flag flag = getCreatedFlag(flagId);
					if(flag == null) return false;
					Object data = flag.marshal(region.getFlag(flag));
					return data != null && data.equals(value);
				}
				catch(Throwable ignored)
				{
					ignored.printStackTrace(); // TODO Remove.
				}
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
		return Status.SUCCESS;
	}

	public static void setWhenToOverridePVP(Plugin plugin, Predicate<EntityDamageByEntityEvent> predicate)
	{
		if(!canWorldGuard())
		{
			protoPVPListeners.put(plugin.getName(), new ProtoPVPListener(plugin, predicate));
		}
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
}