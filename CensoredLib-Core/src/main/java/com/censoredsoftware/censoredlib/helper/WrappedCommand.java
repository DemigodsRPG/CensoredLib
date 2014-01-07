package com.censoredsoftware.censoredlib.helper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class WrappedCommand implements TabExecutor
{
	public WrappedCommand(CensoredJavaPlugin plugin, boolean tab)
	{
		for(String command : getCommandNames())
		{
			plugin.getCommand(command).setExecutor(this);
			if(tab) plugin.getCommand(command).setTabCompleter(this);
		}
	}

	public abstract Set<String> getCommandNames();

	public Set<String> getAliases()
	{
		Set<String> set = Sets.newHashSet();
		for(Command command : getCommands())
			set.addAll(command.getAliases());
		return set;
	}

	public Collection<Command> getCommands()
	{
		return Collections2.transform(getCommandNames(), new Function<String, Command>()
		{
			@Override
			public Command apply(String name)
			{
				return Bukkit.getPluginCommand(name);
			}
		});
	}

	public abstract boolean processCommand(CommandSender sender, Command command, final String[] args);

	public List<String> processTab(CommandSender sender, Command command, final String[] args)
	{
		return Lists.newArrayList();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, final String[] args)
	{
		return processTab(sender, command, args);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		return processCommand(sender, command, args);
	}
}
