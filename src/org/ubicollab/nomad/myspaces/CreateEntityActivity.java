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

package org.ubicollab.nomad.myspaces;

import org.ubicollab.nomad.R;
import org.ubicollab.nomad.space.Entity;
import org.ubicollab.nomad.util.TabGroupActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/*
 * @author Samuel Wejeus
 */
public class CreateEntityActivity extends Activity {

	private EditText descriptionView;
	private EditText typeView;
	private EditText dataView;


	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_entity);

		descriptionView = (EditText) findViewById(R.id.modify_entity_description);
		typeView = (EditText) findViewById(R.id.modify_entity_type);
		dataView = (EditText) findViewById(R.id.modify_entity_data);
	}

	public void saveClick(View v) {
		Entity entity = new Entity(null, descriptionView.getText().toString(), typeView.getText().toString(), dataView.getText().toString());

		Intent i = getIntent();
		i.putExtra(TabGroupActivity.GOT_RETURN_VALUE, true);
		i.putExtra(TabGroupActivity.DATA, entity);

		finish();
	}

	public void cancelClick(View v) {
		finish();
	}

}
