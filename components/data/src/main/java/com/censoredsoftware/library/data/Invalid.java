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

import java.util.Map;

/**
 * The invalid data type.
 *
 * @deprecated Do not use this as a data type, it exists solely for testing.
 */
@Deprecated
public class Invalid implements DataSerializable<Void>
{
	@Override
	public Void getId()
	{
		throw new UnsupportedOperationException("Plugin tried accessing non-existent data type.");
	}

	@Override
	public Map<String, Object> serialize()
	{
		throw new UnsupportedOperationException("Plugin tried accessing non-existent data type.");
	}
}
