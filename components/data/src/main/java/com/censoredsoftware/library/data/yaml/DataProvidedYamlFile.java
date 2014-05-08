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

import com.censoredsoftware.library.data.DataProvider;
import com.censoredsoftware.library.data.DataSerializable;
import com.censoredsoftware.library.data.DataType;
import com.censoredsoftware.library.data.IdType;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * YAML file that utilizes the DataProvider annotation.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 * @param <I> Interface for value (can match value).
 */
public class DataProvidedYamlFile<K, V extends DataSerializable<K>, I> extends YamlConvertible implements YamlFile
{
	private final String name;
	private final String fileName, fileType, savePath;
	ConcurrentMap<K, I> dataStore = Maps.newConcurrentMap();
	private Method providerMethod;
	private IdType idType;

	/**
	 * Create a new file.
	 *
	 * @param dataType  The data type.
	 * @param filePath  The path to the file.
	 * @param name      The internal name of this file.
	 * @param extension The extension this file will be saved as.
	 */
	public DataProvidedYamlFile(final DataType dataType, String filePath, String name, String extension)
	{
		try
		{
			// Look over all constructors in the data class.
			for(Method method : dataType.getDataClass().getMethods())
			{
				// Attempt to find a registered constructor.
				DataProvider methodConstructor = method.getAnnotation(DataProvider.class);

				// Is the constructor suitable for use?
				if(methodConstructor == null || !Modifier.isStatic(method.getModifiers()) || !dataType.getIdType().equals(methodConstructor.idType())) continue;

				// So far so good, now we double check the params.
				Class<?>[] params = method.getParameterTypes();
				if(params.length < 2 || !params[0].equals(dataType.getIdType().getCastClass()) || !params[1].equals(ConfigurationSection.class))
					// No good.
					throw new RuntimeException("The defined constructor for " + dataType.getDataClass().getName() + " is invalid.");

				providerMethod = method;
				break;
			}
		}
		catch(Exception ignored)
		{
		}
		if(providerMethod == null) throw new RuntimeException("Unable to find a constructor for " + dataType.getDataClass().getName() + ".");

		this.name = name;
		this.fileName = dataType.getAbbreviation();
		this.fileType = extension;
		this.savePath = filePath;
		this.idType = dataType.getIdType();
	}

	/**
	 * Helper method to serialize the data.
	 *
	 * @param key The key.
	 * @return Serialised data.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> serialize(K key)
	{
		return ((V) getLoadedData().get(key)).serialize();
	}

	/**
	 * Return all loaded data.
	 *
	 * @return All loaded data.
	 */
	public ConcurrentMap<K, I> getLoadedData()
	{
		return dataStore;
	}

	/**
	 * Create the value from the data.
	 *
	 * @param stringKey The string key representation.
	 * @param conf      The configuration section holding the data.
	 * @return The data as it's interface.
	 */
	@SuppressWarnings("unchecked")
	public I valueFromData(String stringKey, ConfigurationSection conf)
	{
		try
		{
			return (I) providerMethod.invoke(null, keyFromString(stringKey), conf);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public K keyFromString(String stringKey)
	{
		return idType.fromString(stringKey);
	}

	@Override
	public String getDirectoryPath()
	{
		return savePath;
	}

	@Override
	public String getFullFileName()
	{
		return fileName + fileType;
	}

	@Override
	public void loadDataFromFile()
	{
		dataStore = getCurrentFileData();
	}

	@SuppressWarnings("unchecked")
	public ConcurrentMap<K, I> getCurrentFileData()
	{
		// Grab the current file.
		FileConfiguration data = YamlFileUtil.getConfiguration(getDirectoryPath(), getFullFileName());

		// Convert the raw file data into more usable data, in map form.
		ConcurrentHashMap<K, I> map = new ConcurrentHashMap<>();
		for(String stringId : data.getKeys(false))
		{
			try
			{
				map.put((K) keyFromString(stringId), valueFromData(keyFromString(stringId), data.getConfigurationSection(stringId)));
			}
			catch(Exception ignored)
			{
			}
		}
		return map;
	}

	public boolean saveDataToFile()
	{
		// Grab the current file, and its data as a usable map.
		FileConfiguration currentFile = YamlFileUtil.getConfiguration(getDirectoryPath(), getFullFileName());
		final Map<K, I> currentFileMap = getCurrentFileData();

		// Create/overwrite a configuration section if new data exists.
		for(K key : Collections2.filter(getLoadedData().keySet(), new Predicate<K>()
		{
			@Override
			public boolean apply(K key)
			{
				return !currentFileMap.containsKey(key) || !currentFileMap.get(key).equals(getLoadedData().get(key));
			}
		}))
			currentFile.createSection(key.toString(), serialize(key));

		// Remove old unneeded data.
		for(K key : Collections2.filter(currentFileMap.keySet(), new Predicate<K>()
		{
			@Override
			public boolean apply(K key)
			{
				return !getLoadedData().keySet().contains(key);
			}
		}))
			currentFile.set(key.toString(), null);

		// Save the file!
		return YamlFileUtil.saveFile(getDirectoryPath(), getFullFileName(), currentFile);
	}

	public final I valueFromData(Object... data)
	{
		if(data == null || data.length < 3 || !String.class.isInstance(data[0]) || !ConfigurationSection.class.isInstance(data[1])) return null;
		return valueFromData((String) data[0], (ConfigurationSection) data[1]);
	}
}
