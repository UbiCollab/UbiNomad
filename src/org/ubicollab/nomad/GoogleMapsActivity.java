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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class GoogleMapsActivity extends MapActivity
{
//	private LocationManager lm;
//	private LocationListener locationListener;
//	GeoPoint p;
//	MapView mapView; 

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);


        
//		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		locationListener = new MyLocationListener();
//
//		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
//				locationListener);
        
        String uri = "geo:"+ "10.256" + "," + "63.100";
        this.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

//        // You can also choose to place a point like so:
//        String uri = "geo:"+ latitude + "," + longitude + "?q=my+street+address";
//        startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

   

	}

//	private class MyLocationListener implements LocationListener {
//
//		@Override
//		public void onLocationChanged(Location loc) {
//			Toast.makeText(
//					getBaseContext(),
//					"lattitude: " + loc.getLatitude() + "\nlongitude: "
//							+ loc.getLongitude(), Toast.LENGTH_SHORT).show();
//
//			mapView = (MapView) findViewById(R.id.mapview);
//			MapController mc = mapView.getController();
//	 
//	        p = new GeoPoint(
//	            (int) (loc.getLatitude() * 1E6), 
//	            (int) (loc.getLongitude() * 1E6));
//	 
//	        mc.animateTo(p);
//	        mc.setZoom(2); 
//	        mapView.invalidate();
//		}
//
//		@Override
//		public void onProviderDisabled(String provider) {
//			// TODO Auto-generated method stub
//		}
//
//		@Override
//		public void onProviderEnabled(String provider) {
//			// TODO Auto-generated method stub
//		}
//
//		@Override
//		public void onStatusChanged(String provider, int status, Bundle extras) {
//			// TODO Auto-generated method stub
//		}
//		
//	}
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
