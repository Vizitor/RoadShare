package es.upm.etsiinf.dam.roadshare;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import br.com.thinkti.android.filechooser.FileChooser;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;

public class MainActivity extends Activity implements RoutingListener {

	protected GoogleMap mMap;
	protected LatLng start;
	protected LatLng end;
	boolean onRoutingSuccess=false;
	Activity MyActivity=this;
	EditText startText;
	EditText endText;
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

	private void setDirection(String startP, String endP) {
		Geocoder gc = new Geocoder(this);
		List<Address> listS, listE;
		try {
			Routing routing = new Routing(Routing.TravelMode.WALKING);
			routing.registerListener(this);
			listS = gc.getFromLocationName(startP, 1);
			listE = gc.getFromLocationName(endP, 1);
			Address Staddress = listS.get(0);
			Address Enaddress = listE.get(0);
			start = new LatLng(Staddress.getLatitude(), Staddress.getLongitude());
			end = new LatLng(Enaddress.getLatitude(), Enaddress.getLongitude());
			routing.execute(start, end);
		} catch (IOException e) {
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
			e.printStackTrace();
		}		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();
		startText = (EditText) findViewById(R.id.startingPoint);
		endText = (EditText) findViewById(R.id.endingPoint);
		startText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE && !startText.getText().toString().isEmpty() ) {
					if(endText.getText().toString().isEmpty()) {
						setLocation(startText.getText());
					} else {
					setDirection(startText.getText().toString(), endText.getText().toString());
					}

					handled = true;
				}
				return handled;
			}
		});
		endText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE && !endText.getText().toString().isEmpty() ) {
					if(startText.getText().toString().isEmpty()) {
						setLocation(endText.getText());
					} else {
					setDirection(startText.getText().toString(), endText.getText().toString());
					}
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
		if (id == R.id.save) {
			if(onRoutingSuccess){
				final EditText title = new EditText(this);
				title.setHint("Insert title");//optional
				LinearLayout lay = new LinearLayout(this);
				lay.setOrientation(LinearLayout.VERTICAL);
				lay.addView(title);
				final EditText input = new EditText(MainActivity.this);  
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				input.setLayoutParams(lp);
				input.setInputType(InputType.TYPE_CLASS_TEXT);
				input.setHint("Enter a name for the road");
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setPositiveButton("Save", null)
				.setNegativeButton("Cancel", null)
				.setView(input);
				final AlertDialog dialog = builder.create();
				dialog.show();
				dialog.getButton(AlertDialog.BUTTON_POSITIVE)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (input.getText()==null){
							input.setError("Insert title please");
						}
						else if (input.getText().length()==0){
							input.setError("Insert title please");
						}
						else if (input.getText().equals("")){
							input.setError("Insert title please");
						}
						else {
							String filename = input.getText().toString()+".rds";
							File file = new File(MyActivity.getFilesDir(),filename );
							FileOutputStream outputStream;
							try {
								outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
								outputStream.write(startText.getText().toString().getBytes());
								String newLine="\n";
								outputStream.write(newLine.getBytes());
								outputStream.write(endText.getText().toString().getBytes());
								outputStream.close();		 
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						dialog.dismiss();
					}
				});
			}
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("Please, create or load a route.")
				.setNegativeButton("Cancel", null);
				final AlertDialog dialog = builder.create();
				dialog.show();
			}
		}

		if (id == R.id.load) {
			Intent intent = new Intent(this, FileChooser.class);
			ArrayList<String> extensions = new ArrayList<String>();
			extensions.add(".rds");
			intent.putStringArrayListExtra("filterFileExtension", extensions);
			startActivityForResult(intent, 1);	
		}
		if (id == R.id.delete) {
			Intent intent = new Intent(this, FileChooser.class);
			ArrayList<String> extensions = new ArrayList<String>();
			extensions.add(".rds");
			intent.putStringArrayListExtra("filterFileExtension", extensions);
			startActivityForResult(intent, 2);
		}
		if (id == R.id.share) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == 1) && (resultCode == -1)) {
			String fileSelected = data.getStringExtra("fileSelected");
			try {
				FileInputStream inputStream = new FileInputStream (new File(fileSelected));
				if ( inputStream != null ) 
				{
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					String start2 = "";
					String end2 = "";
					start2 = bufferedReader.readLine();
					end2= bufferedReader.readLine();
					inputStream.close();
					startText.setText(start2);
					endText.setText(end2);
					setDirection(start2, end2);
				}
			} catch (IOException e ) {
				e.printStackTrace();
			} 
		}       
		if ((requestCode == 2) && (resultCode == -1)) { 
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setPositiveButton("Yes", null)
			.setNegativeButton("No", null)
			.setTitle("Deleting file. Are you sure?");
			final AlertDialog dialog = builder.create();
			final Intent data1=data;
			dialog.show();
			dialog.getButton(AlertDialog.BUTTON_POSITIVE)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String fileSelected = data1.getStringExtra("fileSelected");
					File file=new File(fileSelected);
					file.delete();
					dialog.dismiss();
				}
			});
		} 
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
		PolylineOptions polyoptions = new PolylineOptions();
		polyoptions.color(Color.BLUE);
		polyoptions.width(10);
		polyoptions.addAll(mPolyOptions.getPoints());
		mMap.clear();
		mMap.addPolyline(polyoptions);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start,15));
		onRoutingSuccess=true;

	}
}
