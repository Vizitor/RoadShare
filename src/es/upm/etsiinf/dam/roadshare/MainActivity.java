package es.upm.etsiinf.dam.roadshare;

import java.io.IOException;
import java.util.List;


import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;

public class MainActivity extends Activity implements RoutingListener {

	protected GoogleMap mMap;
	protected LatLng start;
	protected LatLng end;
	private static final String TAG = "Roadshare";


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

	private void setDirection(Editable startP, Editable endP) {
		Geocoder gc = new Geocoder(this);
		List<Address> listS, listE;
		try {
			Routing routing = new Routing(Routing.TravelMode.WALKING);
			routing.registerListener(this);
			listS = gc.getFromLocationName(startP.toString(), 1);
			listE = gc.getFromLocationName(endP.toString(), 1);
			Address Staddress = listS.get(0);
			Address Enaddress = listE.get(0);
			start = new LatLng(Staddress.getLatitude(), Staddress.getLongitude());
			end = new LatLng(Enaddress.getLatitude(), Enaddress.getLongitude());
			routing.execute(start, end);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setLocation(Editable location) {
		LatLng latlng;
		Geocoder gc = new Geocoder(this);
		List<Address> list;
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
		Log.v(TAG, "This is a log test !");
		setUpMapIfNeeded();
		final EditText startText = (EditText) findViewById(R.id.startingPoint);
		final EditText endText = (EditText) findViewById(R.id.endingPoint);
		startText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					setLocation(startText.getText());
					handled = true;
				}
				return handled;
			}
		});
		endText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					setDirection(startText.getText(), endText.getText());
					handled = true;
				}
				return handled;
			}
		});

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

	@Override
	public void onRoutingFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoutingStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
		Log.v(TAG, "onRoutingSuccess !");
		Log.v(TAG, mPolyOptions.getPoints().toString());
		PolylineOptions polyoptions = new PolylineOptions();
		polyoptions.color(Color.BLUE);
		polyoptions.width(10);
		polyoptions.addAll(mPolyOptions.getPoints());
		mMap.addPolyline(polyoptions);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start,15));

	}
}
