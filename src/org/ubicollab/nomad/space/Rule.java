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
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Rule implements Serializable{
	
	private String id;
	private String description;
	private Entity entity;
	private String statement;
	
	public Rule(String id, String description) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		this.description = description;
		
	}
	
	
	public void setId(String id) {
		this.id = id;
	}


	public String getId() {
		return id;
	}


	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public Entity getEntity() {
		return entity;
	}



	public void setEntity(Entity entity) {
		this.entity = entity;
	}



	public String getStatement() {
		return statement;
	}



	public void setStatement(String statement) {
		this.statement = statement;
	}

	@Override
	public String toString() {
		return "Rule [description=" + description + ", entity=" + entity + ", statement=" + statement + "]";
	}
	
	
}
