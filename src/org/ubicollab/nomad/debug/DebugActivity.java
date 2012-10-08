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


package org.ubicollab.nomad.debug;

import java.util.Random;

import org.ubicollab.nomad.R;
import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.auth.AuthorizationException;
import org.ubicollab.nomad.auth.FrontController;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.nomad.util.DatabaseExceptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class DebugActivity extends Activity {
	private SpaceManager sm;
	private FrontController frontController;

	@Override
	public void onCreate(Bundle savedInstanceState)throws NullPointerException {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);

		Spinner spinner = (Spinner) findViewById(R.id.debug_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.planets_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);


		try {
			frontController = FrontController.getInstance(getApplicationContext());
		} catch (AuthorizationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		try {
			sm = frontController.getSpaceManager();
		} catch (AuthorizationException e) {
			// If not authorized, force the user to login.
			frontController.startLoginActivity();
		}

		if (sm != null) {
			if (sm.getCurrentSpace() != null) {
				Toast toast = Toast.makeText(getApplicationContext(), sm
						.getCurrentSpace().getName(), Toast.LENGTH_LONG);
				toast.show();
			} else {
				Toast toast2 = Toast.makeText(getApplicationContext(),
						"Its empty!", Toast.LENGTH_LONG);
				toast2.show();
			}
		}
		else if(sm == null){
			Toast toast3 = Toast.makeText(getApplicationContext(),
					"Space manager is empty", Toast.LENGTH_LONG);
			toast3.show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (sm != null) {
			if (sm.getCurrentSpace() != null) {
				Toast toast = Toast.makeText(getApplicationContext(), sm
						.getCurrentSpace().getName(), Toast.LENGTH_LONG);
				toast.show();
			} else {
				Toast toast2 = Toast.makeText(getApplicationContext(),
						"Its empty!", Toast.LENGTH_LONG);
				toast2.show();
			}
		}
		else if(sm == null){
			Toast toast3 = Toast.makeText(getApplicationContext(),
					"Space manager is empty", Toast.LENGTH_LONG);
			toast3.show();
		}
	}

	public void setloginfalse(View v) {
		frontController.clearCredentials();

		//Toast.makeText(this, "User have been logged out", Toast.LENGTH_LONG)
		//		.show();
	}

	public void generateSpaces(View v) {

		for (int i = 0; i < 3; i++) {
			Random r = new Random();
			String token = Long.toString(Math.abs(r.nextLong()), 36);

			Space s = new Space(null, null, null, token, token, "string", null,
					false, null, null);

			SpaceManager.getInstance(getApplicationContext()).createNewSpace(s);

		}

	}

	public void confirmDialog(View v) {

		//Ask the user if they really want to remove the space
		new AlertDialog.Builder(this)
		// .setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("Delete")
		.setMessage("Do you REEEALLY want to delete?")
		.setPositiveButton("Hell yes!",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog,
					int which) {

				///Toast.makeText(getApplicationContext(),
				//	"Deleted space: ", Toast.LENGTH_SHORT)
				//.show();

				finish();
			}

		}).setNegativeButton("Naw, changed me mind", null)
		.show();

	}

	public void dialogList(View v) {


		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a color");
		builder.setItems(Space.DATA, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), Space.DATA[item],
				//	Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = builder.create();

		alert.show();
	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			//Toast.makeText(
			//	parent.getContext(),
			//"The planet is " + parent.getItemAtPosition(pos).toString(),
			//Toast.LENGTH_LONG).show();
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}
}
