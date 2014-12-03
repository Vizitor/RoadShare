package es.upm.etsiinf.dam.roadshare;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;

public class MainActivity extends Activity {

	private GoogleMap mMap;
	private static final String TAG = "RoadShare";


	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.

			}
		}
	}

	private void setLocation(Editable location) {
		LatLng latlng;
		Geocoder gc = new Geocoder(this);
		List<Address> list;
		Log.d(TAG, "Setlocation  = "+location.toString());
		try {
			list = gc.getFromLocationName(location.toString(), 1);
			Address address = list.get(0);
			latlng = new LatLng(address.getLatitude(), address.getLongitude());
			mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
			mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();
		final EditText startText = (EditText) findViewById(R.id.startingPoint);
		startText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					setLocation(startText.getText());
					handled = true;
				}
				return handled;
			}
		});
		//startText.setOnEditorActionListener(l);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
