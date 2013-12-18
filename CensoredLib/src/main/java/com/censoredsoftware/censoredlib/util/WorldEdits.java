package com.censoredsoftware.censoredlib.util;

import com.censoredsoftware.censoredlib.CensoredLibPlugin;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.events.DisallowedPVPEvent;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class WorldEdits implements Listener
{
	private static boolean ENABLED;

	@EventHandler(priority = EventPriority.MONITOR)
	void onPluginEnable(PluginEnableEvent event)
	{
		if(ENABLED || !event.getPlugin().getName().equals("WorldGuard")) return;
		try
		{
			ENABLED = !(event.getPlugin() instanceof WorldGuardPlugin);
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

		Bukkit.getPluginManager().registerEvents(new WorldEdits(), CensoredLibPlugin.PLUGIN);
	}

	public static boolean canWorldGuard()
	{
		return ENABLED;
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

	public static boolean checkForFlag(final Flag<?> flag, Location location)
	{
		return Iterators.any(WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location).iterator(), new Predicate<ProtectedRegion>()
		{
			@Override
			public boolean apply(ProtectedRegion region)
			{
				return Iterators.any(region.getFlags().keySet().iterator(), new Predicate<Flag<?>>()
				{
					@Override
					public boolean apply(Flag<?> found)
					{
						return found.equals(flag);
					}
				});
			}
		});
	}

	public static boolean checkForFlagValue(final Flag<?> flag, final Object value, Location location)
	{
		return Iterators.any(WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location).iterator(), new Predicate<ProtectedRegion>()
		{
			@Override
			public boolean apply(ProtectedRegion region)
			{
				return Iterators.any(region.getFlags().entrySet().iterator(), new Predicate<Map.Entry<Flag<?>, Object>>()
				{
					@Override
					public boolean apply(Map.Entry<Flag<?>, Object> entry)
					{
						return entry.getKey().equals(flag) && entry.getValue().equals(value);
					}
				});
			}
		});
	}

	public class WorldGuardPVPListener implements Listener
	{
		private Predicate<EntityDamageByEntityEvent> checkPVP;

		public WorldGuardPVPListener(Plugin plugin, Predicate<EntityDamageByEntityEvent> checkPVP)
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
}
