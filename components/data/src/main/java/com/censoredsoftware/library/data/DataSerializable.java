package com.censoredsoftware.library.data;

import java.util.Map;

/**
 * Simple interface for serializable data.
 * @param <K> Key.
 */
public interface DataSerializable<K>
{
	/**
	 * Get the ID of the data object.
	 *
	 * @return The ID.
	 */
	K getId();

	/**
	 * Serialize the data held in the child class.
	 *
	 * @return Map of serialized data for the child class's current instance.
	 */
	Map<String, Object> serialize();
}
