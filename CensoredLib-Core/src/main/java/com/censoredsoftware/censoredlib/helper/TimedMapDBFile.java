package com.censoredsoftware.censoredlib.helper;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.TimeUnit;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Easy class for use with the MapDB library.
 * Immutable objects only.
 */
public class TimedMapDBFile
{
	// File name and path
	private final String name, path;

	// All of the data
	private DB data;
	private ConcurrentNavigableMap<Fun.Tuple2<String, Long>, String> string;
	private ConcurrentNavigableMap<Fun.Tuple2<String, Long>, Number> number;
	private ConcurrentNavigableMap<Fun.Tuple2<String, Long>, Boolean> bool;

	// -- CONSTRUCTOR -- //

	public TimedMapDBFile(String fileName, String filePath)
	{
		name = fileName;
		path = filePath;

		File pathFile = new File(path);
		if(!pathFile.exists()) pathFile.mkdirs();

		data = DBMaker.newFileDB(new File(path + name)).asyncWriteEnable().closeOnJvmShutdown().make();

		string = data.createTreeMap("string").keySerializer(BTreeKeySerializer.TUPLE2).makeOrGet();
		number = data.createTreeMap("number").keySerializer(BTreeKeySerializer.TUPLE2).makeOrGet();
		bool = data.createTreeMap("bool").keySerializer(BTreeKeySerializer.TUPLE2).makeOrGet();
	}

	// -- STRING -- //

	public Set<String> stringKeySet()
	{
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		for(Fun.Tuple2<String, Long> key : string.keySet())
			builder.add(key.a);
		return builder.build();
	}

	public Set<String> stringValues()
	{
		return ImmutableSet.copyOf(string.values());
	}

	public boolean stringContainsKey(final String key)
	{
		return string.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).keySet().size() >= 1;
	}

	public String getString(final String key)
	{
		Map.Entry<Fun.Tuple2<String, Long>, String> found = Iterables.find(string.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).entrySet(), new Predicate<Map.Entry<Fun.Tuple2<String, Long>, String>>()
		{
			@Override
			public boolean apply(Map.Entry<Fun.Tuple2<String, Long>, String> entry)
			{
				return entry.getKey().a.equals(key);
			}
		}, null);
		if(found != null) return found.getValue();
		return null;
	}

	public void setString(String key, String value, Integer time, TimeUnit unit)
	{
		if(stringContainsKey(key)) removeString(key);
		string.put(Fun.t2(key, System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit)), value);
	}

	public void removeString(String key)
	{
		if(stringContainsKey(key)) string.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).clear();
	}

	public Long stringExpireInMilli(final String key)
	{
		Map.Entry<Fun.Tuple2<String, Long>, String> found = Iterables.find(string.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).entrySet(), new Predicate<Map.Entry<Fun.Tuple2<String, Long>, String>>()
		{
			@Override
			public boolean apply(Map.Entry<Fun.Tuple2<String, Long>, String> entry)
			{
				return entry.getKey().a.equals(key);
			}
		}, null);
		if(found != null) return found.getKey().b;
		return null;
	}

	// -- NUMBER -- //

	public Set<String> numberKeySet()
	{
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		for(Fun.Tuple2<String, Long> key : number.keySet())
			builder.add(key.a);
		return builder.build();
	}

	public Set<Number> numberValues()
	{
		return ImmutableSet.copyOf(number.values());
	}

	public boolean numberContainsKey(final String key)
	{
		return number.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).keySet().size() >= 1;
	}

	public Number getNumber(final String key)
	{
		Map.Entry<Fun.Tuple2<String, Long>, Number> found = Iterables.find(number.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).entrySet(), new Predicate<Map.Entry<Fun.Tuple2<String, Long>, Number>>()
		{
			@Override
			public boolean apply(Map.Entry<Fun.Tuple2<String, Long>, Number> entry)
			{
				return entry.getKey().a.equals(key);
			}
		}, null);
		if(found != null) return found.getValue();
		return null;
	}

	public void setNumber(String key, Number value, Integer time, TimeUnit unit)
	{
		if(numberContainsKey(key)) removeNumber(key);
		number.put(Fun.t2(key, System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit)), value);
	}

	public void removeNumber(String key)
	{
		if(numberContainsKey(key)) number.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).clear();
	}

	public Long numberExpireInMilli(final String key)
	{
		Map.Entry<Fun.Tuple2<String, Long>, Number> found = Iterables.find(number.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).entrySet(), new Predicate<Map.Entry<Fun.Tuple2<String, Long>, Number>>()
		{
			@Override
			public boolean apply(Map.Entry<Fun.Tuple2<String, Long>, Number> entry)
			{
				return entry.getKey().a.equals(key);
			}
		}, null);
		if(found != null) return found.getKey().b;
		return null;
	}

	// -- BOOL -- //

	public Set<String> boolKeySet()
	{
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		for(Fun.Tuple2<String, Long> key : bool.keySet())
			builder.add(key.a);
		return builder.build();
	}

	public Set<Boolean> boolValues()
	{
		return ImmutableSet.copyOf(bool.values());
	}

	public boolean boolContainsKey(final String key)
	{
		return bool.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).keySet().size() >= 1;
	}

	public Boolean getBool(final String key)
	{
		Map.Entry<Fun.Tuple2<String, Long>, Boolean> found = Iterables.find(bool.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).entrySet(), new Predicate<Map.Entry<Fun.Tuple2<String, Long>, Boolean>>()
		{
			@Override
			public boolean apply(Map.Entry<Fun.Tuple2<String, Long>, Boolean> entry)
			{
				return entry.getKey().a.equals(key);
			}
		}, null);
		if(found != null) return found.getValue();
		return null;
	}

	public void setBool(String key, Boolean value, Integer time, TimeUnit unit)
	{
		if(boolContainsKey(key)) removeBool(key);
		bool.put(Fun.t2(key, System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit)), value);
	}

	public void removeBool(String key)
	{
		if(boolContainsKey(key)) bool.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).clear();
	}

	public Long boolExpireInMilli(final String key)
	{
		Map.Entry<Fun.Tuple2<String, Long>, Boolean> found = Iterables.find(bool.subMap((Fun.Tuple2) Fun.t2(key, null), (Fun.Tuple2) Fun.t2(key, Fun.HI())).entrySet(), new Predicate<Map.Entry<Fun.Tuple2<String, Long>, Boolean>>()
		{
			@Override
			public boolean apply(Map.Entry<Fun.Tuple2<String, Long>, Boolean> entry)
			{
				return entry.getKey().a.equals(key);
			}
		}, null);
		if(found != null) return found.getKey().b;
		return null;
	}

	// -- MISC -- //

	public final void clearExpired()
	{
		long currentTime = System.currentTimeMillis();
		for(Fun.Tuple2<String, Long> tuple : string.keySet())
			if(tuple.b < currentTime) string.remove(tuple);
		for(Fun.Tuple2<String, Long> tuple : number.keySet())
			if(tuple.b < currentTime) number.remove(tuple);
		for(Fun.Tuple2<String, Long> tuple : bool.keySet())
			if(tuple.b < currentTime) bool.remove(tuple);
	}

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
