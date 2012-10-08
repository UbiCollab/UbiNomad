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

package org.ubicollab.nomad.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.ubicollab.nomad.R;
import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.space.Space;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ImportActivity extends Activity {

	TextView textFile, textFileName, textFolder;

	private static final int PICKFILE_RESULT_CODE = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);

		Button buttonPick = (Button)findViewById(R.id.buttonpick);
		textFile = (TextView)findViewById(R.id.textfile);
		textFolder = (TextView)findViewById(R.id.textfolder);
		textFileName = (TextView)findViewById(R.id.textfilename);

		buttonPick.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("file/*");
				startActivityForResult(intent,PICKFILE_RESULT_CODE);

			}});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode){
		case PICKFILE_RESULT_CODE:
			if(resultCode==RESULT_OK){

				String FilePath = data.getData().getPath();
				String FileName = data.getData().getLastPathSegment();
				int lastPos = FilePath.length() - FileName.length();
				String Folder = FilePath.substring(0, lastPos);

				textFile.setText("Full Path: \n" + FilePath + "\n");
				textFolder.setText("Folder: \n" + Folder + "\n");
				textFileName.setText("File Name: \n" + FileName + "\n");

				importSpaces(FilePath);
			}
			break;

		}
	}

	private void importSpaces(String filePath) {
		
		XmlPullSpaceListParser XmlParser = new XmlPullSpaceListParser(filePath);
		ArrayList<Space> spaces = XmlParser.parse();

		TextView resultView = (TextView)findViewById(R.id.result);
		resultView.setText("Imported: " + spaces.size()+" space(s)");
		
		SpaceManager sm = SpaceManager.getInstance(getApplicationContext());
		for (int i = 0; i < spaces.size(); i++) {
			sm.createNewSpace(spaces.get(i));
		}
		
		Button doneButton = (Button)findViewById(R.id.button_import_done);
		doneButton.setVisibility(View.VISIBLE);
		
	}

	public void done(View v) {
		finish();
	}

}
