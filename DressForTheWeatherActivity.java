package act.dressfortheweather;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


//ADDED GPS STUFF
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class DressForTheWeatherActivity extends Activity {
	
	public final String TAG = "DressForTheWeatherActivity";
	private ImageButton suggestions;
	private ImageButton hourly;
	private ImageButton help;
	private ImageButton wardrobe;
	private ImageButton camera;
	
	// weather
	private double temperature;
	private String condition;
	// "k" icon set.  Example: http://icons.wxug.com/i/c/k/clear.gif
	private String iconSet = "k";	

	//GPS stuff
//	private MyLocation myLocation = new MyLocation();
	public double latitude;
	public double longitude;

	// Menu stuff
	static final int ENTER_LOCATION_ID = 0;
	private int zip;						// from user input in menu.
	private int year;
	private int month;
	private int day;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//     GPS STUFF
		LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();

		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		mlocManager.getLastKnownLocation(LOCATION_SERVICE); 
		
		String latitude2 = latitude + "";
		String longitude2 = longitude + "";
		Log.v("GPS Latitude", latitude2);
		Log.v("GPS Longitude", longitude2);


		//END GPS STUFF
		
		/** Weather stuff **/
		// call current weather; output information in textview at the top during testing.
		zip = 78705;
		
		// by default, the year, month, and day will be set to today's date.  
		// And the current weather method will be called.
		getCurrentWeather(getJSON());

		suggestions = (ImageButton)findViewById(R.id.suggestions_button);
		wardrobe = (ImageButton) findViewById(R.id.wardrobe_button);
		help = (ImageButton) findViewById(R.id.help_button);
		hourly = (ImageButton) findViewById(R.id.weather_button);
		camera = (ImageButton) findViewById(R.id.camera);
		
		// settings the initial values for the date - pass into the SettingsDate class?
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		displayDate();
	}


	public void openWardrobe(View view){
		Log.d(TAG, "in openWardrobe going to start WardrobeActivity");
		Intent wardrobeIntent = new Intent( DressForTheWeatherActivity.this, WardrobeTabWidget.class);
		startActivity(wardrobeIntent);
	}

	public void openHelp(View view){
		Log.d(TAG, "in openHelp");
		Intent helpIntent = new Intent( DressForTheWeatherActivity.this, Help.class);
		startActivity(helpIntent);
	}

	public void openSuggestions(View view){		// pass in temperature + conditions.
		Log.d(TAG, "in openSuggestions");
		Intent suggestionsIntent = new Intent( DressForTheWeatherActivity.this, Suggestions.class);
		suggestionsIntent.putExtra("temperature", temperature);
		suggestionsIntent.putExtra("condition", condition);
		startActivity(suggestionsIntent);
	}

	public void openSettings(View view){
		Log.d(TAG, "in openSettings");
		Intent settingsIntent = new Intent( DressForTheWeatherActivity.this, Settings.class);
		startActivity(settingsIntent);
	}

	public void openHourlyWeather(View view){
		Log.d(TAG, "in openHourlyWeather");
		Intent hourlyWeatherIntent = new Intent(DressForTheWeatherActivity.this, HourlyWeather.class);
		hourlyWeatherIntent.putExtra("zip", zip);
		hourlyWeatherIntent.putExtra("year", year);
		hourlyWeatherIntent.putExtra("month", month);
		hourlyWeatherIntent.putExtra("day", day);
		startActivity(hourlyWeatherIntent);
	}

	public void openAddToWardrobe(View view){
		Log.d(TAG, "in openAddToWardrobe");
		Intent intent = new Intent( DressForTheWeatherActivity.this, AddToWardrobe.class);
		startActivity(intent);
	}

	public void openCamera(View view){
		Log.d(TAG, "in openCamera");
		Intent cameraIntent = new Intent(DressForTheWeatherActivity.this, CameraActivity.class);
		startActivity(cameraIntent);
	} 
	
	public void openSettingsDate(View view){
		Log.d(TAG, "in openSettingsDate");
		Intent settingsDateIntent = new Intent(DressForTheWeatherActivity.this, SettingsDate.class);
		// pack the date (default values are today's date).
		settingsDateIntent.putExtra("year", year);
		settingsDateIntent.putExtra("month", month);
		settingsDateIntent.putExtra("day", day);
		startActivityForResult(settingsDateIntent, 0);
		Log.d(TAG, "came back from SettingsDate");
	} 
	
	@Override
	public void onBackPressed(){
		Log.d(TAG, "Exiting app");
		// 
	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_HOME);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	}
	
	// coming back from SettingsDate with the date.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();
		year = extras.getInt("year");
		month = extras.getInt("month");
		day = extras.getInt("day");
		displayDate();
	}


	//GPS STUFF
	/* Class My Location Listener */
		public class MyLocationListener implements LocationListener{
		//        @Override
		public void onLocationChanged(Location loc){
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();
		}

		//        @Override
		public void onProviderDisabled(String provider){
		}

		//        @Override
		public void onProviderEnabled(String provider){
		}

		//        @Override
		public void onStatusChanged(String provider, int status, Bundle extras){
		}

	}/* End of Class MyLocationListener */

	// MENU STUFF
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		// loads the options menu.
		// standard code from Tutorial 3.
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		Log.d(TAG, "inflating the menu.");
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.Settings:
			Log.d(TAG, "opening Settings page.");
			// go to the settings page.
			openSettingsDate(this.getCurrentFocus());
			Log.d(TAG, "you can unpack the date now");
