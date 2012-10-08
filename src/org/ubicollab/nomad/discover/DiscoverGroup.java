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

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
 
public class DiscoverGroup extends ActivityGroup {  
	  
	        // Keep this in a static variable to make it accessible for all the nested activities, lets them manipulate the view  
	    public static DiscoverGroup group;  
	  
	        // Need to keep track of the history if you want the back-button to work properly, don't use this if your activities requires a lot of memory.  
	    private ArrayList<View> history;  
	  
	    @Override  
	    protected void onCreate(Bundle savedInstanceState) {  
	          super.onCreate(savedInstanceState);  
	          this.history = new ArrayList<View>();  
	          group = this;  
	  
	              // Start the root activity withing the group and get its view  
	          View view = getLocalActivityManager().startActivity("DiscoverActivity", new  
	                                            Intent(this,DiscoverActivity.class)  
	                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))  
	                                            .getDecorView();  
	  
	              // Replace the view of this ActivityGroup  
	          replaceView(view);  
	  
	       }  
	  
	    public void replaceView(View v) {  
	                // Adds the old one to history  
	        history.add(v);  
	                // Changes this Groups View to the new View.  
	        setContentView(v);  
	    }  
	  
	    public void back() {  
	        if(history.size() > 0) {  
	            history.remove(history.size()-1);  
	            setContentView(history.get(history.size()-1));  
	        }else {  
	            finish();  
	        }  
	    }  
 

	  
	}  


