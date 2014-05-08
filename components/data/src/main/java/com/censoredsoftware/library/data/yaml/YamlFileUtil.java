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

package com.censoredsoftware.library.data.yaml;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Package private utility for common file related methods.
 */
class YamlFileUtil
{
	/**
	 * Private constructor.
	 */
	private YamlFileUtil()
	{
	}

	/**
	 * Get the FileConfiguration at a given location.
	 * If no file exists, create one.
	 *
	 * @param path     The file directory path.
	 * @param fileName The file name.
	 * @return The configuration.
	 */
	static FileConfiguration getConfiguration(String path, String fileName)
	{
		File dataFile = new File(path + fileName);
		if(!(dataFile.exists())) createFile(dataFile);
		return YamlConfiguration.loadConfiguration(dataFile);
	}

	/**
	 * Create a new file.
	 *
	 * @param dataFile The file object.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	static void createFile(File dataFile)
	{
		try
		{
			// Create the directories.
			(dataFile.getParentFile()).mkdirs();

			// Create the new file.
			dataFile.createNewFile();
		}
		catch(Exception errored)
		{
			throw new RuntimeException("CensoredLib couldn't create a data file!", errored);
		}
	}

	/**
	 * Save the file.
	 *
	 * @param path     The file directory path.
	 * @param fileName The file name.
	 * @param conf     The bukkit handled file configuration.
	 * @return Saved successfully.
	 */
	static boolean saveFile(String path, String fileName, FileConfiguration conf)
	{
		try
		{
			conf.save(path + fileName);
			return true;
		}
		catch(Exception ignored)
		{
		}
		return false;
	}
}
