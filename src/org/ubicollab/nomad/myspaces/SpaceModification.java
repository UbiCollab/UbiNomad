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

import java.util.Random;

import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.auth.FrontController;
import org.ubicollab.nomad.space.Entity;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.nomad.util.TabGroupActivity;
import org.ubicollab.nomad.util.TemporaryStorage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SpaceModification extends Activity {

	protected FrontController frontController;
	protected SpaceManager spaceManager;
	protected Context context;

	protected EditText nameView;
	protected EditText descriptionView;
	protected EditText informationView;
	protected LinearLayout entitiesView;

	protected Space tmpSpace;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* 
		 * If the ActivityGroup is within a TabActivity that means we have nested 
		 * activities with more then two levels. Android doesn't support this at 
		 * the moment but this is a workaround. 
		 * The solution is to use the parent activity's context instead. 
		 */
		if (getParent() != null) {
			context = getParent();
		} else {
			context = this;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();

		// TODO values could be null! 
		Bundle bundle = getIntent().getExtras();
		if ((bundle != null) && bundle.containsKey(TabGroupActivity.DATA)) {			
			Entity entity = (Entity) bundle.getSerializable(TabGroupActivity.DATA);
			tmpSpace.getEntities().add(entity);
			Log.d(this.getLocalClassName(), "(onResume) Adding entities: " + tmpSpace.getEntities());
			getIntent().removeExtra(TabGroupActivity.DATA);
		}

		this.populate();
	}


	@Override
	protected void onPause() { 
		super.onPause();
		Log.d(this.getLocalClassName(), "(onPause)");
		if (this.isFinishing()) {
			Log.d(this.getLocalClassName(), "(onPause) isFinishing()");
			TemporaryStorage.clearStorage(this);
		} else {
			this.updateTemporaryStorage();	
		}
	}


	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		this.updateTemporaryStorage();
	}


	private void updateTemporaryStorage() {
		tmpSpace.setName(nameView.getText().toString());
		tmpSpace.setDescription(descriptionView.getText().toString());
		tmpSpace.setInformation(informationView.getText().toString());
		
		Log.d(this.getLocalClassName(), "(updateTemporaryStorage) content is now: " + tmpSpace);
		TemporaryStorage.commit(this);
	}

	private void populate() {

		// Refill views with data from temporary storage
		nameView.setText(tmpSpace.getName());
		descriptionView.setText(tmpSpace.getDescription());
		informationView.setText(tmpSpace.getInformation());

		entitiesView.removeAllViews();

		/*
		 * Populates the Entity list.
		 */
		if (tmpSpace.getEntities().size() != 0) {
			for (int i = 0; i < tmpSpace.getEntities().size(); i++) {
				// Create a new container for this entity
				RelativeLayout container = new RelativeLayout(context);
				
				// Add text
				TextView newChildText = new TextView(context);
				newChildText.setText(tmpSpace.getEntities().get(i).getDescription());
				container.addView(newChildText);

				// Add remove button
				Button newChildButton = new Button(context);
				newChildButton.setId(i);
				newChildButton.setText("-");
				newChildButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						tmpSpace.getEntities().remove(v.getId());
						// Re:populate the view
						populate();
					}
				});
				
				container.addView(newChildButton);
				
				// Set the layout of text
				RelativeLayout.LayoutParams paramsText = (RelativeLayout.LayoutParams) newChildText.getLayoutParams();
				paramsText.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				paramsText.addRule(RelativeLayout.CENTER_VERTICAL);
				// Set the layout of button
				RelativeLayout.LayoutParams paramsButton = (RelativeLayout.LayoutParams) newChildButton.getLayoutParams();
				paramsButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				// Causes a update to layout
				newChildText.setLayoutParams(paramsText);
				newChildButton.setLayoutParams(paramsButton);
				

				entitiesView.addView(container);
			}
		} else {
			TextView newChild = new TextView(context);
			newChild.setText("(no entities set)");
			entitiesView.addView(newChild);
		}

		entitiesView.invalidate();
	}


	public void addExtra(View v) {
				
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Choose type");
		builder.setItems(Space.DATA, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(context, Space.DATA[item], Toast.LENGTH_SHORT).show();

				Intent i = new Intent(context, CreateEntityActivity.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("CreateEntityActivity", i);

			}
		});
		AlertDialog alert = builder.create();

		alert.show();
	}

	public void cancelClick(View v) {
		TemporaryStorage.clearStorage(this);
		finish();
	}



	/* TEMPORARY DEBUGGING METHOD */
	public void generateRandomEntities(View v) {
		for (int i = 0; i < 3; i++) {
			Random r = new Random();
			String token = Long.toString(Math.abs(r.nextLong()), 36);
			Entity tmpEntity = new Entity(null, token, "type", "data");
			tmpSpace.getEntities().add(tmpEntity);
		}

		this.populate();
	}

}
