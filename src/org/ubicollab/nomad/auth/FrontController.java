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

import org.ubicollab.nomad.SpaceManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


//Usage: FrontController.getSpaceManager()
public class FrontController {

	private static FrontController instance;
	private Context context;
	
	private SpaceManager spaceManager;
	private User user;

	private SharedPreferences settings;
	public static final String USER_PREFS = "USER_PREFERENCES";
	
	private FrontController(Context context) {
		this.context = context;
		settings = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
		
//		if (settings.contains(User.USER_PREFS_ACCOUNT_TYPE)) {
//			int type = settings.getInt(User.USER_PREFS_ACCOUNT_TYPE, 0);
//			switch (type) {
//			case User.TYPE_PRIVILEGED:
//				// Do extras for privilged user.
//			case User.TYPE_LOCAL:
//				User user = new LocalUser();
//				user.setFirstname(settings.getString(User.USER_PREFS_FIRSTNAME, "John"));
//				user.setLastname(settings.getString(User.USER_PREFS_LASTNAME, "Doe"));
//				user.setSendBroadcasts(settings.getBoolean(User.USER_PREFS_SEND_BROADCAST, true));
//				user.setRecieveBroadcasts(settings.getBoolean(User.USER_PREFS_RECIEVE_BROADCAST, true));
//				user.setUseSensors(settings.getBoolean(User.USER_PREFS_USE_SENSORS, false));
//				this.user = user;
//			}
//		}
		
		this.spaceManager = SpaceManager.getInstance(context);
		
	}

	public static FrontController getInstance(Context context) throws AuthorizationException {
		if (instance == null) {
			instance = new FrontController(context);
			instance.loginUser(new LocalUser(context));
		}
		return instance;
	}

	
	public boolean isAuthorized() {
		if (settings.getBoolean(User.USER_PREFS_AUTHORIZED, false)) {
			return true;
		} else {
			return false;
		}
	}


	protected void loginUser(User user) throws AuthorizationException {
		
		SharedPreferences.Editor settingsEditor = settings.edit();
		this.clearCredentials();
		
		if (user instanceof PrivilegedUser) {
			// TODO do validation
			settingsEditor.putInt(User.USER_PREFS_ACCOUNT_TYPE, User.TYPE_PRIVILEGED);
			settingsEditor.commit();
			this.user = user;
		} else if (user instanceof LocalUser) {
			settingsEditor.putBoolean(User.USER_PREFS_AUTHORIZED, true);
			settingsEditor.putInt(User.USER_PREFS_ACCOUNT_TYPE, User.TYPE_LOCAL);
			settingsEditor.commit();
			this.user = user;
		} else {
			throw new AuthorizationException("Error during validation of user.");
		}
	}


	public User getUser() {
		if (user != null) {
			return user;
		} else {
			startLoginActivity();
			return null;
			//throw new AuthorizationException("No valid user exists.");
		}
	}
	

	/*
	 * Returns a SpaceManager
	 * @throws AuthorizaitonException if no user is logged in.
	 */
	public SpaceManager getSpaceManager() throws AuthorizationException {
		if (isAuthorized()) {
			return this.spaceManager;
		} else {
			throw new AuthorizationException("User not authorized.");
		}
	}

	public void startLoginActivity() {
		Intent i = new Intent(context, LoginActivity.class);

		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		context.startActivity(i);
	}
	
	public void clearCredentials() {
		SharedPreferences.Editor settingsEditor = settings.edit();
		settingsEditor.clear();
		settingsEditor.commit();
	}
}


