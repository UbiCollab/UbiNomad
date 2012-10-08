package org.ubicollab.nomad.deprecated;


import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.discover.DiscoverGroup;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.ulm.R;
import org.ubicollab.ulm.R.id;
import org.ubicollab.ulm.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
//import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class CreateSpace extends Activity {

	EditText spaceNameInput;
	EditText descriptionInput;

	TextView parentSpaceText = (TextView)findViewById(R.id.parent_space_textview);

	//
	//InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);



	public void onCreate(Bundle savedInstanceState)throws NullPointerException
 {	    

		final SpaceManager sm = SpaceManager.getInstance(getApplicationContext());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_space);


		if(SetParentSpaceList.parentSpace == null){
			parentSpaceText.setText((CharSequence)"Parent space not set");
		}
		else{
			parentSpaceText.setText((CharSequence)"Parent space set as:" + SetParentSpaceList.parentSpace.getName());
		}
 


		final Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DiscoverGroup.group.back();
			}


		});

		final EditText spaceNameInput = (EditText) findViewById(R.id.space_name_input);
		spaceNameInput.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					System.out.println();
					return true;
				}
				return false;
			}
		});
		//Skal hindre keyboard i � �pnes n�r en trykker ned p� edittexten
		//mgr.hideSoftInputFromWindow(spaceNameInput.getWindowToken(), 0);



		final EditText descriptionInput = (EditText) findViewById(R.id.description_input);
		descriptionInput.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					System.out.println();
					return true;
				}
				return false;
			}
		});
		//Skal hindre keyboard i � �pnes n�r en trykker ned p� edittexten
		//mgr.hideSoftInputFromWindow(descriptionInput.getWindowToken(), 0);

		final Button saveSpaceButton = (Button) findViewById(R.id.save_space_button);
		saveSpaceButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//String id ,String name, Location location, String description, Space parent, String dateCreated
				sm.createNewSpace(new Space(spaceNameInput.toString(), null, descriptionInput.toString(), SetParentSpaceList.parentSpace));	      
				Toast.makeText(CreateSpace.this, "Space '" + spaceNameInput.getText() + "' saved successfully", Toast.LENGTH_SHORT).show();
				DiscoverGroup.group.back();
			}

		});

		final Button setParentSpaceButton = (Button) findViewById(R.id.set_parent_space_button);
		setParentSpaceButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), SetParentSpaceList.class);
				View view = DiscoverGroup.group.getLocalActivityManager()  
				.startActivity(".SetParentSpaceList", myIntent 
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))  
						.getDecorView(); 
				DiscoverGroup.group.replaceView(view);

			}

		});


	}

}
