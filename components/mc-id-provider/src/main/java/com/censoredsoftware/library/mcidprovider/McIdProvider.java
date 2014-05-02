/*
 * Copyright 2014 Alexander Chauncey
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

package com.censoredsoftware.library.mcidprovider;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class McIdProvider
{
	private static final ConcurrentMap<String, UUID> CACHE;
	private static final String AGENT;
	private static final HttpProfileRepository repository;

	static
	{
		AGENT = "minecraft";
		CACHE = new ConcurrentHashMap<String, UUID>();
		repository = new HttpProfileRepository(AGENT);
	}

	private McIdProvider()
	{
	}

	/**
	 * Get the UUID of a player name.
	 *
	 * @param playerName The player name that we are checking the UUID of.
	 * @return The Mojang UUID.
	 */
	public static UUID getId(String playerName)
	{
		if(playerName == null) throw new NullPointerException("Cannot find a player id from null.");

		// Check if we already know this id, of if the name actually belongs to a player.
		if(CACHE.containsKey(playerName)) return CACHE.get(playerName);

		// Find the id.
		String id = find(playerName).length == 1f ? find(playerName)[0].getId() : null;

		// Check for null.
		if(id == null) return null;

		// Put the player in the known ids, and return the found id.
		CACHE.put(playerName, toUUID(id));
		return CACHE.get(playerName);
	}

	/**
	 * Get the UUID of a collection of players..
	 *
	 * @param playerNames The players that we are checking the UUIDs of.
	 * @return The Mojang UUIDs.
	 */
	public static List<UUID> getIds(Collection<String> playerNames)
	{
		if(playerNames == null) throw new NullPointerException("Cannot find player ids from null.");

		// Prevent problems with too many names at once.
		if(playerNames.size() > 100)
		{
			List<UUID> uuids = Lists.newArrayList();

			for(List<String> nameList : Lists.partition(Lists.newArrayList(playerNames), 100))
				uuids.addAll(getIds(nameList));

			return uuids;
		}

		List<UUID> uuids = Lists.newArrayList();
		for(Profile profile : find(playerNames))
		{
			// Find the id.
			String id = profile.getId();

			// Check for null.
			if(id == null) return null;

			// Put the player in the known ids, and return the found id.
			CACHE.put(profile.getName(), toUUID(id));

			// Add to return list.
			uuids.add(toUUID(id));
		}

		return uuids;
	}

	private static Profile[] find(Collection<String> playerNames)
	{
		return find(playerNames.toArray(new String[playerNames.size()]));
	}

	private static Profile[] find(String... playerNames)
	{
		return repository.findProfilesByNames(playerNames);
	}

	private static UUID toUUID(String mojangId)
	{
		// Check if its already in the right format.
		if(mojangId.length() == 36 && mojangId.contains("-")) return UUID.fromString(mojangId);

		// Check that it is long enough.
		if(mojangId.length() != 32) return null;

		// Grab the components from the UUID.
		String component1 = mojangId.substring(0, 8),
				component2 = mojangId.substring(8, 12),
				component3 = mojangId.substring(12, 16),
				component4 = mojangId.substring(16, 20),
				component5 = mojangId.substring(20);

		// Create the new string.
		String fullId = Joiner.on('-').join(component1, component2, component3, component4, component5);

		// Transform into UUID
		return UUID.fromString(fullId);
	}
}
