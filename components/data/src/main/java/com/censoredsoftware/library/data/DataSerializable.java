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

import java.util.Map;

/**
 * Simple interface for serializable data.
 *
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
