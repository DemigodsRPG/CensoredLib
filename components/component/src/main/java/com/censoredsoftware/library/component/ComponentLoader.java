package com.censoredsoftware.library.component;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ComponentLoader<T>
{
	private final JarFile jarFile;
	private final Class<T> registeredComponentType;

	ComponentLoader(JarFile jarFile, Class<T> registeredComponentType)
	{
		this.jarFile = jarFile;
		this.registeredComponentType = registeredComponentType;
	}

	public void initializeComponentClass()
	{
		for(Enumeration<JarEntry> en = jarFile.entries(); en.hasMoreElements(); )
		{
			JarEntry next = en.nextElement();

			// Make sure it's a class
			if(!next.getName().endsWith(".class")) continue;

			Class<?> clazz = null;
			try
			{
				clazz = Class.forName(formatPath(next.getName()), true, registeredComponentType.getClassLoader());
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}

			if(clazz == null || !isComponentClass(clazz)) continue;

			try
			{
				for(Method method : clazz.getMethods())
				{
					LoadableMethod loadable = method.getAnnotation(LoadableMethod.class);
					if(loadable != null)
					{
						LoadOrder loadOrder = loadable.loadOrder();
						ComponentRegistry.registerMethodLoadOrder(loadOrder, method);
					}
				}
			}
			catch(Exception t)
			{
				System.err.print("Error initializing component " + clazz + ": " + t.getMessage());
				t.printStackTrace();
			}
		}
	}

	private String formatPath(String path)
	{
		if(path.length() < 6) return path;
		return path.substring(0, path.length() - 6).replaceAll("/", ".");
	}

	private boolean isComponentClass(Class<?> clazz)
	{
		return registeredComponentType.isAssignableFrom(clazz) && !clazz.isAnnotationPresent(LoadableComponent.class);
	}
}
