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

package com.censoredsoftware.library.data;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * Interface defining a DataManager.
 */
public interface DataManagerInterface
{
	/**
	 * Initialize the DataManager.
	 */
	void init();

	/**
	 * Save all data.
	 */
	void save();

	/**
	 * Flush/purge all data.
	 */
	void flushData();

	/**
	 * Get data for a certain data class from a key.
	 *
	 * @param clazz Data class.
	 * @param key   The key.
	 * @param <K>   Key type.
	 * @param <V>   Value type
	 * @param <I>   Interface type (can match value).
	 * @return The data object.
	 */
	<K, V extends DataSerializable<K>, I> I getFor(final Class<V> clazz, final K key);

	/**
	 * Return a collection of all objects belonging to a type.
	 *
	 * @param clazz Data class.
	 * @param <K>   Key type.
	 * @param <V>   Value type.
	 * @param <I>   Interface type (can match value).
	 * @return All of the type.
	 */
	<K, V extends DataSerializable<K>, I> Collection<I> getAllOf(final Class<V> clazz);

	/**
	 * Return the ConcurrentMap that holds the data belonging to a type.
	 *
	 * @param clazz Data class.
	 * @param <K>   Key type.
	 * @param <V>   Value type.
	 * @param <I>   Interface type (can match value).
	 * @return The data map.
	 */
	<K, V extends DataSerializable<K>, I> ConcurrentMap<K, I> getMapFor(final Class<V> clazz);
}
