package org.ubicollab.nomad.deprecated;

import org.ubicollab.nomad.SpaceManager;
import org.ubicollab.nomad.discover.DiscoverGroup;
import org.ubicollab.nomad.space.Space;
import org.ubicollab.ulm.R;
import org.ubicollab.ulm.R.layout;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SetParentSpaceList extends ListActivity{
	

	static Space parentSpace; 
	 //Intent intent = new Intent(SetParentSpaceList.this, CreateSpace.class);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
			SpaceManager sm = SpaceManager.getInstance(getApplicationContext());
		  setListAdapter(new ArrayAdapter<Space>(this, R.layout.list_item, sm.getAllSpaces()));

		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);

		  lv.setOnItemClickListener(new OnItemClickListener() {
		    @Override
			public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      // When clicked, show a toast with the TextView text
		      Toast.makeText(getApplicationContext(), ((TextView) view).getText() + " set as parent space.",
		          Toast.LENGTH_SHORT).show();
		      //How send parent to createspace????
		      parentSpace = (Space)parent.getItemAtPosition(position);
		      DiscoverGroup.group.back();
		     
		      //intent.putExtra("parentSpaceName", parentSpace.getName());
		      
		    }
		  });
		}
}
