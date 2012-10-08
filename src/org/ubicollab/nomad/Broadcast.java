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


import java.util.ArrayList;

import org.ubicollab.nomad.auth.AuthorizationException;
import org.ubicollab.nomad.auth.FrontController;
import org.ubicollab.nomad.space.Space;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast class responsible for communication with 3rd party applications
 * @author Fredrik Bjørland
 *
 */

public class Broadcast extends Activity{
	public FrontController frontController;

	
	/**
	 * Method that check if the user is authorized to send broadcasts
	 */
	public Boolean broadcastAuthenticated(Context context) {
		
		
		try {
			frontController = FrontController.getInstance(context);
		} catch (AuthorizationException e) {
	
			e.printStackTrace();
		}
		boolean auth = false;
		
			auth = frontController.getUser().isSendBroadcasts();
		

		return auth;
		
	}
	
	/**
	 * Braodcast space name when a new space is beeign set to current space
	 * @param context
	 * @param space
	 */
	
	public void broadCastSetCurrentSpace(Context context,Space space) {
		
		if(broadcastAuthenticated(context)) {
			String payload = space.getName();
			Intent broadCastIntent = new Intent();
			broadCastIntent.setAction(Constants.SET_CURRENT_SPACE_IDENTIFIER);
			broadCastIntent.putExtra(Constants.SET_CURRENT_SPACE_IDENTIFIER,payload);
			context.sendBroadcast(broadCastIntent);
		}
			
	
	}
	
	/**
	 * Broadcast space object when a new space update is being committed
	 * @param context
	 * @param space
	 */
	
	public void broadCastUpdateSpace(Context context,Space space) {
		if(broadcastAuthenticated(context)) {
			Intent broadCastIntent = new Intent();
			broadCastIntent.setAction(Constants.UPDATE_SPACE_IDENTIFIER);
			broadCastIntent.putExtra(Constants.UPDATE_SPACE_IDENTIFIER,space);
			context.sendBroadcast(broadCastIntent);
		}
		
	
	}

	/**
	 * Broadcast a newly created space object upon creation 
	 * @param context
	 * @param space
	 */
	public void broadCastCreateNewSpace(Context context,Space space) {

		if(broadcastAuthenticated(context)) {
			Intent broadCastIntent = new Intent();
			broadCastIntent.setAction(Constants.CREAET_NEW_SPACE_IDENTIFIER);
			broadCastIntent.putExtra(Constants.CREAET_NEW_SPACE_IDENTIFIER,space);
			context.sendBroadcast(broadCastIntent);
		}
		
	}
	
	/**
	 * Broadcast space object that is being deleted
	 * @param context
	 * @param space
	 */
	
	public void broadCastDeleteSpace(Context context,Space space) {

		if(broadcastAuthenticated(context)) {
			Intent broadCastIntent = new Intent();
			broadCastIntent.setAction(Constants.DELETE_SPACE_IDENTIFIER);
			broadCastIntent.putExtra(Constants.DELETE_SPACE_IDENTIFIER,space);
			context.sendBroadcast(broadCastIntent);
		}
		
	}
	
	/**
	 * Broadcast an arraylist over all space names
	 * @param context
	 * @param spacelist
	 */

	public void broadcastSpaceList(Context context,ArrayList<String> spacelist) {
		if(broadcastAuthenticated(context)) {
			Intent broadCastIntent = new Intent();
			broadCastIntent.setAction(Constants.PIGIBACK_UBICAL_IDENTIFIER);
			broadCastIntent.putStringArrayListExtra(Constants.PIGIBACK_UBICAL_IDENTIFIER,spacelist);
			context.sendBroadcast(broadCastIntent);	
		}
	}
	/**
	 * Broadcast an arraylist over all space ids
	 * @param context
	 * @param spaceIDList
	 */
	
	public void broadcastSpaceIDList(Context context,
		ArrayList<String> spaceIDList) {
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(Constants.SPACES_ID_LIST);
		broadCastIntent.putStringArrayListExtra(Constants.SPACES_ID_LIST, spaceIDList);
		context.sendBroadcast(broadCastIntent);	
	}

	public void broadcastCurrentSpace(Context context, Space currentSpace) {
		
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(Constants.CURRENT_SPACE);
		broadCastIntent.putExtra(Constants.CURRENT_SPACE_ID, currentSpace.getId());
		broadCastIntent.putExtra(Constants.CURRENT_SPACE_NAME, currentSpace.getName());
		context.sendBroadcast(broadCastIntent);	
	}

	public void broadcastApplyRules(Context context, Space currentSpace) {
		
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(Constants.APPLY_RULES);
		broadCastIntent.putExtra(Constants.CURRENT_SPACE_ID, currentSpace.getId());
		context.sendBroadcast(broadCastIntent);	
	}
	
	public void broadcastSearchTODO(Context context, Space currentSpace){
		System.out.println("Broadcasting search for a List from the ubiNomad");
		
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(Constants.SEARCH_LIST);
		broadCastIntent.putExtra(Constants.CURRENT_SPACE_NAME, currentSpace.getName());
		context.sendBroadcast(broadCastIntent);	
	}
}
