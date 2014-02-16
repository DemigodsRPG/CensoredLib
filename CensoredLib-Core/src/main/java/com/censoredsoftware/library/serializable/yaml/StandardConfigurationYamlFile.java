package com.censoredsoftware.library.serializable.yaml;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

/**
 * Standard configuration style yaml file.
 */
@SuppressWarnings("unchecked")
public abstract class StandardConfigurationYamlFile implements YamlFile
{
    /**
     * Method to return the ConfigurationSection held in the child class.
     * @return Loaded config.
     */
    public abstract ConfigurationSection getLoadedConfig();

    @Override
	public FileConfiguration getCurrentFileData()
	{
		return YamlFileUtil.getConfiguration(getDirectoryPath(), getFullFileName());
	}

    @Override
	public boolean saveDataToFile()
	{
        // Grab the current file and it's map.
		final FileConfiguration currentFile = getCurrentFileData();
        final Map<String, Object> currentFileMap = currentFile.getValues(true);

        // Set new values for the file.
		for(String key : getLoadedConfigMap().keySet())
            currentFile.set(key, getLoadedConfig().get(key));

        // Remove any options that aren't needed anymore.
		for(String key : Collections2.filter(currentFileMap.keySet(), new Predicate<String>()
		{
			@Override
			public boolean apply(String key)
			{
				return !getLoadedConfigMap().containsKey(key);
			}
		}))
			currentFile.set(key, null);

        // Save the config.
		return YamlFileUtil.saveFile(getDirectoryPath(), getFullFileName(), currentFile);
	}

    /**
     * Return the map data from the currently loaded config.
     * @return Loaded config data.
     */
    public final Map<String, Object> getLoadedConfigMap()
    {
        return getLoadedConfig().getValues(true);
    }
}
