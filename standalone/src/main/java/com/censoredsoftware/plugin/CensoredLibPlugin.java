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

package com.censoredsoftware.plugin;

import com.censoredsoftware.library.command.AbstractJavaPlugin;

public class CensoredLibPlugin extends AbstractJavaPlugin
{
	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		// TODO Submit pull request to Bukkit/Spigot about this:
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		// Init
		CensoredLib.init();

		message("enabled");
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		// Uninitialize plugin
		CensoredLib.uninit();

		message("disabled");
	}

	private void message(String status)
	{
		getLogger().info("  ");
		getLogger().info("            888        d8b   888");
		getLogger().info("            888              888");
		getLogger().info("  .d8888b   888        888   88888b.");
		getLogger().info(" d88P\"      888        888   888 \"88b");
		getLogger().info(" Y88b.      888        888   888 d88P");
		getLogger().info("  \"Y8888P   88888888   888   88888P\"");
		getLogger().info("  ");
		getLogger().info(" ...version " + getDescription().getVersion().replaceAll("SNAPSHOT", "S") + " has " + status + " successfully!");
	}
}
