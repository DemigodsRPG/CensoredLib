package com.censoredsoftware.censoredlib.helper;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface ConversationManager
{
	public Listener getUniqueListener();

	public Conversation startMenu(Player player);
}
