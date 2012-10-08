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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.ubicollab.nomad.R;
import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.auth.AuthorizationException;
import org.ubicollab.nomad.auth.FrontController;
import org.ubicollab.nomad.rss.RssReader;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.nomad.util.TabGroupActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreen extends Activity implements Runnable{

	private CameraView camera;
	private FrontController frontController;
	private SpaceAdapter spaceAdapter;
	private SpaceManager spaceManager;
	private ListView resultList, rssList;
	private TextView description, currentspaceDesc;
	private ImageButton space_image;
	private PopupWindow pw;
	private TextView more_text;
	private RssListAdapter adapter;
	private ArrayList<Space> spaceList;
	private ImageView current_picture_popup;
	private LinearLayout rss_layout;
	private List<JSONObject> jobs;
	private TextView latest_rss;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		try {
			frontController = FrontController.getInstance(getApplicationContext());
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Initializations
		 */
		resultList = (ListView) findViewById(R.id.home_spaces_list);
		description = (TextView) findViewById(R.id.currentplace);
		space_image = (ImageButton) findViewById(R.id.currentspace_image);
		currentspaceDesc = (TextView) findViewById(R.id.currentspace_desc);
		rss_layout = (LinearLayout) findViewById(R.id.home_rss_layout);
		latest_rss = (TextView) findViewById(R.id.latest_rss);
	}

	private void editPic(final int fileName) {
		// get the instance of the LayoutInflater
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate our view from the corresponding XML file
		View layout = inflater.inflate(R.layout.popup,
				(ViewGroup) findViewById(R.id.popup_menu_root), false);

		Button button_ok = (Button) layout.findViewById(R.id.popup_menu_ok);
		Button button_cancel = (Button) layout
				.findViewById(R.id.popup_menu_cancel);
		current_picture_popup = (ImageView) findViewById(R.id.popup_currentspace_image);

		// create a 100px width and 200px height popup window
		Display display = getWindowManager().getDefaultDisplay();

		pw = new PopupWindow(layout, display.getWidth() / 4
				+ display.getWidth() / 2, display.getWidth() / 4
				+ display.getHeight() / 2, true);
		// set actions to buttons we have in our popup

		FileInputStream in = null;
		BufferedInputStream buf = null;
		try {
			in = new FileInputStream("/sdcard/" + fileName + ".jpg");
			buf = new BufferedInputStream(in);
			Bitmap bMap = BitmapFactory.decodeStream(buf);
			current_picture_popup.setImageBitmap(bMap);
			if (in != null) {
				in.close();
			}
			if (buf != null) {
				buf.close();
			}
		} catch (Exception e) {
			Log.e("Error reading file", e.toString());
		}

		button_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View vv) {
				takePicture(fileName);
			}
		});
		button_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View vv) {
				pw.dismiss();
			}
		});
		// finally show the popup in the center of the window
		pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// try {
		// spaceManager = frontController.getSpaceManager();
		// } catch (AuthorizationException e) {
		// // If not authorized, force the user to login.
		// frontController.startLoginActivity();
		// }

		try {
			spaceManager = frontController.getSpaceManager();
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setupAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();

		// // Check login and update
		// if (frontController.isAuthorized() && spaceAdapter != null) {
		// spaceAdapter.notifyDataSetChanged();
		// } else {
		// frontController.startLoginActivity();
		// }

		setupLightAdapter();
	}

	private void setupLightAdapter() {
		if (spaceManager != null) {
			spaceList = spaceManager.getLog();

			if (spaceList.isEmpty()) {
				clearScreen();
			} else {
				openScreen();

				if (spaceList.size() > 3) {
					ArrayList<Space> list = new ArrayList(spaceList.subList(0,
							3));
					spaceAdapter = new SpaceAdapter(this,
							R.layout.home_itemview, list);
					addFooter();
				} else {
					spaceAdapter = new SpaceAdapter(this,
							R.layout.home_itemview, spaceList);
				}
			}
			// Connect the Adapter to the default View.
			resultList.setAdapter(spaceAdapter);
			resultList.setOnItemClickListener(spaceClickedHandler);
		}
	}

	private void setupAdapter() {
		removeFooter();
		if (spaceManager != null) {
			spaceList = spaceManager.getLog();
			if (spaceList.isEmpty()) {
				clearScreen();
			} else {
				openScreen();
				if (spaceList.size() > 3) {
					spaceAdapter = new SpaceAdapter(this,
							R.layout.home_itemview, spaceList.subList(0, 3));
					addFooter();
				} else {
					spaceAdapter = new SpaceAdapter(this,
							R.layout.home_itemview, spaceList);
				}
			}
			// Connect the Adapter to the default View.
			resultList.setAdapter(spaceAdapter);
			resultList.setOnItemClickListener(spaceClickedHandler);
		}
	}

	public void onPause() {
		super.onPause();
		// removeFooter();
	}

	private void clearScreen() {
		setContentView(R.layout.welcome);
	}

	private void openScreen() {
		setContentView(R.layout.home);

		int pos = spaceList.size() - 1;

		resultList = (ListView) findViewById(R.id.home_spaces_list);
		description = (TextView) findViewById(R.id.currentplace);
		space_image = (ImageButton) findViewById(R.id.currentspace_image);
		currentspaceDesc = (TextView) findViewById(R.id.currentspace_desc);
		rssList = (ListView) findViewById(R.id.updates_list);
		
		rssList.setSelector(android.R.color.transparent);
		rssList.setFocusable(false);
		rssList.setClickable(false);
		
		space_image.setVisibility(View.VISIBLE);

		final int fileName = Integer.parseInt(spaceList.get(pos).getId());

		FileInputStream in;
		BufferedInputStream buf;
		try {
			in = new FileInputStream("/sdcard/" + fileName + ".jpg");
			buf = new BufferedInputStream(in);
			Bitmap bMap = BitmapFactory.decodeStream(buf);
			space_image.setImageBitmap(bMap);
			if (in != null) {
				in.close();
			}
			if (buf != null) {
				buf.close();
			}
		} catch (Exception e) {
			Log.e("Error reading file", e.toString());
		}

		//description.setText("Space Description:");
		spaceList = spaceManager.getLog();
		currentspaceDesc.setText(spaceList.get(pos).getDescription());

		Thread thread = new Thread(this);
		thread.start();

		space_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"edit pic", Toast.LENGTH_SHORT);
				toast.show();

				editPic(fileName);
			}
		});
		
		
		
		
