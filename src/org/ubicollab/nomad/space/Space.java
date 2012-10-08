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

package org.ubicollab.nomad.space;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.util.Log;



/*
 * Space definiton class.
 * 
 * Author: Samuel Wej�us
 */
public class Space implements Serializable {

	public static final String SPACE_IDENTIFIER = "org.ubicollab.nomad.Space";
	public static final CharSequence[] DATA = {"Entity", "Location", "Category", "Rule", "Related"};


	/* Basic Space definition */
	private String id;
	private String created;
	private String modified;
	private String name;
	private String description;
	private String parent;
	private boolean shareable;
	private ArrayList<Entity> entities;
	private ArrayList<Rule> rules;
	private String information;

	/* Extensions */
	//	private Location location;
	//	private List<Space> related;
	//	private List<String> categories;
	//	private List<Rule> rules;
	//	private List<images> images;
	//	private dimensions;


	public Space() {
		this.id = "";
		this.created = "";
		this.modified = "";
		this.name = "(no name set)";
		this.description = "(no description)";
		this.information = "(no information)";
		this.parent = "";
		this.shareable = true;
		this.entities = new ArrayList<Entity>();
		this.rules = new ArrayList<Rule>();
	}

	public Space(String id, String created, String modified, String name, String description, String information, String parent, boolean shareable, ArrayList<Entity> entities, ArrayList<Rule> rules) {
		this.id = id;
		this.created = created;
		this.modified = modified;
		this.name = name;
		this.description = description;
		this.information = information;
		this.parent = parent;
		this.shareable = shareable;
		if (entities == null) {
			this.entities = new ArrayList<Entity>();
		} else {
			this.entities = entities;
		}
		if (rules == null) {
			this.rules = new ArrayList<Rule>();
		} else {
			this.rules = rules;
		}
	}

	/** Copy constructor */
	public Space(Space clone) {
		this(
				clone.id,
				clone.created,
				clone.modified,
				clone.name,
				clone.description,
				clone.information,
				clone.parent,
				clone.shareable,
				clone.entities,
				clone.rules
		);

		if (clone.entities != null) {
			//clone.entities = new ArrayList<Entity>(clone.entities);
			ArrayList<Entity> clonedList = new ArrayList<Entity>(clone.entities.size());
		    for (Entity entity : clone.entities) clonedList.add(new Entity(entity));
		    
		    this.entities = clonedList;
		}
		
		Log.d("(Space)", "Cloned space entities is: " + entities);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}


	public boolean isShareable() {
		return shareable;
	}

	public void setShareable(boolean shareable) {
		this.shareable = shareable;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}


	public ArrayList<Entity> getEntities() {
		return entities;
	}
	
	public ArrayList<Rule> getRules() {
		return rules;
	}

	public void setRules(ArrayList<Rule> rules) {
		// TODO should only store unique.
		this.rules = rules;
	}

	public void setEntities(ArrayList<Entity> entities) {
		// TODO should only store unique.
		this.entities = entities;
	}
	
	public void removeRules(int i){
		this.rules.remove(i);
	}
	
	@Override
	public String toString() {
		return name;
	}


}
