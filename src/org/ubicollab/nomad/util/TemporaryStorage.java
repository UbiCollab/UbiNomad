/*
	Licensed to UbiCollab.org under one or more contributor
	license agreements.  See the NOTICE file distributed 
	with this work for additional information regarding
	copyright ownership. UbiCollab.org licenses this file
	to you under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance
	with the License. You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing,
	software distributed under the License is distributed on an
	"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
	KIND, either express or implied.  See the License for the
	specific language governing permissions and limitations
	under the License.
*/

package org.ubicollab.nomad.util;

import java.util.HashMap;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;


/**
 * A global accessible singleton used for temporary storage
 * between activity changes or internal state change. Intended
 * usage is to create the notion of 'state' upon activity change.
 * 
 * Usage: By supplying a, for your intended usage, unique identifier
 * test if the current storage exist. If so; retrive it byt getStorage()
 * else create a new one by createStorage() and also supply the type
 * of object you want to store. Remember to always typecast to 
 * desired object type.
 * 
 * It is up to the caller class to clear their storage when finished.
 * Temporary objects are stored as long as the application is running.
 * 
 * For persistant storage of temporary data a user must also call 
 * commit(). (NOT IMPLEMENTED YET!)
 * 
 * @author Samuel Wejeus
 */
public class TemporaryStorage {

	
	private static HashMap<String, Object> instances;
	private static final String TAG = "TemporaryStorage";

	/* Test if a storage container exists for current identifier */
	public static boolean containsStorage(Activity caller) {
		
		String identifier = caller.getClass().toString();
		Log.d(TAG, "Checking if storage contains data for dentifier: \"" + identifier +"\"");
		
		if (instances == null) return false;
		
		if (instances.containsKey(identifier)) {
			Log.d(TAG, "Container got data for dentifier: \"" + identifier +"\" returning true.");
			return true;
		} else {
			return false;
		}
	}
	
	/* Creates a new storage for identifier of wanted type */
	public static Object createStorage(Activity caller, Object object) {
		
		String identifier = caller.getClass().toString();
		
		if (instances == null) {
			Log.d(TAG, "Creating new storage container.");
			instances = new HashMap<String, Object>();
		}
		
		Log.d(TAG, "Identifier: \"" + identifier +"\" putting object: \"" + object +"\"");
		instances.put(identifier, object);
		
		return instances.get(identifier);
	}
	
	/* Retrives storage container for identifier */
	public static Object getStorage(Activity caller) {
		String identifier = caller.getClass().toString();
		
		if (instances.containsKey(identifier)) {
			Log.d(TAG, "Container got data for dentifier: \"" + identifier +"\" returning object: " + instances.get(caller.getClass().toString()).toString());
			return instances.get(caller.getClass().toString());
		} else {
			Log.d(TAG, "Could not retrive data.");
			return false;
		}
		
	}
	
	/* Clears storage container of all data for identifier */
	public static void clearStorage(Activity caller) {
		String identifier = caller.getClass().toString();
		
		if (instances.containsKey(identifier)) {
			Log.d(TAG, "Clearing storage for dentifier: \"" + identifier +"\"");
			instances.remove(identifier);
		}
	}

	/* Persistantly stores data for identifier */
	public static void commit(Activity caller) {
		// TODO persistant storage
	}

}
