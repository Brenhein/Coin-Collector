package edu.msu.defenso2.project3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;

import edu.msu.defenso2.project3.Cloud.Cloud;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    /**
     * Represents a sing coin
     */
    private class Coin {
        public double latitude;
        public double longitude;
        public int amount;
        public boolean active = false;
        public Marker marker;

        public Coin(double latitude, double longitude, int amount) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.amount = amount;
        }
    }

    /**
     * Constants
     */
    private ArrayList<Coin> COINS = new ArrayList<Coin>();
    private final int TIME_TO_CHECK_LOC = 5000;
    private final int DIST_TO_CHECK_LOC = 5;

    /**
     * Member Variables
     */
    private int score = 0; ///< The score of the player
    private final String apiKey = ""; ///< The API key to access google maps
    private GoogleMap gMap; ///< The google maps object
    private MapView mapView; ///< The map view
    private GoogleMap mMap;
    private String user;
    private LocationManager locationManager;
    private Location location;
    private Marker clientMarker;
    private ArrayList<LatLng> markers;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                Log.d("MapsActivity: onCreate", "Location permission not granted");
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_TO_CHECK_LOC, DIST_TO_CHECK_LOC, this);

        // Gets the username
        final View gameView = findViewById(R.id.gameView);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("USER");
        user = username;

        // Loads the user data into the game
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final int coins = cloud.loadFromCloud(username);
                if (coins == -1) {
                    gameView.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(gameView.getContext(), "failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else { // The connection worked!
                    score = coins;
                    gameView.post(new Runnable() {
                        @Override
                        public void run() {
                            // Adds the coin count to the screen
                            TextView scoreView = findViewById(R.id.currentScore);
                            scoreView.append(" " + Integer.toString(coins));
                        }
                    });

                }
            }
        }).start();

        // Adds the user to the screen
        TextView user = findViewById(R.id.username);
        user.append(" " + username);

        initCoins(); // Adds the 30 coins to the game
    }

    /**
     * Update current location
     *
     * @param location current location
     */
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        final LatLng latLng = new LatLng(this.location.getLatitude(), this.location.getLongitude());
        if (clientMarker == null) {
            // Initialize marker
            clientMarker = mMap.addMarker(new MarkerOptions().icon(getMarkerIcon("#18453b")).title("You").position(latLng));
        } else {
            clientMarker.setPosition(latLng);
        }
        final double lat = location.getLatitude();
        final double lon = location.getLongitude();

        checkIfClose(lat,lon);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                /*Cloud cloud = new Cloud();
//                if (cloud.isNearCoin(latLng)) {
//                    final View gameView = findViewById(R.id.gameView);
//                    gameView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(gameView.getContext(), "Retrieved coin!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }*/
//                checkIfClose(lat,lon);
//            }
//        }).start();


        Log.d("onLocationChanged", "Latitude:" + this.location.getLatitude() + ", Longitude:" + this.location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("onStatusChanged", provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("onProviderDisabled", provider + "disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("onProviderEnabled", provider + "enabled");
    }

    /**
     * Gets a color for GMaps marker
     *
     * @param color String with hex value of color
     * @return
     */
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    /**
     * Called when the user collects a coin.  Creates a new thread to process it
     *
     * @param amount The amount they collected
     */
    public void coinCollected(final int amount) {
        final View gameView = findViewById(R.id.gameView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.addCoins(user, amount);
                if (!ok) {
                    gameView.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(gameView.getContext(), "failed to add coins to user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Initializes the 30 coins
     */
    public void initCoins() {
        COINS.add(new Coin(42.72669 , -84.48392 , 50));
        COINS.add(new Coin(42.72636 , -84.46785 , 1));
        COINS.add(new Coin(42.72413 , -84.49207 , 1));
        COINS.add(new Coin(42.72130 , -84.48454 , 5));
        COINS.add(new Coin(42.72725 , -84.48047 , 50));
        COINS.add(new Coin(42.73211 , -84.47467 , 20));
        COINS.add(new Coin(42.72270 , -84.46364 , 5));
        COINS.add(new Coin(42.72498 , -84.46681 , 1));
        COINS.add(new Coin(42.73178 , -84.47920 , 10));
        COINS.add(new Coin(42.72172 , -84.47914 , 10));
        COINS.add(new Coin(42.72761 , -84.47198 , 5));
        COINS.add(new Coin(42.72773 , -84.47182 , 1));
        COINS.add(new Coin(42.72497 , -84.47831 , 5));
        COINS.add(new Coin(42.72725 , -84.46683 , 5));
        COINS.add(new Coin(42.72266 , -84.49238 , 20));
        COINS.add(new Coin(42.72857 , -84.48801 , 1));
        COINS.add(new Coin(42.73274 , -84.48733 , 50));
        COINS.add(new Coin(42.73068 , -84.48796 , 20));
        COINS.add(new Coin(42.72653 , -84.47759 , 5));
        COINS.add(new Coin(42.72729 , -84.48506 , 1));
        COINS.add(new Coin(42.73150 , -84.49355 , 20));
        COINS.add(new Coin(42.72412 , -84.48167 , 20));
        COINS.add(new Coin(42.72850 , -84.46975 , 50));
        COINS.add(new Coin(42.73238 , -84.47844 , 5));
        COINS.add(new Coin(42.72262 , -84.47226 , 1));
        COINS.add(new Coin(42.73081 , -84.48741 , 10));
        COINS.add(new Coin(42.73218 , -84.48129 , 10));
        COINS.add(new Coin(42.72318 , -84.48303 , 50));
        COINS.add(new Coin(42.72528 , -84.49105 , 1));
        COINS.add(new Coin(42.72482 , -84.48999 , 1));
    }


    /**
     * Takes the user back to the home screen
     *
     * @param view
     */
    public void onEndGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(42.726133,
                        -84.477815));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        Random r = new Random();
        int i1 = r.nextInt(30);
        Coin c1 = COINS.get(i1);

        int i2 = r.nextInt(30);
        while (i2 == i1){
            i2 = r.nextInt(30);
        }
        Coin c2 = COINS.get(i2);

        int i3 = r.nextInt(30);
        while (i3 == i1 || i3 == i2){
            i3 = r.nextInt(30);
        }
        Coin c3 = COINS.get(i3);

        int i4 = r.nextInt(30);
        while (i4 == i1 || i4 == i2 || i4 == i3){
            i4 = r.nextInt(30);
        }
        Coin c4 = COINS.get(i4);

        int i5 = r.nextInt(30);
        while (i5 == i1 || i5 == i2 || i5 == i3 || i5 == i4){
            i5 = r.nextInt(30);
        }
        Coin c5 = COINS.get(i5);

        placeMarker(c1,"$"+Integer.toString(c1.amount), c1.latitude, c1.longitude);
        placeMarker(c2,"$"+Integer.toString(c2.amount), c2.latitude, c2.longitude);
        placeMarker(c3,"$"+Integer.toString(c3.amount), c3.latitude, c3.longitude);
        placeMarker(c4,"$"+Integer.toString(c4.amount), c4.latitude, c4.longitude);
        placeMarker(c5,"$"+Integer.toString(c5.amount), c5.latitude, c5.longitude);

        c1.active = true;
        c2.active = true;
        c3.active = true;
        c4.active = true;
        c5.active = true;

        COINS.set(i1,c1);
        COINS.set(i2,c2);
        COINS.set(i3,c3);
        COINS.set(i4,c4);
        COINS.set(i5,c5);
    }

    /**
     * Places a marker from the database to the map
     *
     * @param amount The coin amount
     * @param lat    the latitude location
     * @param lon    The longitude location
     */
    public void placeMarker(Coin c, String amount, double lat, double lon) {
        if (mMap != null) {
            LatLng marker = new LatLng(lat, lon);
            c.marker = mMap.addMarker(new MarkerOptions().title(amount).position(marker));
        }
    }

    public void checkIfClose(double lat, double lon){
        for (Coin c: COINS){
            double x = Math.sqrt(Math.pow(lat-c.latitude,2)+Math.pow(lon-c.longitude,2)*1.0);
            if (c.active && x < 0.0005){
                // Finds a new coin
                Random r = new Random();
                int i1 = r.nextInt(30);
                Coin c1 = COINS.get(i1);
                while (c1.active || // Goes until finds one that isn't active and is outside our check bounds
                        Math.sqrt(Math.pow(lat-c1.latitude,2)+Math.pow(lon-c1.longitude,2)*1.0) < .0005) {
                    i1 = r.nextInt(30);
                    c1 = COINS.get(i1);
                }

                placeMarker(c1,"$"+Integer.toString(c1.amount),c1.latitude,c1.longitude);
                Marker m = c.marker;
                m.remove();

                int i = COINS.indexOf(c);
                c.active = false;
                COINS.set(i,c);

                c1.active = true;
                COINS.set(i1,c1);

                // update the user's coin count
                score += c.amount;
                TextView scoreView = findViewById(R.id.currentScore);
                scoreView.setText("Current Score: " + Integer.toString(score));
                coinCollected(c.amount);
            }
        }
    }
}
