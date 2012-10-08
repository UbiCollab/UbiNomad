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

package org.ubicollab.nomad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ubicollab.nomad.auth.AuthorizationException;
import org.ubicollab.nomad.auth.FrontController;
import org.ubicollab.nomad.auth.User;
import org.ubicollab.nomad.debug.DebugActivity;
import org.ubicollab.nomad.discover.DiscoverGroup;
import org.ubicollab.nomad.home.HomeActivity;
import org.ubicollab.nomad.myspaces.MySpacesActivityGroup;
import org.ubicollab.nomad.util.ImportActivity;
import org.ubicollab.nomad.util.XmlSpaceListWriter;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

public class Nomad extends TabActivity {

	FrontController frontController;
	public static TabHost tabHost;



	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost);
		Log.i("Nomad", "onCreate");

		// Show login screen.
		try {
			frontController = FrontController.getInstance(getApplicationContext());
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if (!frontController.isAuthorized()) {
			frontController.startLoginActivity();
		}	

		tabHost = getTabHost();
		initTabHost();
		//tabHost.setSaveEnabled(false);
		setTabColor(tabHost);
		tabHost.setCurrentTab(1);
	}

	@Override
	protected void onNewIntent (Intent intent) {
		super.onNewIntent(intent);
		tabHost.setCurrentTab(1);
	}

	public void initTabHost() {
		Resources res = getResources(); 	// Resource object to get Drawables 
		TabHost.TabSpec spec; 				// Resusable TabSpec for each tab
		Intent intent; 						// Reusable Intent for each tab


		//Initialize a TabSpec for each tab and add it to the TabHost
		intent = new Intent().setClass(this, HomeActivity.class);
		spec = tabHost.newTabSpec("Home").setIndicator("Home", res.getDrawable(R.drawable.ic_tab_home)).setContent(intent);
		tabHost.addTab(spec);

		// Create an Intent to launch the ActivityGroup for the tab (to be reused)  
		intent = new Intent().setClass(this, MySpacesActivityGroup.class);
		spec = tabHost.newTabSpec("MySpacesActivityGroup").setIndicator("MySpaces", res.getDrawable(R.drawable.ic_tab_myspaces)).setContent(intent);		
		tabHost.addTab(spec);


//		intent = new Intent().setClass(this, DiscoverGroup.class);
//		spec = tabHost.newTabSpec("discover").setIndicator("Discover", res.getDrawable(R.drawable.ic_tab_discover)).setContent(intent);
//		tabHost.addTab(spec);
//
//
//		/* (BOF) DEBUG ACTIVITY */
//		intent = new Intent().setClass(this, DebugActivity.class);
//		spec = tabHost.newTabSpec("debug").setIndicator("Debug", res.getDrawable(R.drawable.ic_tab_debug)).setContent(intent);
//		tabHost.addTab(spec);
//		/* (EOF) DEBUG ACTIVITY */


		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			public void onTabChanged(String tabId)
			{
				setTabColor(tabHost);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), 0);

			}
		});
	}



	public void setTabColor(TabHost tabhost) {
		for(int i=0;i<tabhost.getTabWidget().getChildCount();i++)
		{
			//unselected
			tabhost.getTabWidget().getChildAt(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_unselected));
			//tabhost.getTabWidget().getChildAt(i).setPadding(2, 0, 2, 0);
		}
		// selected
		tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selected));
		//tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setPadding(0, 0, 0, 4);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.import_spaces:
			Intent i = new Intent(getApplicationContext(), ImportActivity.class);
			startActivity(i);
			return true;   
		case R.id.export_spaces:
			this.exportSpaces();
			return true;    
		case R.id.settings:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Settings");

			//	        final User userSettings;
			//try {
				
				final User userSettings = frontController.getUser();
				
				final String[] items = {"Send Broadcasts", "Recieve Broadcasts", "Use Sensors"};
				final boolean[] states = {userSettings.isSendBroadcasts(), userSettings.isRecieveBroadcasts(), userSettings.isUseSensors()};

				builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener(){
					public void onClick(DialogInterface dialogInterface, int item, boolean state) {

						switch (item) {
						case 0:
							userSettings.setSendBroadcasts(state);
							break;
						case 1:
							userSettings.setRecieveBroadcasts(state);
							break;
						case 2:
							userSettings.setUseSensors(state);
							break;
						}
						
						userSettings.saveSettings();
					}
				});
				builder.show();
//			} catch (AuthorizationException e) {
//				e.printStackTrace();
//			}

			return true;           

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void exportSpaces() {

		SpaceManager spaceManager = SpaceManager.getInstance(getApplicationContext());
		XmlSpaceListWriter xml = new XmlSpaceListWriter();
		String output = xml.writeXml(spaceManager.getAllSpaces());


		File myFile = null;
		try {
			myFile = this.getExternalCacheDir().createTempFile("SpaceDefinitions_", ".xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileWriter f;
		try {
			f = new FileWriter(myFile);

			f.write(output);
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Toast.makeText(getApplicationContext(), "Space definitions exported as: \"" +myFile.getName()+"\"", Toast.LENGTH_LONG).show();
	}


}
