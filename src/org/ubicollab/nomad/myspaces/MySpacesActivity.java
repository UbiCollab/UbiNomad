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


import java.util.ArrayList;
import java.util.List;

import org.ubicollab.nomad.R;
import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.nomad.util.TabGroupActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/*
 * My Spaces Activity
 * 
 * @author: Samuel Wej�us (In heavy development FFS! =)
 * 
 * Possible changes: repopulate the list from database everytime the user use it
 * or keep the list in internal structure and only update on changes? 
 * 
 * First draft is update everytime..
 * possible choices: 
 * 		many different adapters (one for each category (favorites, rec..)
 * 		different Lists repopulate adapter on change
 * 		one adapter, one list, each item has a category? (maybe needs architectual changes..)
 * 
 */
public class MySpacesActivity extends Activity {

	private SpaceAdapter spaceAdapter;
	private SpaceManager spaceManager;
	private ListView resultList;
	RelativeLayout layoutTop;
	LinearLayout layoutContent;
	
	protected Context context;

	private ArrayList<Space> spaceList; //hide?
	final String[] CATEGORIES = new String[] { "ALL", "FAVORITES", "RECENT", "NEARBY", "STARRED" };


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_spaces);
		Log.i("MySpaces","onCreate");
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

		spaceManager = SpaceManager.getInstance(getApplicationContext()); 
		layoutTop = (RelativeLayout) findViewById(R.id.my_spaces_layout_top);
		layoutContent = (LinearLayout) findViewById(R.id.my_spaces_layout_content);
		
		/* Create a new spinner, attach adapter, set layout and connect to listeners 
		 * The reason this is done in code is that when defined in XML spinner uses current
		 * activity context for drawing. Due to use of TabHost and activity groups we have
		 * to make all drawing in the parent */ 
//		Spinner spinner = new Spinner(context);
//		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, CATEGORIES);
//		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinner.setAdapter(spinnerArrayAdapter);
//		spinner.setPrompt("Select Category");
//		RelativeLayout.LayoutParams spinnerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		spinnerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//		spinnerParams.addRule(RelativeLayout.LEFT_OF, R.id.my_spaces_add_button);
//		// Causes a update to layout
//		spinner.setLayoutParams(spinnerParams);
//
//		spinner.setOnItemSelectedListener(new categorySelectedHandler());



		/* Initialization of the ListView that will hold the list of all the users spaces. */		
		resultList = (ListView) findViewById(R.id.my_spaces_space_list);
		// Now hook into our object and set its onItemClickListener member to our class handler object.
		resultList.setOnItemClickListener(new spaceClickedHandler());
		resultList.setOnItemLongClickListener(new spaceLongClickedHandler());

		/* Add programmatically created views to layout */
		//layoutTop.addView(spinner);		
	}	


	@Override
	public void onResume() {
		super.onResume();
		populate();
	}




	private void populate() {
		if (spaceManager != null) {
			// Get depending on current view
			spaceList = spaceManager.getAllSpaces();
			if (spaceList.size() != 0) { 
				layoutContent.removeAllViews();
				//resultList.setVisibility(ListView.VISIBLE);
				// Create a new Adapter and fill it with a list of spaces.
				spaceAdapter = new SpaceAdapter(this, R.layout.utility_my_spaces_list_item, spaceList);
				// Connect the Adapter to the default View. 
				resultList.setAdapter(spaceAdapter);
				layoutContent.addView(resultList);
			} else {
				layoutContent.removeAllViews();
				//resultList.setVisibility(ListView.GONE);
				TextView infoText = new TextView(context);
				infoText.setText("No spaces for current selection, add some more!");
				infoText.setGravity(Gravity.CENTER);
				infoText.setTextSize(15);
				layoutContent.addView(infoText);
			}

		} else {
			Log.e("MySpaces", "SpaceManager is null!");
			finish();
		}
	}


	public void addSpace(View v) {
		Intent i = new Intent(v.getContext(), CreateSpaceActivity.class);

		TabGroupActivity parentActivity = (TabGroupActivity) getParent();
		parentActivity.startChildActivity("CreateSpaceActivity", i);
	}

	/* Handler for spinner selections */
	private class categorySelectedHandler implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			//Toast.makeText(context,"You choosed: " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
			//Toast.makeText(context,"You selected nothing.", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Starts a new activity based on the space that was pressed.
	 */
	private class spaceClickedHandler implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View v, int pos, long id)
		{
			Space selectedSpace = (Space) parent.getAdapter().getItem(pos);

			Intent i = new Intent(v.getContext(), SpaceInfoActivity.class);
			i.putExtra(Space.SPACE_IDENTIFIER, selectedSpace);

			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("SpaceInfoActivity", i);

		}
	}

	private class spaceLongClickedHandler implements OnItemLongClickListener {


		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
			//Toast.makeText(context,"You choosed: " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();

			final CharSequence[] CHOICES = {"Set as current space"};
			final Space selectedSpace = (Space) parent.getAdapter().getItem(pos);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(parent.getItemAtPosition(pos).toString());
			
			builder.setItems(CHOICES, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) { // Set as current
						spaceManager.setCurrentSpace(selectedSpace);
						
						//set space on Widget
						Intent i = new Intent ();
				    	i.setAction("org.ubicollab.nomad");
				    	getApplicationContext().sendBroadcast(i);
				    	//
				    	
						Toast.makeText(context, "Current space set to: " + selectedSpace.getName(),Toast.LENGTH_SHORT).show();
					} 
//					else if (item == 1) { // Delete
//						spaceManager.deleteSpace(selectedSpace);
//						Toast.makeText(context, "Deleted space: " + selectedSpace.getName(),Toast.LENGTH_SHORT).show();
//						populate();
//					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();

			return true;
		}
	}

	/*
	 * A ListAdapter that manages a ListView backed by an array of space objects. 
	 * This class expects that the provided resource id references a single TextView. 
	 * The TextView is used to generate list items for the list that this adapter is connected to.
	 */
	private class SpaceAdapter extends ArrayAdapter<Space> {

		private List<Space> items;

		public SpaceAdapter(Context context, int textViewResourceId, List<Space> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		/*
		 * TODO re-write using: http://developer.android.com/videos/index.html#v=wDBM6wVEO70
		 * (fast views)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.utility_my_spaces_list_item, null);
			}
			Space o = items.get(position);
			if (o != null) {
				TextView itemName = (TextView) v.findViewById(R.id.my_spaces_list_item_name);
				TextView itemDesc = (TextView) v.findViewById(R.id.my_spaces_list_item_desc);
				if (itemName != null) {
					itemName.setText(o.getName()); }
				if(itemDesc != null){
					itemDesc.setText(o.getDescription());
				}
			}
			return v;
		}
	}

}



