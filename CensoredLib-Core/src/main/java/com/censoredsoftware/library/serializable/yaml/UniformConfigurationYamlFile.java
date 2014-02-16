package com.censoredsoftware.library.serializable.yaml;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Uniform configuration style yaml file, all keys are the same type, and all values are the same type.
 * 
 * @param <K> The type of key (extending comparable rules out most non-yaml friendly types, but not all comparables are safe).
 * @param <V> The type of value.
 */
@SuppressWarnings("unchecked")
public abstract class UniformConfigurationYamlFile<K extends Comparable, V> extends YamlConvertible implements YamlFile
{
	/**
	 * Get the ConfigurationSection being held in the child class.
	 * 
	 * @return The loaded config.
	 */
	public abstract ConcurrentMap<K, V> getLoadedConfig();

	/**
	 * Convert an object into a value we want.
	 * 
	 * @param data Raw object data.
	 * @return Defined value.
	 */
	public abstract V valueFromData(Object data);

	@Override
	public ConcurrentMap<K, V> getCurrentFileData()
	{
		// Grab the current file.
		final FileConfiguration data = YamlFileUtil.getConfiguration(getDirectoryPath(), getFullFileName());

		// Create a new map and convert the raw file data into objects we can understand.
		ConcurrentMap<K, V> map = new ConcurrentHashMap<>();
		for(Map.Entry<String, Object> entry : data.getValues(true).entrySet())
			map.put((K) keyFromString(entry.getKey()), valueFromData(entry.getValue()));
		return map;
	}

	@Override
	public boolean saveDataToFile()
	{
		// Grab the current file and the converted file data map.
		FileConfiguration currentFile = YamlFileUtil.getConfiguration(getDirectoryPath(), getFullFileName());
		final Map<K, V> currentFileMap = getCurrentFileData();

		// Set all new data.
		for(K key : Collections2.filter(getLoadedConfig().keySet(), new Predicate<K>()
		{
			@Override
			public boolean apply(K key)
			{
				return !currentFileMap.containsKey(key) || !currentFileMap.get(key).equals(getLoadedConfig().get(key));
			}
		}))
			currentFile.set(key.toString(), getLoadedConfig().get(key));

		// Remove unneeded data.
		for(K key : Collections2.filter(currentFileMap.keySet(), new Predicate<K>()
		{
			@Override
			public boolean apply(K key)
			{
				return !getLoadedConfig().containsKey(key);
			}
		}))
			currentFile.set(key.toString(), null);

		// Save the config!
		return YamlFileUtil.saveFile(getDirectoryPath(), getFullFileName(), currentFile);
	}

	@Override
	public final V valueFromData(Object... data)
	{
		if(data == null || data.length < 1) return null;
		return valueFromData(data[0]);
	}
}
