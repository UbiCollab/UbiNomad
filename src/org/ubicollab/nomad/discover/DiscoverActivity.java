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

package org.ubicollab.nomad.discover;

import java.util.ArrayList;

import org.ubicollab.nomad.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DiscoverActivity extends Activity {
    /** Called when the activity is first created. */
//	private ArrayList<String> spaceNames = null;
//    SpaceManager sm = SpaceManager.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState)throws NullPointerException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_activity);
    /*
    Button myDynamicallyCreatedButton = new Button(this);
    View v = findViewById(R.layout.discover_activity);
    myDynamicallyCreatedButton.setText("click me");
    myDynamicallyCreatedButton.setOnClickListener(new View.OnClickListener() {
        
     	public void onClick(View v) {
         	
         	Intent i = new Intent ();
         	i.setAction("org.ubicollab.nomad");
         	
         	v.getContext().sendBroadcast(i);
         	
         }
    });
   
    
    ArrayList<View> l = new ArrayList<View>();
    l.add(myDynamicallyCreatedButton);
    v.addFocusables(l, 0);
    */
        
    final Button createSpaceButton = (Button) findViewById(R.id.create_space_button);
    createSpaceButton.setOnClickListener(new View.OnClickListener() {
       //send to widget
    	public void onClick(View v) {
        	
//        	Intent i = new Intent ();
//        	i.setAction("org.ubicollab.nomad");
//        	
//        	v.getContext().sendBroadcast(i);
        	
        }

        
        
    });
    
//    for(Space space : sm.getAllSpaces()){
//    	spaceNames.add(space.getName());
//    }
    
    

    }
    

}
