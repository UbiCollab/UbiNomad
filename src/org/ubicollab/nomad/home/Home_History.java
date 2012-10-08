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

import org.ubicollab.nomad.R;
import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.nomad.util.MainDB;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Home_History extends Activity {
	private SpaceAdapter spaceAdapter;
	private SpaceManager spaceManager;
	private MainDB db;
	private ListView resultList;

	private ArrayList<Space> spaceList;
	private ArrayList<Space> spaceList_date_used;
	private int color_int = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);

		spaceManager = SpaceManager.getInstance(getApplicationContext());

		spaceList = spaceManager.getLog();
		spaceList_date_used = spaceManager.getLogDateUsed();
	
		db = new MainDB(this);

		/*
		 * Initialization of the ListView that will hold the list of all the
		 * users spaces.
		 */

		resultList = (ListView) findViewById(R.id.spaces_list);
		resultList.setSelector(android.R.color.transparent);
		resultList.setFocusable(false);
		resultList.setClickable(false);


		if (spaceList.size() > 10) {
			spaceAdapter = new SpaceAdapter(this,
					R.layout.utility_my_spaces_list_item, spaceList.subList(0,
							10));
			// Connect the Adapter to the default View.
			resultList.setAdapter(spaceAdapter);
		} else {
			spaceAdapter = new SpaceAdapter(this,
					R.layout.utility_my_spaces_list_item, spaceList);
			// Connect the Adapter to the default View.
			resultList.setAdapter(spaceAdapter);
		}
		
		ImageView image1 = (ImageView) findViewById(R.id.most_used1);
		
		String spaceID_max_used = null;
		int temp = 0;
		
		ArrayList<Space> spaces = (ArrayList<Space>) db.getMostUsedStatistics();
		for(int i = 0; i<spaces.size(); i++){
			System.out.println(spaces.get(i).getName());
			if(Integer.parseInt(spaces.get(i).getName()) > temp){
				temp = Integer.parseInt(spaces.get(i).getName());
				spaceID_max_used = spaces.get(i).getId();
			}
		}
		System.out.println(temp);
		System.out.println(spaceID_max_used);
		
		
		try {
			FileInputStream in = new FileInputStream("/sdcard/" + spaceID_max_used + ".jpg");
			BufferedInputStream buf = new BufferedInputStream(in);
			Bitmap bMap = BitmapFactory.decodeStream(buf);
			image1.setImageBitmap(bMap);
			if (in != null) {
				in.close();
			}
			if (buf != null) {
				buf.close();
			}
		} catch (Exception e) {
			Log.e("Error reading file", e.toString());
		}
		
		TextView most_used_txt = (TextView) findViewById(R.id.most_used_txt);
		most_used_txt.setText(db.getSpaceNameByID(spaceID_max_used));
		
		Button full = (Button) findViewById(R.id.show_full);
		full.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resultList.setVisibility(View.VISIBLE);
			}
		});
				// TODO Auto-generated method stub

				TextView spaces_pie = (TextView) findViewById(R.id.spaces_pie);
				spaces_pie.setMaxLines(6);
				spaces_pie.setMovementMethod(new ScrollingMovementMethod());

				for (int i = 0; i < spaceManager.getAllSpaces().size(); i++) {
					spaces_pie.append("\n"
							+ spaceManager.getAllSpaces().get(i).toString()
							+ ": "
							+ db.retrieveSpaceStatistics(spaceManager
									.getAllSpaces().get(i)));
				}

				LinearLayout full_for_real = (LinearLayout) findViewById(R.id.full_statistics_for_real);
				full.setVisibility(View.INVISIBLE);

				// LinearLayout pie_layout = (LinearLayout)
				// findViewById(R.id.pie_layout);

				LinearLayout pie_container = (LinearLayout) findViewById(R.id.pie_container);

				// pie_layout.setVisibility(View.VISIBLE);
				// pie_container.setVisibility(View.VISIBLE);

				full_for_real.setVisibility(View.VISIBLE);


				drawPie();

	}

	@Override
	public void onResume() {
		super.onResume();
		spaceAdapter.notifyDataSetChanged();

	}

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
				v = vi.inflate(R.layout.statistics_item_view, null);
			}
			Space o = items.get(position);
			if (o != null) {
				TextView itemName = (TextView) v
						.findViewById(R.id.home_spaces_list_item_name);

				if (itemName != null) {
					itemName.setText(o.getCreated() + " \t " + o.getName());
				}

			}
			return v;
		}
	}

	public void drawPie() {
		List<PieChart> PieData = new ArrayList<PieChart>(0);

		PieChart Item;

		// Items from statistics table
		int MaxPieItems = db.getStatistics().size(); 
		
		int MaxCount = 0;
		int ItemCount = 0;
		List<Space> spaces = this.db.getStatistics();

		System.out.println(db.getStatistics().size());

		// for(int m =0 ; m<db.getStatistics().size(); m++){
		// System.out.println(db.getStatistics().get(m).getName().toString());
		// }

		// System.out.println(spaces.size());

		for (int i = 0; i < MaxPieItems; i++) {

			// System.out.println(spaces.get(i).getName());
			// Just insert here times one space is used.
			// Can use array list or List to store how many times one space was
			// changed sequentially.
			// Then just retrieve the data one by one, deleting components. -
			// same as with the colors in this class
			if (spaces.get(i).getName() != null) {
				ItemCount = Integer.parseInt(spaces.get(i).getName());
			} else
				ItemCount = 1;

			Item = new PieChart();
			Item.Count = ItemCount;

			// List of available colors Generator is not used
			List<Integer> color = new ArrayList<Integer>(0);
			color.add(Color.GREEN);
			color.add(Color.CYAN);
			color.add(Color.BLUE);
			color.add(Color.RED);
			color.add(Color.BLACK);
			color.add(Color.DKGRAY);
			color.add(Color.LTGRAY);
			color.add(Color.MAGENTA);
			color.add(Color.YELLOW);
			// Assign a color starting from begining
			if (color_int < 9) {
				//Choose a color from a list
				Item.Color = (int) color.get(color_int);
				color_int++;

				// add a new pie piece
				PieData.add(Item);
				MaxCount += ItemCount;
			} else
				// start choosing colors from beginning
				color_int = 0;
		}

		// make pie 4 times smaller than the size of the screen

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int Size = dm.heightPixels / 4;

		// Same color as the background

		int BgColor = Color.parseColor("#FFFFFF"); // Color.TRANSPARENT; - makes
													// it black

		// mBackgroundImage => Temporary image will be drawn with the content of pie view
		Bitmap mBackgroundImage = Bitmap.createBitmap(Size, Size,
				Bitmap.Config.RGB_565);

		PieChartView PieChartView = new PieChartView(this);
		PieChartView.setLayoutParams(new LayoutParams(Size, Size));
		PieChartView.setGeometry(Size, Size, 2, 2, 2, 2);
		PieChartView.setSkinParams(BgColor);
		PieChartView.setData(PieData, MaxCount);
		PieChartView.invalidate();

		// Draw Pie on Bitmap canvas

		PieChartView.draw(new Canvas(mBackgroundImage));
		PieChartView = null;

		ImageView mImageView = new ImageView(this);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		mImageView.setBackgroundColor(BgColor);
		mImageView.setImageBitmap(mBackgroundImage);

		LinearLayout target = (LinearLayout) findViewById(R.id.pie_container);
		target.addView(mImageView);
	}
}
