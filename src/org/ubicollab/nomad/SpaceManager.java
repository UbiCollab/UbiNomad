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
import org.ubicollab.nomad.space.Entity;
import org.ubicollab.nomad.space.Location;
import org.ubicollab.nomad.space.Rule;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.nomad.util.DatabaseExceptions;
import org.ubicollab.nomad.util.MainDB;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;



/**
 * The SpaceManager class is responsible for maintaining a local copy of the
 * list of all spaces + specialized lists of spaces (nearby, favorites...)
 * All work is in first hand made on local copy then merged with backend database
 * and future server connection.
 * 
 * Important note: usage of 'setters' on list object DOES NOT guarantee a permanent
 * storage of those changes! The changes will only be reflected in the current 
 * instance of the currently used object holder list. A user MUST use the 
 * update/delete/create methods or else gremlings will eat your soul.
 * 
 * @Author: Samuel Wej�us
 * @Maintained by: Samuel Wej�us
 */
public class SpaceManager extends Activity {

	private static SpaceManager instance;
	
	private MainDB db;
	public Broadcast bc;
	public Context con;
	/*
	 * Note: it is impossible due to restrictions in the Java language to make these immutable.
	 * This is because the Collections.unmodifiableList(List<T>) construct only makes the list 
	 * immutable, objects is still sent as modifiable references.
	 */
	private ArrayList<Space> allSpaces;
	private ArrayList<Space> categorySpaces;
	private ArrayList<Space> recentSpaces;
	private ArrayList<Space> nearbySpaces;
	private ArrayList<Space> starredSpaces;
	private ArrayList<Space> log;
	private ArrayList<Rule> rules;
	private Space currentSpace;

	
	
	public SpaceManager(Context context) {
		
		db = new MainDB(context);
		bc = new Broadcast();
		con = context;
		
		allSpaces = (ArrayList<Space>) db.selectAll();
		
		categorySpaces = (ArrayList<Space>) db.selectAll();
		recentSpaces = (ArrayList<Space>) db.selectAll();
		nearbySpaces = (ArrayList<Space>) db.selectAll();
		starredSpaces = (ArrayList<Space>) db.selectAll();
		currentSpace = db.getCurrentSpace();
		log = (ArrayList<Space>) db.getLog();
		
	//	currentSpace = db.getCurrentSpace();
		//currentSpace = db.getCurrentSpace();
	}
	
	/**
	 * Return the current instance of class. If it not exists it creates it.
	 */	 
	public static SpaceManager getInstance(Context context) {
		if (instance == null) {
			instance = new SpaceManager(context);
		}

		return instance;
	}

	
	public ArrayList<Space> getLog(){	
		return this.log;
	}
	
	public ArrayList<Space> getLogDateUsed(){	
		return this.log;
	}
	
	/**
	 * Returns all spaces associated with current user.
	 */
	public ArrayList<Space> getAllSpaces() {
		return this.allSpaces;
	}

	public ArrayList<Space> getSpacesByCategory(String Category) {
		return categorySpaces;
	}

	public ArrayList<Space> getNeabySpaces(Location location) {
		return nearbySpaces;
	}

	public ArrayList<Space> getRecentVisitedSpaces() {
		return recentSpaces;
	}

	public ArrayList<Space> getStarredSpaces() {
		return starredSpaces;
	}
	
	public ArrayList<Rule> getRules(){
		return rules;
	}
	
	
	/**
	 * Sets the supplied space to current space (system wide)
	 */
	public void setCurrentSpace(Space space) {
		this.currentSpace = space;
		db.setCurrentSpace(currentSpace);
		db.insertIntoStatistics(currentSpace);;
		bc.broadCastSetCurrentSpace(con,space);
		bc.broadcastApplyRules(con, space);
		bc.broadcastSearchTODO(con, space);
		log.add(space);
		
	}
	
	public Space getCurrentSpace() {
		return this.currentSpace;
	}

	/**
	 * Updates all references to this space to use new definitions from supplied space. 
	 * This method MUST be used for all changes made to spaces to make sure these changes
	 * are permanently stored.
	 */
	public void updateSpace(Space space) {
		
		try {
			db.updateSpace(space);
		} catch (DatabaseExceptions e) {
			e.printStackTrace();
		}
		// Re:populates the specialized spacelists from database.
		allSpaces = (ArrayList<Space>) db.selectAll();
		categorySpaces = (ArrayList<Space>) db.selectAll();
		recentSpaces = (ArrayList<Space>) db.selectAll();
		nearbySpaces = (ArrayList<Space>) db.selectAll();
		starredSpaces = (ArrayList<Space>) db.selectAll();
		
		
	}

	/**
	 * Creates a new space and stores it in local database + server.
	 * Also updates all lists to include this new space.
	 */
	public void createNewSpace(Space space) {
		Space newSpace = null;
		
		try {
			newSpace = db.createNewSpace(space);
		} catch (DatabaseExceptions e) {
			e.printStackTrace();
		}

		if(newSpace!=null){
			allSpaces.add(newSpace);
		}
		
		// Re:populates the specialized spacelists from database.
		categorySpaces = (ArrayList<Space>) db.selectAll();
		recentSpaces = (ArrayList<Space>) db.selectAll();
		nearbySpaces = (ArrayList<Space>) db.selectAll();
		starredSpaces = (ArrayList<Space>) db.selectAll();
	}
	
	/**
	 * Removes a space from all lists that might contain that space and also deletes
	 * it from the local database. Should also delete from server (future implementation).
	 * 
	 * Implemented as delete from local because its probably cheaper to make lookups
	 * in local lists of specialized spaces on the assumption that they are pretty small.
	 */
	public void deleteSpace(Space space){
	
		if (allSpaces.contains(space)) allSpaces.remove(space);
		if (categorySpaces.contains(space)) categorySpaces.remove(space);
		if (recentSpaces.contains(space)) recentSpaces.remove(space);
		if (nearbySpaces.contains(space)) nearbySpaces.remove(space);
		if (starredSpaces.contains(space)) starredSpaces.remove(space);
	
		// TODO refactor
		if (space == currentSpace) {
			currentSpace = null;
		}
		
		try {
			db.deleteSpace(space);
		} catch (DatabaseExceptions e) {
			e.printStackTrace();
		}
	}
	
	public void createRules(String id, ArrayList<Rule> rules){
		for(int i = 0; i< allSpaces.size(); i++){
			rules.add(rules.get(i));
		}
		this.db.createRules(id, rules);
	}	
	
	public void insertStatistics(Space space)
	{
		db.insertIntoStatistics(space);
	}
	
}
