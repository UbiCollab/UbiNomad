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

import org.ubicollab.nomad.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class LoginActivity extends Activity {

	FrontController frontController;
	InputMethodManager mgr;

	EditText username;
	EditText password;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		try {
			frontController = FrontController.getInstance(getApplicationContext());
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		username = (EditText) findViewById(R.id.login_text_username);
		password = (EditText) findViewById(R.id.login_text_password);

	}

	public void loginPrivilegedUser(View v) {
		try {
			frontController.loginUser(new PrivilegedUser(getApplicationContext(), username.getText().toString(), password.getText().toString()));
		} catch (AuthorizationException e) {
			e.printStackTrace();
		}
		
		hideVirtualKeyboard();
		finish();
	}

	public void loginLocalUser(View v) {
		try {
			frontController.loginUser(new LocalUser(getApplicationContext()));
		} catch (AuthorizationException e) {
			e.printStackTrace();
		}
		
		hideVirtualKeyboard();
		finish();
	}



	public void hideVirtualKeyboard() {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(username.getWindowToken(), 0);
		mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
	}
}
