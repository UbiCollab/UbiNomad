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

package org.ubicollab.nomad.home;

import org.ubicollab.nomad.util.TabGroupActivity;

import android.content.Intent;
import android.os.Bundle;


public class HomeActivity extends TabGroupActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startChildActivity("HomeScreen", new Intent(this, HomeScreen.class));
	}
}  


//public class HomeActivity extends Activity {
//	private MainDB db;
//
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.home);
//
//	}
//	
//	public void setloginfalse(View v) {
//		SharedPreferences settings = getSharedPreferences(FrontController.USER_PREFS, MODE_WORLD_WRITEABLE);
//		SharedPreferences.Editor settingsEditor = settings.edit();
//
//		settingsEditor.putBoolean(FrontController.USER_AUTH_VALID, false);
//		settingsEditor.commit();
//		
//		Toast.makeText(this, "User have been logged out", Toast.LENGTH_LONG).show();
//	}
//}
