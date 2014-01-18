package com.censoredsoftware.censoredlib.helper;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Easy class for use with the MapDB library.
 * Immutable objects only.
 */
public class MapDBFile
{
	// File name and path
	private final String name, path;

	// All of the data
	private DB data;
	private ConcurrentMap<String, String> string;
	private ConcurrentMap<String, Number> number;
	private ConcurrentMap<String, Boolean> bool;
	private ConcurrentMap<String, List<String>> stringList;
	private ConcurrentMap<String, List<Number>> numberList;
	private ConcurrentMap<String, List<Boolean>> boolList;

	// -- CONSTRUCTOR -- //

	public MapDBFile(String fileName, String filePath)
	{
		name = fileName;
		path = filePath;

		File pathFile = new File(path);
		if(!pathFile.exists()) pathFile.mkdirs();

		data = DBMaker.newFileDB(new File(path + name)).closeOnJvmShutdown().make();

		string = data.createHashMap("string").makeOrGet();
		number = data.createHashMap("number").makeOrGet();
		bool = data.createHashMap("bool").makeOrGet();

		stringList = data.createHashMap("stringList").makeOrGet();
		numberList = data.createHashMap("numberList").makeOrGet();
		boolList = data.createHashMap("boolList").makeOrGet();
	}

	// -- STRING -- //

	public String getString(String key)
	{
		if(string.containsKey(key)) return string.get(key);
		return null;
	}

	public void setString(String key, String value)
	{
		string.put(key, value);
	}

	public void removeString(String key)
	{
		if(string.containsKey(key)) string.remove(key);
	}

	// -- NUMBER -- //

	public Number getNumber(String key)
	{
		if(number.containsKey(key)) return number.get(key);
		return null;
	}

	public void setNumber(String key, Number value)
	{
		number.put(key, value);
	}

	public void removeNumber(String key)
	{
		if(number.containsKey(key)) number.remove(key);
	}

	// -- BOOL -- //

	public Boolean getBool(String key)
	{
		if(bool.containsKey(key)) return bool.get(key);
		return null;
	}

	public void setBool(String key, Boolean value)
	{
		bool.put(key, value);
	}

	public void removeBool(String key)
	{
		if(bool.containsKey(key)) bool.remove(key);
	}

	// -- STRING LIST -- //

	public List<String> getStringList(String key)
	{
		if(stringList.containsKey(key)) return stringList.get(key);
		return null;
	}

	public void setStringList(String key, List<String> value)
	{
		stringList.put(key, value);
	}

	public void removeStringList(String key)
	{
		if(stringList.containsKey(key)) stringList.remove(key);
	}

	// -- NUMBER LIST -- //

	public List<Number> getNumberList(String key)
	{
		if(numberList.containsKey(key)) return numberList.get(key);
		return null;
	}

	public void setNumberList(String key, List<Number> value)
	{
		numberList.put(key, value);
	}

	public void removeNumberList(String key)
	{
		if(numberList.containsKey(key)) numberList.remove(key);
	}

	// -- BOOL LIST -- //

	public List<Boolean> getBoolList(String key)
	{
		if(boolList.containsKey(key)) return boolList.get(key);
		return null;
	}

	public void setBoolList(String key, List<Boolean> value)
	{
		boolList.put(key, value);
	}

	public void removeBoolList(String key)
	{
		if(boolList.containsKey(key)) boolList.remove(key);
	}

	// -- MISC -- //

	public final void save()
	{
		data.commit();
	}

	public final void compact()
	{
		data.compact();
	}

	public final void close()
	{
		data.close();
	}

	public final void clear()
	{
		string.clear();
		number.clear();
		bool.clear();

		stringList.clear();
		numberList.clear();
		boolList.clear();
	}

	public boolean delete()
	{
		clear();
		close();
		return new File(getPath() + getName()).delete();
	}

	public final String getName()
	{
		return name;
	}

	public final String getPath()
	{
		return path;
	}
}