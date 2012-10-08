/*	Licensed to UbiCollab.org under one or more contributor
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

package org.ubicollab.nomad.auth;

import android.content.Context;

public class PrivilegedUser extends User {

	private boolean serverSynchronization;
	
	private String username;
	private String password;
		
	public PrivilegedUser(Context context, String username, String password) {
		super(context, User.TYPE_PRIVILEGED);
		this.username = username;
		this.password = password;
		
		// TODO setup rest of data using persistent storage.
	}


	@Override
	public void setMode(int MODE) {
		super.setMode(MODE);
		
		switch (MODE) {
		case User.MODE_ONLINE:
			this.serverSynchronization = true;
			break;
		case User.MODE_OFFLINE:
			this.serverSynchronization = false;
			break;
			
		case User.MODE_USER_PREFERENCES:
		default:
			this.serverSynchronization = true;

		}
	}



	public boolean isServerSynchronization() {
		return serverSynchronization;
	}

	public void setServerSynchronization(boolean serverSynchronization) {
		this.serverSynchronization = serverSynchronization;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
}
