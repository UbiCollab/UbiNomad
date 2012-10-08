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

package org.ubicollab.nomad.util;

import java.util.ArrayList;

import org.ubicollab.nomad.Nomad;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

/**
 * The purpose of this Activity is to manage the activities in a tab.
 * Note: Child Activities can handle Key Presses before they are seen here.
 * 
 * @author Samuel Wejeus
 * @author (Some code based on ideas from: )
 */
public class TabGroupActivity extends ActivityGroup  {

	private ArrayList<String> mIdList;	

	public static final String GOT_RETURN_VALUE = "org.ubicollab.nomad.TabGroup.GOT_RETURN_VALUE";
	public static final String FINISH_CALLER = "org.ubicollab.nomad.TabGroup.FINISH_CALLER";
	public static final String DATA = "org.ubicollab.nomad.TabGroup.DATA";


	/**
	 * this.getLocalActivityManager().dispatchCreate(container); Is broken and cant restore previous state.
	 * 
	 * Instead we restore manually and restart all activities in current queue.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("TabGroupActivity", "onCreate");

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("ACTIVITY_MANAGER_STATE") && savedInstanceState.containsKey("ACTIVITY_LIST") ) {	

				this.mIdList = savedInstanceState.getStringArrayList("ACTIVITY_LIST");
				SparseArray<Intent> activityIntents = savedInstanceState.getSparseParcelableArray("ACTIVITY_INTENTS");
				Bundle container = savedInstanceState.getBundle("ACTIVITY_MANAGER_STATE");

				LocalActivityManager manager = getLocalActivityManager();

				String[] tempArray = new String[mIdList.size()]; 
				container.keySet().toArray(tempArray);


				for (int i = 0; i < this.mIdList.size(); i++) {			
					String activityID = mIdList.get(i);

					Intent tempIntent = activityIntents.get(i);
					manager.startActivity(activityID, tempIntent);

					Log.i("TabGroupActivity", "ID: "+ i +" Started activity: " + activityID + " with intent: " + tempIntent);
				}
			}
		} else {
			mIdList = new ArrayList<String>();
		}
	}

	/**
	 * When everything is restored we restart last running activity and set current contentView to correspond to that of last activity.
	 */
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		Log.i("TabGroupActivity", "onRestoreInstanceState: ");
		LocalActivityManager manager = getLocalActivityManager();


		int index = mIdList.size()-1;				
		String lastId = mIdList.get(index);

		Log.i("TabGroupActivity", "Intend to start activity: " + lastId);
		Intent lastIntent = manager.getActivity(lastId).getIntent();

		Log.i("TabGroupActivity", ".. with intent: " + lastIntent);
		Window newWindow = manager.startActivity(lastId, lastIntent);

		setContentView(newWindow.getDecorView());
	}

	/**
	 * Local activity manager is broken. See: http://code.google.com/p/android/issues/detail?id=6225 for similar issue.
	 * 
	 * Instead we manually save all running activities and their intents, let the activity restart and restore for savedInstanceState bundle
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		Log.i("TabGroupActivity", "(BOF) onSaveInstanceState: ");

		Bundle bundle = this.getLocalActivityManager().saveInstanceState();
		outState.putBundle("ACTIVITY_MANAGER_STATE", bundle);
		outState.putStringArrayList("ACTIVITY_LIST", mIdList);

		SparseArray<Intent> activityIntents = new SparseArray<Intent>();
		for (int i = 0; i < mIdList.size(); i++) {
			Activity tmpActivity = this.getLocalActivityManager().getActivity(mIdList.get(i));
			Intent tmpIntent = tmpActivity.getIntent();
			activityIntents.put(i, tmpIntent);
			Log.i("TabGroupActivity", "ID: "+ i +" Packing intent: " + tmpIntent.toString());
		}
		outState.putSparseParcelableArray("ACTIVITY_INTENTS", activityIntents);

		Log.i("TabGroupActivity", "(EOF) onSaveInstanceState: ");


	}

	/**
	 * This is called when a child activity of this one calls its finish method.
	 * This implementation calls {@link LocalActivityManager#destroyActivity} on the child activity
	 * and starts the previous activity.
	 * If the last child activity just called finish(),this activity (the parent),
	 * calls finish to finish the entire group.
	 */
	@Override
	public void finishFromChild(Activity child) {
		Log.i("TabGroup","finishFromChild " + child.getLocalClassName());

		child.getWindow().getDecorView().setAnimation(TabAnimation.outToRightAnimation());

		LocalActivityManager manager = getLocalActivityManager();

		// Since finish() has been called from child we always want to hide soft keyboard
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(Nomad.tabHost.getApplicationWindowToken(), 0);	

		int index = mIdList.size()-1;
		if (index < 1) {
			finish();
			return;
		}

		Bundle oldBundle = child.getIntent().getExtras();
		/* Kill previous child if caller wants that */
		if ((oldBundle != null) && (oldBundle.containsKey(GOT_RETURN_VALUE))) {
			if (oldBundle.containsKey(FINISH_CALLER)) {
				manager.destroyActivity(mIdList.get(index), true);
				mIdList.remove(index);
				index--;
			}
		}


		manager.destroyActivity(mIdList.get(index), true);
		mIdList.remove(index);
		index--;
		String lastId = mIdList.get(index);
		Intent lastIntent = manager.getActivity(lastId).getIntent();

		if ((oldBundle != null) && (oldBundle.containsKey(GOT_RETURN_VALUE))) {
			if (oldBundle.containsKey(DATA)) {
				lastIntent.putExtra(DATA, oldBundle.getSerializable(DATA));
				//Toast.makeText(getApplicationContext(), "Host: " + lastIntent.getExtras().getSerializable(DATA).toString(), Toast.LENGTH_SHORT).show();
			}
		}

		Window newWindow = manager.startActivity(lastId, lastIntent);
		setContentView(newWindow.getDecorView());
	}



	/**
	 * Starts an Activity as a child Activity to this.
	 * @param Id Unique identifier of the activity to be started.
	 * @param intent The Intent describing the activity to be started.
	 * @throws android.content.ActivityNotFoundException.
	 */
	public void startChildActivity(String Id, Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Window window = getLocalActivityManager().startActivity(Id,intent);
		window.getDecorView().setAnimation(TabAnimation.inFromRightAnimation());

		if (window != null) {
			mIdList.add(Id);
			setContentView(window.getDecorView());
		}
	}


	/**
	 * If a Child Activity handles KeyEvent.KEYCODE_BACK.
	 * Simply override and add this method.
	 */
	@Override
	public void onBackPressed() {

		Log.d(this.getLocalClassName(), "Backbutton pressed.");

		int length = mIdList.size();
		if (1 < length) {
			Activity current = getLocalActivityManager().getActivity(mIdList.get(length-1));
			current.finish();
		} else {
			finish();
		}

	}

}





/* Commented out by Samuel. Bug found: when both onKeyDown and override:onBackPressed Android runs em both. Twice! */
//	/**
//	 * The primary purpose is to prevent systems before android.os.Build.VERSION_CODES.ECLAIR
//	 * from calling their default KeyEvent.KEYCODE_BACK during onKeyDown.
//	 */
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			//preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	/**
//	 * Overrides the default implementation for KeyEvent.KEYCODE_BACK
//	 * so that all systems call onBackPressed().
//	 */
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			onBackPressed();
//			return true;
//		}
//		return super.onKeyUp(keyCode, event);
//	}
