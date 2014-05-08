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

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Default and commonly used ID types.
 */
public enum DefaultIdType implements IdType
{
	/**
	 * String.
	 */
	STRING(new Function<String, String>()
	{
		@Override
		public String apply(@Nullable String s)
		{
			return s;
		}
	}, java.lang.String.class),
	/**
	 * UUID.
	 */
	UUID(new Function<String, java.util.UUID>()
	{
		@Override
		public java.util.UUID apply(@Nullable String s)
		{
			return java.util.UUID.fromString(s);
		}
	}, java.util.UUID.class),
	/**
	 * Void (invalid).
	 */
	VOID(new Function<String, Comparable>()
	{
		@Override
		public Comparable apply(@Nullable String s)
		{
			return null;
		}
	}, Comparable.class);

	private Function<String, ?> fromString;
	private Class cast;

	/**
	 * Id data for a data type.
	 *
	 * @param fromString Convert the id from a string.
	 */
	private <K> DefaultIdType(Function<String, K> fromString, Class<K> cast)
	{
		this.fromString = fromString;
		this.cast = cast;
	}

	@Override
	public String toString()
	{
		return name();
	}

	@SuppressWarnings("unchecked")
	public <K> K fromString(String string)
	{
		return (K) fromString.apply(string);
	}

	public Class getCastClass()
	{
		return cast;
	}
}
