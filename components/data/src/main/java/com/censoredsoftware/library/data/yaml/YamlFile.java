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

package com.censoredsoftware.library.data.yaml;

/**
 * Interface for methods that all of our YamlFile classes share.
 */
public interface YamlFile
{
	/**
	 * Method to find the directory this file is located in.
	 *
	 * @return The directory path.
	 */
	String getDirectoryPath();

	/**
	 * Method to find the entire name of this file, including the extension.
	 *
	 * @return The full file name.
	 */
	String getFullFileName();

	/**
	 * This method is intended to use the getCurrentFileData() method to create a cache, or add data to an object.
	 */
	void loadDataFromFile();

	/**
	 * This method returns the data from the file in certain way.
	 *
	 * @param <V> Some value that the file returns.
	 * @return The file's data.
	 */
	<V> V getCurrentFileData();

	/**
	 * Save all relevant data to the file.
	 *
	 * @return Save was successful.
	 */
	boolean saveDataToFile();
}
