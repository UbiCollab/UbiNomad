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

package org.ubicollab.nomad.widget;

import java.util.ArrayList;
import java.util.List;

import org.ubicollab.nomad.Nomad;
import org.ubicollab.nomad.R.drawable;
import org.ubicollab.nomad.R.id;
import org.ubicollab.nomad.R.layout;
import org.ubicollab.nomad.SpaceManager;

import org.ubicollab.nomad.R;
import org.ubicollab.nomad.discover.DiscoverActivity;
import org.ubicollab.nomad.discover.DiscoverGroup;
import org.ubicollab.nomad.home.HomeActivity;
import org.ubicollab.nomad.home.HomeScreen;
import org.ubicollab.nomad.myspaces.CreateSpaceActivity;
import org.ubicollab.nomad.myspaces.MySpacesActivity;
import org.ubicollab.nomad.myspaces.SpaceInfoActivity;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.nomad.util.TabGroupActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/*
 * My Spaces Activity
 */
public class WidgetListSpacesActivity extends Activity {

	private SpaceAdapter spaceAdapter;
	private SpaceManager spaceManager;
	private ListView resultList;
	
	private ArrayList<Space> spaceList; //hide?
	final String[] CATEGORIES = new String[] { "FAVORITES", "RECENT", "NEARBY", "ALL", "STARRED" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//my_spaces
		setContentView(R.layout.widget_spaces);

		spaceManager = SpaceManager.getInstance(getApplicationContext());
		spaceList = spaceManager.getAllSpaces();
		
		OnItemClickListener spaceClickedHandler = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos, long id)
			{
				//sets space on click
				Space selectedSpace = (Space) parent.getAdapter().getItem(pos);
				spaceManager.setCurrentSpace(selectedSpace);
				
				Intent i = new Intent ();
		    	i.setAction("org.ubicollab.nomad");
		    	v.getContext().sendBroadcast(i);
				
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
			}
		};
		
		//go to home page
		
		  final Button homeButton = (Button) findViewById(R.id.my_spaces_home);
		    homeButton.setOnClickListener(new View.OnClickListener() {
		       //onClick go to UbiNomad home page
		    	public void onClick(View v) {
		    		  //Intent intent = new Intent(WidgetListSpacesActivity.this, org.ubicollab.nomad.Nomad.class);
		    		  Intent intent = new Intent(WidgetListSpacesActivity.this, Nomad.class);		  
		    		  startActivity(intent);
		    		  
		        }
		    });
		
		/*
		 * Initialization of the ListView that will hold the list of all the users spaces.
		 */		
		resultList = (ListView) findViewById(R.id.my_spaces_space_list);
		spaceAdapter = new SpaceAdapter(this, R.layout.widget_spaces_list_item, spaceList);
		resultList.setAdapter(spaceAdapter);
		resultList.setOnItemClickListener(spaceClickedHandler); 
	
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.widget_spaces_list_item, null);
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
