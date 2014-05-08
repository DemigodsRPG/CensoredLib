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

package com.censoredsoftware.library.data;

import com.google.common.base.Supplier;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporary, thread-safe data organized in a table.
 */
public class TempDataManager
{
	private final Table<String, String, Object> TEMP = Tables.newCustomTable(new ConcurrentHashMap<String, Map<String, Object>>(), new Supplier<ConcurrentHashMap<String, Object>>()
	{
		@Override
		public ConcurrentHashMap<String, Object> get()
		{
			return new ConcurrentHashMap<>();
		}
	});

	/**
	 * Check if the data exists.
	 *
	 * @param row    The row.
	 * @param column The column.
	 * @return Data exists.
	 */
	public boolean exists(String row, String column)
	{
		return TEMP.contains(row, column);
	}

	/**
	 * Get data from row and column.
	 *
	 * @param row    The row.
	 * @param column The column.
	 * @return The value.
	 */
	public Object get(String row, String column)
	{
		if(exists(row, column)) return TEMP.get(row, column);
		else return null;
	}

	/**
	 * Put data.
	 *
	 * @param row    The row.
	 * @param column The column.
	 * @param value  The value.
	 */
	public void put(String row, String column, Object value)
	{
		TEMP.put(row, column, value);
	}

	/**
	 * Remove data.
	 *
	 * @param row    The row.
	 * @param column The column.
	 */
	public void remove(String row, String column)
	{
		if(exists(row, column)) TEMP.remove(row, column);
	}

	/**
	 * Purge all temp data.
	 */
	public void purge()
	{
		TEMP.clear();
	}
}