//			startActivityForResult(new Intent(this, Settings.class), 0);		// ****** changed to SettingsDate
			return true;
		case R.id.EnterLocation:
			Log.d(TAG, "getting user's zip code.");
			// brings up an EditText box so that the user can input a zip code.
			showInputAlert();
			return true;
		}
		return false;
	}

	// for the menu.
	public void showInputAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Preferred location");
		alert.setMessage("Enter your zip code here:");

		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String zipStr = input.getText().toString();		// gets the user-inputted string.
				zip = Integer.valueOf(zipStr);
				Log.d(TAG, "zip is " + zip);					// it works!  :D
				
				// get the weather again.
				getCurrentWeather(getJSON());
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// cancels.
			}
		});
		alert.show();
	}
	
	// displays the string.
	public void displayDate() {
		TextView dateText = (TextView) findViewById(R.id.date);
		dateText.setText("Date: " + monthString(month) + " " + day + ", " + year);
	}
	
	// mapping from 0-11 to month.
	public String monthString(int month) {
		String[] months = { "January", "February", "March",
							"April", "May", "June",
							"July", "August", "September",
							"October", "November", "December" };
		
		return months[month];
	}


	// Getting weather stuff
	
	// this method opens up the URL and returns the JSON information after getting it from the JSON file.
	public JSONObject getJSON() {
		Log.d(TAG, "in getJSON");
//		// usual zipcode for testing.
//		zip = 78705;
		
		// hardcoded the URL.
		String queryString = "http://api.wunderground.com/api/ce3ba3c110787728/geolookup/conditions/q/" + zip + ".json";
		InputStream inputstream = null;
		JSONObject obj = null;
		String json = "";
		
		try {
			// gets an input stream so that we can read the JSON.
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(queryString);
			
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			inputstream = entity.getContent();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// now we have an input stream.  Convert to a string of JSON.
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, "iso-8859-1"), 8);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			inputstream.close();
			json = builder.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// now try to parse the JSON string.
		try {
			obj = new JSONObject(json);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d(TAG, "done with getJSON(); going to getCurrentWeather()");
		return obj;
	}
	
	// this method gets the current weather condition (clear, rainy, etc), current temperature in F and C, 
	// picture/icon, and wind speed.  
	public void getCurrentWeather(JSONObject j) {
		// usual zipcode for testing.
//		zip = 78705;
		
		// hardcoded the URL.
//		String queryString = "http://api.wunderground.com/api/ce3ba3c110787728/geolookup/conditions/q/" + zip + ".json";
		
		
		try {
			Log.d(TAG, "in getCurrentWeather()");
			
			// the current_observation object.
			JSONObject current_observation = j.getJSONObject("current_observation");
			JSONObject display_location = current_observation.getJSONObject("display_location");
			
			// current condition (e.g. clear, rainy).
			condition = current_observation.getString("weather");
			
			// current temperature: example - "68.9 F (20.5 C)"
			// getString("temp_f") for F, getString("temp_c") for C.
			temperature = current_observation.getDouble("temp_f");	
			
//			String city = current_observation.getString("city");
			
			// current wind string, direction, speed.
			String wind_speed = current_observation.getString("wind_string");
			String wind_direction = current_observation.getString("wind_dir");
			double wind_mph = current_observation.getDouble("wind_mph");
			
			// icon for the current weather condition.
			String icon_url = current_observation.getString("icon_url");
//			String icon = current_observation.getString("icon");
			
			String location = display_location.getString("full");	
			
			Log.d(TAG, "condition: " + condition);
			Log.d(TAG, "temperature: " + temperature);
			Log.d(TAG, "wind speed: " + wind_speed);
			Log.d(TAG, "wind_direction: " + wind_direction);
			Log.d(TAG, "wind mph: " + wind_mph);
			Log.d(TAG, "location: " + location);
			
			TextView tv1 = (TextView) findViewById(R.id.TextView01);
			tv1.setText("Temperature: " + temperature + "\nCondition: " + condition + "\nLocation: " + location);	
			ImageView iv1 = (ImageView) findViewById(R.id.ImageView01);
			Bitmap b1 = getBitmap(icon_url);
			iv1.setImageBitmap(b1);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Following 2 methods adapted from:
	 * http://stackoverflow.com/questions/3601775/display-image-from-url-sizing-and-screen-orientation-problems
	 */
	
	// gets the picture from a URL.
	public Object getPictureContent(String address) {
		try {
			URL url = new URL(address);
			Object content = url.getContent();
			Log.d(TAG, "returning picture content from Wunderground");
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// reads an InputStream and generates a Drawable from the content.
	public Bitmap getBitmap(String address) {
		try {
			InputStream content = (InputStream) getPictureContent(address);
			Bitmap b = BitmapFactory.decodeStream(content);
			Bitmap b2 = Bitmap.createScaledBitmap(b, 120, 120, false);
			Log.d(TAG, "getting Bitmap from Wunderground");
			return b2;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}







