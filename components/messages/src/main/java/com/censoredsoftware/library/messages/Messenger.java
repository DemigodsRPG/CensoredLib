package com.censoredsoftware.library.messages;

import org.bukkit.command.CommandSender;

public interface Messenger
{
	Messenger info(String info);

	Messenger warning(String warning);

	Messenger severe(String severe);

	Messenger broadcast(String broadcast);

	Messenger send(CommandSender target, String severe);
}
