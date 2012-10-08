/*	Licensed to UbiCollab.org under one or more contributor
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

package org.ubicollab.nomad.auth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class User {

	private boolean sendBroadcasts;
	private boolean recieveBroadcasts;
	private boolean useSensors;
	
	private String firstname;
	private String lastname;
	private int type;
	
	private Context context;
	
	public static final int MODE_ONLINE = 1;
	public static final int MODE_OFFLINE = 2;
	public static final int MODE_USER_PREFERENCES = 3;
	
	public static final int TYPE_LOCAL = 4;
	public static final int TYPE_PRIVILEGED = 5;
	
	public static final String USER_PREFS_ACCOUNT_TYPE = "USER_ACCOUNT_TYPE";
	public static final String USER_PREFS_AUTHORIZED = "USER_AUTH_VALID";
	public static final String USER_PREFS_FIRSTNAME = "USER_FIRSTNAME";
	public static final String USER_PREFS_LASTNAME = "USER_LASTNAME";
	public static final String USER_PREFS_SEND_BROADCAST = "USER_SEND_BROADCAST";
	public static final String USER_PREFS_RECIEVE_BROADCAST = "USER_RECIEVE_BROADCAST";
	public static final String USER_PREFS_USE_SENSORS = "USER_USE_SENSORS";
	
	
	public User(Context context, int type) {
		
		this.context = context;
		this.type = type;
		
		SharedPreferences settings = context.getSharedPreferences(FrontController.USER_PREFS, Activity.MODE_PRIVATE);
		
		switch (type) {
		case User.TYPE_PRIVILEGED:
			// Do extras for privilged user.
		case User.TYPE_LOCAL:
			
			this.firstname = settings.getString(USER_PREFS_FIRSTNAME, "John");
			this.lastname = settings.getString(USER_PREFS_LASTNAME, "Doe");
			this.sendBroadcasts = settings.getBoolean(USER_PREFS_SEND_BROADCAST, true);
			this.recieveBroadcasts = settings.getBoolean(USER_PREFS_RECIEVE_BROADCAST, true);
			this.useSensors = settings.getBoolean(User.USER_PREFS_USE_SENSORS, false);
		}
		
	}

	public void saveSettings() {
		SharedPreferences settings = context.getSharedPreferences(FrontController.USER_PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor settingsEditor = settings.edit();
		settingsEditor.clear();
		
		settingsEditor.putBoolean(USER_PREFS_SEND_BROADCAST, sendBroadcasts);
		settingsEditor.putBoolean(USER_PREFS_RECIEVE_BROADCAST, recieveBroadcasts);
		settingsEditor.putBoolean(USER_PREFS_USE_SENSORS, useSensors);
		settingsEditor.putString(USER_PREFS_FIRSTNAME, firstname);
		settingsEditor.putString(USER_PREFS_LASTNAME, lastname);
		settingsEditor.putInt(USER_PREFS_ACCOUNT_TYPE, type);
		
		settingsEditor.commit();
		//TODO save all settings on per instance base (ie: instance dependig on current user account).
	}
	
	public void setMode(int MODE) {
		
		switch (MODE) {
		case MODE_ONLINE:
			this.sendBroadcasts = true;
			this.recieveBroadcasts = true;
			this.useSensors = true;
			break;
		case MODE_OFFLINE:
			this.sendBroadcasts = false;
			this.recieveBroadcasts = false;
			this.useSensors = false;
			break;
			
		case MODE_USER_PREFERENCES:
		default:
			this.sendBroadcasts = true;
			this.recieveBroadcasts = true;
			this.useSensors = true;
		}
	}
	
	public boolean isSendBroadcasts() {
		return sendBroadcasts;
	}

	public void setSendBroadcasts(boolean sendBroadcasts) {
		this.sendBroadcasts = sendBroadcasts;
	}

	public boolean isRecieveBroadcasts() {
		return recieveBroadcasts;
	}

	public void setRecieveBroadcasts(boolean recieveBroadcasts) {
		this.recieveBroadcasts = recieveBroadcasts;
	}

	public boolean isUseSensors() {
		return useSensors;
	}

	public void setUseSensors(boolean useSensors) {
		this.useSensors = useSensors;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	
}
