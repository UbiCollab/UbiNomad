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

import org.ubicollab.nomad.R;
import org.ubicollab.nomad.auth.AuthorizationException;
import org.ubicollab.nomad.auth.FrontController;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.nomad.util.TemporaryStorage;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/*
 * @author Samuel Wejeus
 */
public class CreateSpaceActivity extends SpaceModification {


	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_space);


		// Create a new FrontController
		try {
			frontController = FrontController.getInstance(context);
		} catch (AuthorizationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			spaceManager = frontController.getSpaceManager();
		} catch (AuthorizationException e) {
			// Give option to login?
			frontController.startLoginActivity();
		}

		if (spaceManager == null) {
			// Do stuff? Warn user?
		}

		// Initiate views.
		nameView = (EditText) findViewById(R.id.modify_space_name);
		descriptionView = (EditText) findViewById(R.id.modify_space_description);
		informationView = (EditText) findViewById(R.id.modify_space_information);
		entitiesView = (LinearLayout) findViewById(R.id.modify_space_entity_list);

		// Setup temporary storage
		if (TemporaryStorage.containsStorage(this)) {
			tmpSpace = (Space) TemporaryStorage.getStorage(this);
		} else {
			tmpSpace = (Space) TemporaryStorage.createStorage(this, new Space());
		}

	}


	public void saveClick(View v) {
		tmpSpace.setName(nameView.getText().toString());
		tmpSpace.setDescription(descriptionView.getText().toString());
		tmpSpace.setInformation(informationView.getText().toString());		
		
		
		spaceManager.createNewSpace(tmpSpace);

		TemporaryStorage.clearStorage(this);
		Toast.makeText(context, "New space: \"" + tmpSpace.getName() +"\" created.", Toast.LENGTH_SHORT).show();
		
		finish();
	}



}
