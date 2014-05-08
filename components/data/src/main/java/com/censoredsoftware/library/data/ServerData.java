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

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Generic server data with a table like interface.
 */
public class ServerData implements DataSerializable<UUID>
{
	private UUID id;
	private ServerDataType type;
	private String row;
	private String column;
	private Object value;
	private Long expiration;

	private ServerData()
	{
	}

	/**
	 * DataProvider method for unserializing an instance of this type.
	 *
	 * @param id   The ID.
	 * @param conf The section of a ConfigurationSection to be unserialized.
	 * @return Instance of this type based on data.
	 */
	@DataProvider(idType = DefaultIdType.UUID)
	public static ServerData of(UUID id, ConfigurationSection conf)
	{
		ServerData data = new ServerData();
		data.id = id;
		data.type = ServerDataType.valueOf(conf.getString("type"));
		data.row = conf.getString("row");
		data.column = conf.getString("column");
		data.value = conf.get("value");
		if(conf.get("expiration") != null) data.expiration = conf.getLong("expiration");
		return data;
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", type.name());
		map.put("row", row);
		map.put("column", column);
		map.put("value", value);
		if(expiration != null) map.put("expiration", expiration);
		return map;
	}

	/**
	 * Generate an ID for this class.
	 */
	public void generateId()
	{
		id = UUID.randomUUID();
	}

	/**
	 * Set the type of server data this class is.
	 *
	 * @param type The server data type.
	 */
	public void setType(ServerDataType type)
	{
		this.type = type;
	}

	/**
	 * Set the row of this particular object.
	 *
	 * @param row The row.
	 */
	public void setRow(String row)
	{
		this.row = row;
	}

	/**
	 * Set the column of this particular object.
	 *
	 * @param column The column.
	 */
	public void setColumn(String column)
	{
		this.column = column;
	}

	/**
	 * Set the value of this object.
	 *
	 * @param data The value.
	 */
	public void setValue(Object data)
	{
		if(data instanceof String || data instanceof Integer || data instanceof Boolean || data instanceof Double || data instanceof Map || data instanceof List) this.value = data;
		else if(data == null) this.value = "null";
		else this.value = data.toString();
	}

	/**
	 * Set the expiration time of this object.
	 * THIS WILL ONLY MATTER IF THE TYPE IS 'TIMED'.
	 *
	 * @param unit TimeUnit for the expiration.
	 * @param time Amount of time.
	 */
	public void setExpiration(TimeUnit unit, long time)
	{
		this.expiration = System.currentTimeMillis() + unit.toMillis(time);
	}

	public UUID getId()
	{
		return id;
	}

	/**
	 * Get the type of server data this is.
	 *
	 * @return
	 */
	public ServerDataType getType()
	{
		return type;
	}

	/**
	 * Get the row this data belongs to.
	 *
	 * @return The row.
	 */
	public String getRow()
	{
		return row;
	}

	/**
	 * Get the column this data belongs to.
	 *
	 * @return The column.
	 */
	public String getColumn()
	{
		return column;
	}

