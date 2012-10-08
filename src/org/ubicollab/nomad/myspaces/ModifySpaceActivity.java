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
import org.ubicollab.nomad.util.TabGroupActivity;
import org.ubicollab.nomad.util.TemporaryStorage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


/*
 * @author Samuel Wejeus
 */
public class ModifySpaceActivity extends SpaceModification {

	Space oldSpace;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_space);
		

		// Retrive the supplied space and copy content to temporary storage.
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey(Space.SPACE_IDENTIFIER)) {
			oldSpace = (Space) bundle.getSerializable(Space.SPACE_IDENTIFIER);	
		} else {
			// No space supplied, space will be null -> Crash(); (no, end activity)
			finish();
		}

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
		Log.d(this.getLocalClassName(), "(onCreate) TemporaryStorage identifier: " + this);
		if (TemporaryStorage.containsStorage(this)) {
			tmpSpace = (Space) TemporaryStorage.getStorage(this);
			Log.d(this.getLocalClassName(), "(onCreate) reusing old temporaryStorage. Content: " + tmpSpace);
		} else {
			Log.d(this.getLocalClassName(), "(onCreate) creating new temporaryStorage.");
			tmpSpace = (Space) TemporaryStorage.createStorage(this, new Space(oldSpace));
		}
		
		
		
		
	}

	
	/* Stores everything that is currently on screen */
	public void saveClick(View v) {

		oldSpace.setId(tmpSpace.getId());
		oldSpace.setName(nameView.getText().toString());
		oldSpace.setDescription(descriptionView.getText().toString());
		oldSpace.setInformation(informationView.getText().toString());
		oldSpace.setEntities(tmpSpace.getEntities());
		
		spaceManager.updateSpace(oldSpace);

		Toast.makeText(context, "Space: \"" + oldSpace.getName() +"\" updated.", Toast.LENGTH_SHORT).show();
		TemporaryStorage.clearStorage(this);

		finish();
	}


	/*
	 * Delete confirmation. Ask the user if they really want to remove the space
	 */
	public void confirmDeleteSpace(View v) {

		new AlertDialog.Builder(context)
		.setTitle("Delete")
		.setMessage("Do you REEEALLY want to delete this space?")
		.setPositiveButton("Hell yes!", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				spaceManager.deleteSpace(oldSpace);

				Intent i = getIntent();
				i.putExtra(TabGroupActivity.GOT_RETURN_VALUE, true);
				i.putExtra(TabGroupActivity.FINISH_CALLER, true);
				
				Toast.makeText(context, "Deleted space: " + oldSpace.getName(), Toast.LENGTH_SHORT).show();

				finish();
			}

		})
		.setNegativeButton("Naw..", null)
		.show();

	}


}
