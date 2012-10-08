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

package org.ubicollab.nomad.myspaces;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.ubicollab.nomad.R;
import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.auth.AuthorizationException;
import org.ubicollab.nomad.auth.FrontController;
import org.ubicollab.nomad.space.Entity;
import org.ubicollab.nomad.space.Rule;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.nomad.util.TabGroupActivity;
import org.ubicollab.nomad.util.XmlSpaceListWriter;
import org.ubicollab.nomad.widget.ButtonWidget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SpaceInfoActivity extends Activity {

	FrontController frontController;
	SpaceManager spaceManager;
	Space space;

	TextView nameView;
	TextView descriptionView;
	TextView informationView;
	TextView createdView;
	TextView modifiedView;
	ViewGroup entityList;
	ViewGroup rulesList;

	//private ImageButton worldImageButton;
	//private String locationText = "This space does not have a location!";


	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_space_info);
		Log.i("SpaceInfo","onCreate");
		// Create a new FrontController
		try {
			frontController = FrontController.getInstance(getApplicationContext());
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		nameView = (TextView) findViewById(R.id.space_info_name);
		descriptionView = (TextView) findViewById(R.id.space_info_description);
		informationView = (TextView) findViewById(R.id.space_info_information);
		createdView = (TextView) findViewById(R.id.space_info_created);
		modifiedView = (TextView) findViewById(R.id.space_info_modified);
		entityList = (ViewGroup) findViewById(R.id.space_info_entity_list);
		rulesList = (ViewGroup) findViewById(R.id.space_info_rules_list);

		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey(Space.SPACE_IDENTIFIER)) {
			space = (Space) bundle.getSerializable(Space.SPACE_IDENTIFIER);
		} else {
			// No space supplied, space will be null -> Crash();
			Log.i("SpaceInfo","No space supplied!");
		}

	}


	@Override
	protected void onStart() {
		super.onStart();

		try {
			spaceManager = frontController.getSpaceManager();
		} catch (AuthorizationException e) {
			// If not authorized, force the user to login.
			frontController.startLoginActivity();
		}

		if (spaceManager != null) {
			populate();
		}

		if(space.getId()!=null){
			//Toast toast = Toast.makeText(getApplicationContext(), space.getId(), Toast.LENGTH_LONG);
			//toast.show();
		}else if(space.getId()==null){
			//Toast toast = Toast.makeText(getApplicationContext(), "Id is empty", Toast.LENGTH_LONG);
			//toast.show();
		}
	}


	/*
	 * Set as current space button
	 */
	public void setAsCurrentSpace(View v) {
		try {
			if (ButtonWidget.WidgetUpdateService2 == true){
				
				Intent i = new Intent ();
				i.setAction("org.ubicollab.nomad");
				v.getContext().sendBroadcast(i);
				spaceManager.setCurrentSpace(space);
					Toast.makeText(SpaceInfoActivity.this, "Current space set to: \"" + space.getName() +"\".", Toast.LENGTH_SHORT).show();	
					
					
			}else{
						spaceManager.setCurrentSpace(space);
						Toast.makeText(SpaceInfoActivity.this, "Current space set to: \"" + space.getName() +"\".", Toast.LENGTH_SHORT).show();
						
			}
		} catch (Exception e) { // 0 or more
		}
		finally {                               // finally block
			spaceManager.setCurrentSpace(space);
			Toast.makeText(SpaceInfoActivity.this, "Current space set to: \"" + space.getName() +"\".", Toast.LENGTH_SHORT).show();
			
		}
	}

	/*
	 * Share button
	 */
	public void shareSpace(View v) {

		XmlSpaceListWriter xml = new XmlSpaceListWriter();
		ArrayList<Space> tmpSpaceAsList = new ArrayList<Space>();
		tmpSpaceAsList.add(space);
		String output = xml.writeXml(tmpSpaceAsList);

		//		boolean mExternalStorageAvailable = false;
		//		boolean mExternalStorageWriteable = false;
		//		
		//		String state = Environment.getExternalStorageState();
		//
		//		if (Environment.MEDIA_MOUNTED.equals(state)) {
		//			// We can read and write the media
		//			mExternalStorageAvailable = mExternalStorageWriteable = true;
		//		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		//			// We can only read the media
		//			mExternalStorageAvailable = true;
		//			mExternalStorageWriteable = false;
		//		} else {
		//			// Something else is wrong. It may be one of many other states, but all we need
		//			//  to know is we can neither read nor write
		//			mExternalStorageAvailable = mExternalStorageWriteable = false;
		//		}


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
		Log.i("asdf", ""+myFile.getAbsolutePath());


		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
		emailIntent.setType("text/plain");
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New cool space.");    
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey! I just discovered a new space that I think you would like!");

		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+myFile.getAbsolutePath()));
		
		startActivity(Intent.createChooser(emailIntent, "Send using:"));  
	}

	/*
	 * Modify button could in the future be remove to promote the use of hardware back button only.
	 */
	public void modifySpace(View v) {
		// TODO Refactor to enums.
		//Toast.makeText(this, "Modify..", Toast.LENGTH_SHORT).show();

		Intent i = new Intent(v.getContext(), ModifySpaceActivity.class);
		i.putExtra(Space.SPACE_IDENTIFIER, space);

		TabGroupActivity parentActivity = (TabGroupActivity) getParent();
		parentActivity.startChildActivity("ModifySpaceActivity", i);
	}


	/*
	 * Populates the Entity and Rules list with elements from currently selected space.
	 */
	private void populate() {

		nameView.setText(space.getName());
		descriptionView.setText(space.getDescription());
		informationView.setText(space.getInformation());
		createdView.setText("Created: " + space.getCreated());
		modifiedView.setText("Modified: " + space.getModified());
		TextView rulesText = (TextView) findViewById(R.id.space_info_rules_text);
		
		/*
		 * Populates the Entities list.
		 */
		if (space.getEntities() != null) {

			for (int current = 0; current < space.getEntities().size(); current++) {
				View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.space_info_list_item, null);

				Entity entity = space.getEntities().get(current);

				if (entity != null) {
					TextView itemName = (TextView) view.findViewById(R.id.space_info_list_item);
					if (itemName != null) {
						itemName.setText(entity.getDescription()); 
						itemName.setTextColor(Color.BLACK);
					}
				}    

				// Listener for each entity
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//Intent intent = new Intent(getApplicationContext(), CLASS_TO_START)
						//startActivity(intent);
						Toast.makeText(SpaceInfoActivity.this, "Entity info: " + ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
					}
				});

				entityList.addView(view);   
			}

			entityList.invalidate();
		}
		
		/*
		 * Populates rules
		 */
	if (space.getRules() != null) {
			
			rulesList.removeAllViews();

			for (int i = 0; i < space.getRules().size(); i++) {
				System.out.println("Rules:");
				System.out.println(space.getRules().get(i).toString());
				
				View ruleView = LayoutInflater.from(getBaseContext()).inflate(
						R.layout.space_info_list_item, null);

				Rule rule = space.getRules().get(i);

				if (rule != null && rule.getDescription() != "-1") {
					TextView itemName = (TextView) ruleView
							.findViewById(R.id.space_info_list_item);
					if (itemName != null) {
						itemName.setText(rule.getDescription());
						itemName.setTextColor(Color.BLACK);
					}
					rulesList.addView(ruleView);
				}
			}
			rulesList.invalidate();
			rulesList.postInvalidate();
		} else {
			rulesText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast toast = Toast.makeText(getApplicationContext(),
							"Rules can be set using UbiRule application",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			});
	}

	}


	/* Class My Location Listener */
	/*
	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			locationText = loc.getLatitude() + ", " +loc.getLongitude();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	 */

}

