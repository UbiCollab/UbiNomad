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

package org.ubicollab.nomad;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.ubicollab.nomad.auth.AuthorizationException;
import org.ubicollab.nomad.auth.FrontController;
import org.ubicollab.nomad.space.Rule;
import org.ubicollab.nomad.space.Space;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Class responsible for handling broadcasts from 3rd party applications
 * @author Fredrik Bjørland
 *
 */

public class IncomingReceiver extends BroadcastReceiver {
	public Broadcast bc;
	public FrontController frontController;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (broadcastAuthenticated(context)) {
			if (intent.getAction().equals(
					"org.ubicollab.ubical.setcurrentspace")) {

				setCurrentSpaceOnReceive(context, intent);

			} else if (intent.getAction().equals(
					"org.ubicollab.ubical.setspace")) {

				createSpaceOnReceive(context, intent);

			} else if (intent.getAction().equals(
					"org.ubicollab.ubical.getspacelist")) {
				sendSpaceListOnReceive(context);
				sendSpaceIDListOnReceive(context);
			} else if (intent.getAction().equals(
					"org.ubicollab.ubirule.getspacelist")) {
				// Use instead of that Parceable object to send as extra
				sendSpaceListOnReceive(context);
				sendSpaceIDListOnReceive(context);
			} else if (intent.getAction().equals(
					"org.ubicollab.ubirule.sendcurrentspace")) {
				sendCurrentSpaceOnReceive(context, intent);
			} else if (intent.getAction().equals(
					"org.ubicollab.ubirule.sendspacerules")) {
				setRulesOnReceive(context, intent);
			}
		}
	}

	private void setRulesOnReceive(Context context, Intent intent) {
		ArrayList<Rule> rulesObj = new ArrayList<Rule>();
		ArrayList<String> rules = intent
				.getStringArrayListExtra("org.ubicollab.ubirule.sendspacerules");
		String spaceID = intent.getStringExtra("space");

		for(int i = 0; i<rules.size(); i++){
			Rule rule = null;
			try {
				rule = new Rule(String.valueOf(i), rules.get(i));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rulesObj.add(rule);
		}
		
		SpaceManager sm = SpaceManager.getInstance(context);
		sm.createRules(spaceID, rulesObj);
	}

	/**
	 * Method that check if the user is authorized to send broadcasts
	 */
	
	public Boolean broadcastAuthenticated(Context context) {

		try {
			frontController = FrontController.getInstance(context);
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean auth = false;

		auth = frontController.getUser().isRecieveBroadcasts();

		return auth;

	}

	/**
	 * Handle incoming intent with a string name, searches if this name exists in the spacelist. if it does it sets this name to the 
	 * new current space
	 * @param context
	 * @param intent
	 */
	
	public void setCurrentSpaceOnReceive(Context context, Intent intent) {
		String payload;
		ArrayList<Space> spacelist;

		payload = intent.getStringExtra("org.ubicollab.ubical.setcurrentspace");
		SpaceManager sm = SpaceManager.getInstance(context);
		spacelist = sm.getAllSpaces();
		for (int i = 0; i < spacelist.size(); i++) {
			if (spacelist.get(i).getName().toLowerCase()
					.equals(payload.toLowerCase())) {
				sm.setCurrentSpace(spacelist.get(i));
				Toast.makeText(context, "New current space set to: " + payload,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void sendCurrentSpaceOnReceive(Context context, Intent intent) {
		if (getCurrentSpace(context) != null) {
			System.out.println("Current space from UbiNomad is: "
					+ getCurrentSpace(context));

			bc = new Broadcast();
			bc.broadcastCurrentSpace(context, getCurrentSpace(context));
		} else
			System.out.println("Current space at UbiNomad is already null");
	}

	
	/**
	 * Handle incoming intent with a string name and creates a new space with this name
	 * @param context
	 * @param intent
	 */
	
	public void createSpaceOnReceive(Context context, Intent intent) {
		String payload;
		Space space;
		payload = intent.getStringExtra("org.ubicollab.ubical.setspace");
		SpaceManager sm = SpaceManager.getInstance(context);
		if (compareSpaces(context, payload, sm)) {
			space = new Space();
			space.setName(payload);
			sm.createNewSpace(space);
			Toast.makeText(context, "New space created: " + payload,
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "Space allready exist: " + payload,
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Broadcast an array list with all the space names
	 * @param context
	 */
	
	public void sendSpaceListOnReceive(Context context) {
		bc = new Broadcast();
		;
		bc.broadcastSpaceList(context, getSpaceList(context));
	}

	/**
	 * Broadcast an array list with all the space ids
	 * @param context
	 */
	
	public void sendSpaceIDListOnReceive(Context context) {
		bc = new Broadcast();
		bc.broadcastSpaceIDList(context, getSpaceIDList(context));
	}

	//Help methods
	
	public boolean compareSpaces(Context context,String spaceName,SpaceManager sm) {
		ArrayList<Space> spacelist;
		spacelist = sm.getAllSpaces();
		for (int i = 0; i < spacelist.size(); i++) {
			if (spacelist.get(i).getName().toLowerCase()
					.equals(spaceName.toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<String> getSpaceList(Context context) {
		ArrayList<Space> spacelist;

		SpaceManager sm = SpaceManager.getInstance(context);
		spacelist = sm.getAllSpaces();
		ArrayList<String> stringList = new ArrayList<String>();
		for (int i = 0; i < spacelist.size(); i++) {
			stringList.add(i, spacelist.get(i).getName());
		}
		return stringList;
	}

	public ArrayList<String> getSpaceIDList(Context context) {
		ArrayList<Space> spacelist;

		SpaceManager sm = SpaceManager.getInstance(context);
		spacelist = sm.getAllSpaces();
		ArrayList<String> stringList = new ArrayList<String>();
		for (int i = 0; i < spacelist.size(); i++) {
			stringList.add(i, spacelist.get(i).getId());
		}
		return stringList;
	}
	public Space getCurrentSpace(Context context) {
		Space current = null;

		SpaceManager sm = SpaceManager.getInstance(context);
		current = sm.getCurrentSpace();

		return current;
	}
	
}
	

