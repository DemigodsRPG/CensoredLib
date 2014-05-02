package com.censoredsoftware.library.component;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.JarFile;

public final class ComponentRegistry
{
	@SuppressWarnings("unchecked")
	private static final Multimap<LoadOrder, Method> METHOD_LOAD_ORDER = Multimaps.newMultimap((Map) new EnumMap<LoadOrder, HashSet<Method>>(LoadOrder.class), new Supplier<Collection<Method>>()
	{
		@Override
		public Set<Method> get()
		{
			return Sets.newHashSet();
		}
	});

	private ComponentRegistry()
	{
	}

	public static void register(File component) throws IOException
	{
		ClassPathHack.addFile(component);
	}

	public static void registerWithType(JarFile file, Class<?> clazz)
	{
		ComponentLoader<?> loader = new ComponentLoader<>(file, clazz);
		loader.initializeComponentClass();
	}

	public static void initMethodLoading(LoadOrder order)
	{
		Collection<Method> methodOrder = METHOD_LOAD_ORDER.get(order);
		for(Method orderedMethod : methodOrder)
		{
			try
			{
				orderedMethod.invoke(null);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			METHOD_LOAD_ORDER.remove(order, orderedMethod);
		}
	}

	static void registerMethodLoadOrder(LoadOrder order, Method method)
	{
		if(METHOD_LOAD_ORDER.containsValue(method)) return;
		METHOD_LOAD_ORDER.put(order, method);
	}
}
