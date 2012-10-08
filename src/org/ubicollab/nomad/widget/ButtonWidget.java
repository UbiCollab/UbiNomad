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

package org.ubicollab.nomad.widget;

import java.util.Date;

import org.ubicollab.nomad.R;
import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.myspaces.SpaceInfoActivity;
import org.ubicollab.nomad.space.Space;
//import org.ubicollab.nomad.widget.ButtonWidget.WidgetUpdateService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ButtonWidget extends AppWidgetProvider {

	private static final String DEBUG_TAG = null;
	public static boolean WidgetUpdateService2 = false;
	public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
	public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";

	public int myAppId;
	
	private static boolean ledIsOn = true;
	public int buttonOne = R.layout.button_widget;
    public int buttonTwo = R.layout.button_widget_two;
	int layoutID;
	RemoteViews remoteViews;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		WidgetUpdateService2 = true;
		//Toast.makeText(context, "On update", Toast.LENGTH_SHORT).show();
		RemoteViews remoteViews = null;
		
	    if (ledIsOn){
		        layoutID = buttonOne;
	     }
          else {
               layoutID = buttonTwo;
         }

		this.myAppId=appWidgetIds[0];
		//gets the current space
	
		remoteViews = new RemoteViews(context.getPackageName(), layoutID);
		
		SpaceManager spaceManager = SpaceManager.getInstance(context);
		Space space = spaceManager.getCurrentSpace();
		String asdf;
		
		if (space == null) {
			asdf = "LOCATION:\nSet Space";
		} else {
			asdf = "LOCATION:\n" + space.getName();
		}
		
		remoteViews.setTextViewText(R.id.list_item_text, asdf);
		Intent configIntent = new Intent(context, WidgetListSpacesActivity.class);
		configIntent.setAction(ACTION_WIDGET_CONFIGURE);
		
		Intent active = new Intent(context, ButtonWidget.class);
		active.setAction(ACTION_WIDGET_RECEIVER);
		active.putExtra("msg", "Update");
	
		PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
		PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
	
		remoteViews.setOnClickPendingIntent(R.id.button_one, actionPendingIntent);
		remoteViews.setOnClickPendingIntent(R.id.button_two, configPendingIntent);

		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		
		for (int i = 0; i < appWidgetIds.length; i++) {
	      appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);	
		}
}

	public class WidgetUpdateService extends Service {
		Thread widgetUpdateThread = null;
		protected ContextWrapper context;
		private static final String DEBUG_TAG = "WidgetUpdateService";
		private static final int START_REDELIVER_INTENT = 0;

		@Override
		public int onStartCommand(Intent intent, int flags, final int startId) {
			
			widgetUpdateThread = new Thread() {
				public void run() {
					if (!WidgetUpdateService.this.stopSelfResult(startId)) {
						Log.e(DEBUG_TAG, "Failed to stop service");
					}
				}	
			};
			widgetUpdateThread.start();
			return START_REDELIVER_INTENT;
		}
		
		@Override
		public void onDestroy() {
			widgetUpdateThread.interrupt();
			super.onDestroy();
			WidgetUpdateService2 = false;
		}
		
		@Override
		public IBinder onBind(Intent intent) {
			// no binding; can't from an App Widget
			return null;
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// v1.5 fix that doesn't call onDelete Action
		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			
			WidgetUpdateService2 = false;
			final int appWidgetId = intent.getExtras().getInt(
					
					
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] { appWidgetId });
				
				 
			}
		} else {
			// check, if our Action was called
			if (intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
				
				WidgetUpdateService2 = true;
				
				this.onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ButtonWidget.class)));
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				
				if (ledIsOn){
				        layoutID = buttonTwo;
				        ledIsOn = false;
				        WidgetUpdateService2 = true;
				        Toast.makeText(context, "Offline", Toast.LENGTH_SHORT).show();
			     }
		          else {
		               layoutID = buttonOne;
		               ledIsOn = true;
		               WidgetUpdateService2 = true;
		               Toast.makeText(context, "Online", Toast.LENGTH_SHORT).show();
		         }
				RemoteViews view = new RemoteViews(context.getPackageName(), layoutID);
			
				int[] id = appWidgetManager.getAppWidgetIds(new ComponentName(context, ButtonWidget.class));
				appWidgetManager.updateAppWidget(id[0], view); 
				
				//Toast.makeText(context, "TREEE", Toast.LENGTH_SHORT).show();
				
				String msg = "null";
				try {
					msg = intent.getStringExtra("msg");
				} catch (NullPointerException e) {
					Log.e("Error", "msg = null");
				}
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				//here
				
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
				NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification noty = new Notification(R.drawable.icon, "Button clicked", System.currentTimeMillis());
				
				noty.setLatestEventInfo(context, "Notice", msg, contentIntent);
				notificationManager.notify(1, noty);
				
				this.onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ButtonWidget.class)));
				
			} else {
				
				if(intent.getAction().equals("org.ubicollab.nomad")){
				
					AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
					RemoteViews view = new RemoteViews(context.getPackageName(),layoutID);
					
					SpaceManager spaceManager = SpaceManager.getInstance(context);
					Space space = spaceManager.getCurrentSpace();
					view.setTextViewText(R.id.list_item_text,"LOCATION:\n" + space.getName());
		
					int[] id = appWidgetManager.getAppWidgetIds(new ComponentName(context, ButtonWidget.class));

					appWidgetManager.updateAppWidget(id[0], view); 
					
					Toast.makeText(context, "Space set", Toast.LENGTH_SHORT).show();
					this.onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ButtonWidget.class)));
					
					WidgetUpdateService2 = true;
				}
			}
			super.onReceive(context, intent);
			}
		}
/*
	public static Object WidgetUpdateService2() {
		// TODO Auto-generated method stub
		return true;
	}
	*/
	
	public boolean setWidgetUpdateService2(boolean WidgetUpdateService2){
		return this.WidgetUpdateService2;
	}
	
	public boolean getWidgetUpdateService2(boolean WidgetUpdateService2){
		return WidgetUpdateService2;
	}

	public static Object getWidgetUpdateService2() {
		// TODO Auto-generated method stub
		return null;
	}
	}	
