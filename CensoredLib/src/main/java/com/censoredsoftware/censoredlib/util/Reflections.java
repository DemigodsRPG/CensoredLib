package com.censoredsoftware.censoredlib.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Reflections
{
	public static void setStaticValue(Field field, Object value)
	{
		try
		{
			Field modifiers = Field.class.getDeclaredField("modifiers");

			modifiers.setAccessible(true);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, value);
		}
		catch(Throwable ignored)
		{}
	}

	public static void setPrivateValue(Object instance, String name, Object value)
	{
		try
		{
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(instance, value);
		}
		catch(Throwable ignored)
		{}
	}

	public static Object getPrivateValue(Object instance, String name)
	{
		try
		{
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(instance);
		}
		catch(Throwable ignored)
		{}
		return null;
	}
}