//		space_image.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Toast toast = Toast.makeText(getApplicationContext(),
//						"edit pic", Toast.LENGTH_SHORT);
//				toast.show();
//
//				editPic(fileName);
//			}
//		});
	}
	
	public void run() {
		loadRSS();
		handler.sendEmptyMessage(0);
		}

	private void loadRSS() {
		jobs = new ArrayList<JSONObject>();

		try {
			jobs = RssReader.getLatestRssFeed();
			if(jobs.size()==0){
				//add warning as a JSONObject
				//List<JSONObject> warning = new ArrayList<JSONObject>();
			}
		} catch (Exception e) {
			Log.e("RSS ERROR",
					"Error loading RSS Feed Stream >> " + e.getMessage()
							+ " //" + e.toString());
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			Button rss_link = (Button) findViewById(R.id.home_get_rss);
			
			if (jobs.size()>5) {
				

				rss_link.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast toast = Toast.makeText(getApplicationContext(),
								"Go to: " + RssReader.feed.toString(),
								Toast.LENGTH_SHORT);
						toast.show();
					}
				});

				adapter = new RssListAdapter(getParent(), jobs.subList(0, 5));

				rssList.setAdapter(adapter);
			}else{
				adapter = new RssListAdapter(getParent(), jobs);

				rssList.setAdapter(adapter);
			}
			if(jobs.size() == 0){
				/// TODO Change the layout
			}
			}
		};

	private boolean removeFooter() {
		return resultList.removeFooterView(more_text);
	}

	private void addFooter() {
		synchronized (resultList) {
			more_text = new TextView(this);
			more_text.setText("History");
			more_text.setGravity(0x11);
			more_text.setTextSize(17);
			more_text.setHeight(40);
			resultList.addFooterView(more_text);
			more_text.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(v.getContext(), Home_History.class);

					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("Home_history", i);
					
					//startActivity(new Intent(v.getContext(), Home_History.class));
				}
			});
		}
	}

	private void takePicture(int fileName) {
		Toast toast = Toast.makeText(this, String.valueOf(fileName),
				Toast.LENGTH_LONG);
		toast.show();

		Intent intent = new Intent();
		Bundle filename = new Bundle();

		filename.putInt("file name", fileName); // add space id

		intent.setClass(this, CameraView.class);
		intent.putExtras(filename);
		startActivity(intent);

		// Intent i = new Intent(getApplicationContext(), CameraView.class);
		// i.putExtra("filename", fileName);
		// // TabGroupActivity parentActivity = (TabGroupActivity) getParent();
		// // parentActivity.startChildActivity("CreateSpaceActivity", i);
		// startActivity(i);
	}

	/*
	 * Starts a new activity based on the space that was pressed.
	 */
	private OnItemClickListener spaceClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {

			if (pos != parent.getBottom()) {
				Space selected = (Space) parent.getItemAtPosition(pos);

				spaceManager.setCurrentSpace(selected);
				spaceManager.insertStatistics(selected);
			}

			if (spaceManager != null) {
				spaceList = spaceManager.getLog();

				space_image.setEnabled(true);
				description.setText("Space Description:");

				currentspaceDesc.setText(spaceList.get(pos).getName()
						.toString());

				// Create a new (default) Adapter and fill it with a list of
				// spaces.
				spaceAdapter = new SpaceAdapter(getApplicationContext(),
						R.layout.home_itemview, spaceList);
				// Connect the Adapter to the default View.
				resultList.setAdapter(spaceAdapter);
				// Now hook into our object and set its onItemClickListener
				// member
				// to our class handler object.
				resultList.setOnItemClickListener(spaceClickedHandler);

				spaceAdapter.notifyDataSetChanged();

				setupAdapter();
			}
		}
	};

	/*
	 * A ListAdapter that manages a ListView backed by an array of space
	 * objects. This class expects that the provided resource id references a
	 * single TextView. The TextView is used to generate list items for the list
	 * that this adapter is connected to.
	 */
	private class SpaceAdapter extends ArrayAdapter<Space> {

		private List<Space> items;

		public SpaceAdapter(Context context, int textViewResourceId,
				List<Space> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.home_itemview, null);
			}
			Space o = items.get(position);
			if (o != null) {
				TextView itemName = (TextView) v
						.findViewById(R.id.home_spaces_list_item_name);

				if (itemName != null) {
					itemName.setText(o.getName());
				}

			}
			return v;
		}
	}

}
