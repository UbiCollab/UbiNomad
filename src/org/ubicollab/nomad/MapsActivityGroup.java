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

import org.ubicollab.nomad.util.TabGroupActivity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MapsActivityGroup extends TabGroupActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		View view = getLocalActivityManager().startActivity("ReferenceName", new
//		Intent(this,GoogleMapsActivity.class)
//		.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//		.getDecorView();
//		this.setContentView(view);
//
//		
		//getLocalActivityManager().startActivity("GoogleMapsActivity", new Intent(this, GoogleMapsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
	}

}  
