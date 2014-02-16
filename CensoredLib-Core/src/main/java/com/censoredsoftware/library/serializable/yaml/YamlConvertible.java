package com.censoredsoftware.library.serializable.yaml;

/**
 * A yaml file that has convertible key-value types.
 */
public interface YamlConvertible
{
	/**
	 * Convert a key from a string.
	 * 
	 * @param stringKey The provided string.
	 * @param <K> Key type.
	 * @return The converted key.
	 */
	<K> K keyFromString(String stringKey);

	/**
	 * Convert to a value from a number of objects representing the data.
	 * 
	 * @param data The provided data objects.
	 * @param <V> Value type.
	 * @return The converted value.
	 */
	<V> V valueFromData(Object... data);
}
