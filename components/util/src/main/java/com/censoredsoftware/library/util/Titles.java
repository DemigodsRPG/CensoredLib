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

package com.censoredsoftware.library.util;

import com.censoredsoftware.library.messages.CommonSymbol;

public class Titles
{
	/**
	 * Returns a formatted title ready for the chat.
	 *
	 * @param title the title to format
	 * @return the formatted title
	 */
	public static String chatTitle(String title)
	{
		int total = 86;
		String chatTitle = " " + CommonSymbol.RIGHTWARD_ARROW + " " + title + " ";
		for(int i = 0; i < (total - chatTitle.length()); i++)
			chatTitle += "-";
		return chatTitle;
	}
}
