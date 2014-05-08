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
			finally
			{
				METHOD_LOAD_ORDER.remove(order, orderedMethod);
			}
		}
	}

	static void registerMethodLoadOrder(LoadOrder order, Method method)
	{
		if(METHOD_LOAD_ORDER.containsValue(method)) return;
		METHOD_LOAD_ORDER.put(order, method);
	}
}
