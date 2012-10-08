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

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Toast;

/*
 * LinearLayoutThatDetectsSoftKeyboard - a variant of LinearLayout that can detect when 
 * the soft keyboard is shown and hidden (something Android can't tell you, weirdly). 
 */

public class SoftKeyboardLinearLayout extends LinearLayout {

	public SoftKeyboardLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public interface Listener {
		public void onSoftKeyboardShown(boolean isShowing);
	}

	private Listener listener;

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = MeasureSpec.getSize(heightMeasureSpec);
		Activity activity = (Activity)getContext();
		Rect rect = new Rect();
		
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		
		int statusBarHeight = rect.top;
		int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
		int diff = (screenHeight - statusBarHeight) - height;
		
		if (listener != null) {
			listener.onSoftKeyboardShown(diff>128); // assume all soft keyboards are at least 128 pixels high
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);       
	}

}


/* Setup

implements SoftKeyboardLinearLayout.Listener

public boolean isSoftKeyboardShowing = false;

// implement interface then:
SoftKeyboardLinearLayout my = (SoftKeyboardLinearLayout) getParent().findViewById(R.id.layout_main);
my.setListener(this);

	@Override
	public void onSoftKeyboardShown(boolean isShowing) {
	
		this.isSoftKeyboardShowing = isShowing;
		
		//Toast.makeText(getApplicationContext(), "Keybard is showing: " + isShowing, Toast.LENGTH_SHORT).show();
	}

*/