	/**
	 * Get the value from this data.
	 *
	 * @return The value.
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Get the time this data will expire.
	 *
	 * @return Expiration time.
	 */
	public Long getExpiration()
	{
		return expiration;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		final ServerData other = (ServerData) obj;
		return Objects.equal(this.id, other.id) && Objects.equal(this.row, other.row) && Objects.equal(this.column, other.column) && Objects.equal(this.value, other.value);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.id, this.row, this.column, this.value);
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this).add("id", this.id).add("row", this.row).add("subkey", this.column).add("value", this.value).toString();
	}

	/**
	 * Get all of the server data.
	 *
	 * @param manager The DataManager.
	 * @return
	 */
	public static Collection<ServerData> all(DataManagerInterface manager)
	{
		return manager.getAllOf(ServerData.class);
	}

	/**
	 * Create and put data into the save.
	 *
	 * @param manager The DataManager.
	 * @param row     The row.
	 * @param column  The column.
	 * @param value   The value.
	 */
	public static void put(DataManagerInterface manager, String row, String column, Object value)
	{
		// Remove the value if it exists already
		remove(manager, row, column);

		// Create and save the timed value
		ServerData timedData = new ServerData();
		timedData.generateId();
		timedData.setType(ServerDataType.PERSISTENT);
		timedData.setRow(row);
		timedData.setColumn(column);
		timedData.setValue(value);
		save(manager, timedData);
	}

	/**
	 * Create and put timed data into the save.
	 *
	 * @param manager The DataManager.
	 * @param row     The row.
	 * @param column  The column.
	 * @param value   The value.
	 * @param time    Amount of time.
	 * @param unit    TimeUnit for the expiration.
	 */
	public static void put(DataManagerInterface manager, String row, String column, Object value, long time, TimeUnit unit)
	{
		// Remove the value if it exists already
		remove(manager, row, column);

		// Create and save the timed value
		ServerData timedData = new ServerData();
		timedData.generateId();
		timedData.setType(ServerDataType.TIMED);
		timedData.setRow(row);
		timedData.setColumn(column);
		timedData.setValue(value);
		timedData.setExpiration(unit, time);
		save(manager, timedData);
	}

	/**
	 * Check if data exists for a row and column.
	 *
	 * @param manager The DataManager.
	 * @param row     The row.
	 * @param column  The column.
	 * @return The data exists.
	 */
	public static boolean exists(DataManagerInterface manager, String row, String column)
	{
		return find(manager, row, column) != null;
	}

	/**
	 * Get value from the save.
	 *
	 * @param manager The DataManager.
	 * @param row     The row.
	 * @param column  The column.
	 * @return The value.
	 */
	public static Object get(DataManagerInterface manager, String row, String column)
	{
		return find(manager, row, column).getValue();
	}

	/**
	 * Find the server data object.
	 *
	 * @param manager The DataManager.
	 * @param row     The row.
	 * @param column  The column.
	 * @return The server data.
	 */
	public static ServerData find(DataManagerInterface manager, String row, String column)
	{
		if(findByRow(manager, row) == null) return null;

		for(ServerData data : findByRow(manager, row))
			if(data.getColumn().equals(column)) return data;

		return null;
	}

	/**
	 * Find all data belonging to a row.
	 *
	 * @param manager The DataManager.
	 * @param row     The row.
	 * @return Set of all data in that row.
	 */
	public static Set<ServerData> findByRow(DataManagerInterface manager, final String row)
	{
		return Sets.newHashSet(Collections2.filter(all(manager), new Predicate<ServerData>()
		{
			@Override
			public boolean apply(ServerData serverData)
			{
				return serverData.getRow().equals(row);
			}
		}));
	}

	/**
	 * Remove data with the row and column.
	 *
	 * @param manager The DataManager.
	 * @param row     The row.
	 * @param column  The column.
	 */
	public static void remove(DataManagerInterface manager, String row, String column)
	{
		if(find(manager, row, column) != null) remove(manager, find(manager, row, column));
	}

	/**
	 * Save the data.
	 *
	 * @param manager The DataManager.
	 * @param data    The data.
	 */
	public static void save(DataManagerInterface manager, ServerData data)
	{
		manager.getMapFor(ServerData.class).put(data.getId(), data);
	}

	/**
	 * Remove the data.
	 *
	 * @param manager The DataManager.
	 * @param data    The data.
	 */
	@SuppressWarnings("SuspiciousMethodCalls")
	public static void remove(DataManagerInterface manager, ServerData data)
	{
		manager.getMapFor(ServerData.class).remove(data);
	}

	/**
	 * Clears all expired timed values.
	 *
	 * @param manager The DataManager.
	 */
	@SuppressWarnings("unchecked")
	public static void clearExpired(DataManagerInterface manager)
	{
		for(ServerData data : Collections2.filter((Collection<ServerData>) (Collection) manager.getAllOf(ServerData.class), new Predicate<ServerData>()
		{
			@Override
			public boolean apply(ServerData data)
			{
				return ServerDataType.TIMED.equals(data.getType()) && data.getExpiration() <= System.currentTimeMillis();
			}
		}))
			remove(manager, data);
	}
}
