/*
 * Copyright 2014 Alex Bennett & Alexander Chauncey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.censoredsoftware.library.command;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface ConversationManager
{
	public Listener getUniqueListener();

	public Conversation startMenu(Player player);

	// Can't touch this. Naaaaaa na-na-na.. Ba-dum, ba-dum.
	public static interface Category extends Prompt
	{
		public String getChatName(ConversationContext context);

		public boolean canUse(ConversationContext context);
	}
}