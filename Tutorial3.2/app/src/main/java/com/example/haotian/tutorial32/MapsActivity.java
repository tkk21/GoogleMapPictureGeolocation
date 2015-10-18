package com.example.haotian.tutorial32;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.GpsSatellite;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Connection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class MapsActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    /**constants */
    public static final String TAG = "MapsActivity";
    public static final int THUMBNAIL = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String REQUESTING_LOCATION_UPDATES = "REQUESTING_LOCATION_UPDATES";
    private static final String LOCATION_KEY = "LOCATION_KEY";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_UPDATED_TIME_STRING_KEY";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button picButton; //takes user to camera

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private String mLastUpdatTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        picButton = (Button) findViewById(R.id.photobutton);
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mRequestingLocationUpdates = true;
        createLocationRequest();
        //instantiates the google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        updateValuesFromBundle(savedInstanceState);
    }

    @Override
    protected void onStart (){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop (){
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * callback to save the state of the activity into a bundle
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState (Bundle savedInstanceState){
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdatTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle (Bundle savedInstanceState){
        if (savedInstanceState == null) {
            return;
        }
        //Update the value of mRequestingLocationUpdates from the Bundle
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES);

        }
        //Update the value of mCurrentLocation from the Bundle
        if (savedInstanceState.keySet().contains(LOCATION_KEY)){
            mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
        }
        //Update the value of mLastUpdateTime from the Bundle
        if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)){
            mLastUpdatTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
        }

    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(20, 20)).title("EECS397/600"));
    }

    private void addMarker (Bitmap bitmap){
        mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(
                                mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                bitmap)));
                                //((BitmapDrawable)mImageView.getDrawable()).getBitmap())));
    }

    /**
     * helper method to send intent to take picture
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Get the Thumbnail of the photo
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            addMarker(imageBitmap);
        }

    }

    //Location service stuff

    /**
     * Set up a Location Request
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //10s
        mLocationRequest.setFastestInterval(5000); //5s
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Request Location Updates
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest
                , this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient
                , this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Called when the location has changed.
     * <p/>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdatTime = DateFormat.getTimeInstance().format((new Date()));
        Log.d("MapsActivity", String.format("Location is: %f, %f", location.getLatitude(), location.getLongitude()));
        //think this is all that is needed
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.wtf("MapsActivity", "Connection Failed");
    }
}
