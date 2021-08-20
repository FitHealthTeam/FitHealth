package com.fithealthteam.fithealth.huawei.motionTracking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.fithealthteam.fithealth.huawei.R;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.LocationSource;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.PolylineOptions;

import java.text.DecimalFormat;

public class MotionTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private HuaweiMap hwMap;
    private Marker mMarkerStart, mMarkerEnd;
    private LocationRequest locationRequest;
    private TextView mTvStart, mTvSpeed, mTvDistance;
    private Chronometer mTime;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PathBean path = new PathBean();
    private long seconds = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private PolylineOptions polylineOptions;
    private boolean mIsRunning = false;
    private LocationSource.OnLocationChangedListener mListener;
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            mTime.setText(formatSeconds());
            handler.postDelayed(this, 1000);
        }
    };
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = "MotionTrackingActivity";
    private LocationCallback mLocationCallback;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_tracking);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // check the location settings
        checkLocationSettings();

        // initialize MapView
        mapView = findViewById(R.id.Huawei_MapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        //show the measured movement to users
        mTvSpeed = findViewById(R.id.movementSpeed);
        mTvDistance = findViewById(R.id.movedDistance);
        mTime = findViewById(R.id.elapsedTime);
        mTvStart = findViewById(R.id.motionTrackingStart);
        mTvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processStartClick();
            }
        });


        //initialize polyline for drawing path in map view
        polylineOptions = new PolylineOptions();
        polylineOptions.color(getResources().getColor(R.color.hwid_auth_button_color_blue));
        polylineOptions.width(5f);
    }

    private void processStartClick() {
        if (mIsRunning) {
            mIsRunning = false;
            path.setEndTime(System.currentTimeMillis());
            mTvStart.setText("Start");
            handler.removeCallbacks(timeRunnable);

            if (path.getPathLine().size() > 0) {
                path.setEndPoint(path.getPathLine().get(path.getPathLine().size() - 1));
                if (null != mMarkerStart && null != mMarkerEnd) {
                    mMarkerStart.remove();
                    mMarkerEnd.remove();
                }
                /*MarkerOptions StartPointOptions = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                        .position(path.getStartPoint());
                StartPointOptions.title("Start Point");
                StartPointOptions.snippet("Start Point");
                mMarkerStart = hwMap.addMarker(StartPointOptions);
                MarkerOptions EndPointOptions = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.main_app_logo_round))
                        .position(path.getEndPoint());
                EndPointOptions.title("End Point");
                EndPointOptions.snippet("End Point");
                mMarkerEnd = hwMap.addMarker(EndPointOptions);*/
            }
        } else {
            mIsRunning = true;
            path.reset();
            path.setStartTime(System.currentTimeMillis());
            handler.post(timeRunnable);
            mTvStart.setText("Stop");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        removeLocationUpdatesWithCallback();
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        mapView.onDestroy();
    }

    /**
     * Removed when the location update is no longer required.
     */
    private void removeLocationUpdatesWithCallback() {
        try {

            Task<Void> voidTask = fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "removeLocationUpdates Success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "removeLocationUpdates Failure: " + e);
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        requestLocationUpdate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(MotionTrackingActivity.this, 0);
                        } catch (IntentSender.SendIntentException sie) {

                        }
                        break;
                }
            }
        });
    }

    private void requestLocationUpdate() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (mIsRunning) {
                    processLocationChange(locationResult.getLastLocation());
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
        fusedLocationProviderClient
                .requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully request location update");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed request location update, error: " + e.getMessage());
                    }
                });
    }

    private void processLocationChange(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (path.getStartPoint() == null) {
            path.setStartPoint(latLng);
        }

        path.addPoint(latLng);
        float distance = path.updateDistance();
        double sportMile = distance / 1000d;

        if (seconds > 0) {
            double distribution = (double) seconds / 60d / sportMile;
            path.setDistribution(distribution);
            mTvSpeed.setText(decimalFormat.format(distribution));
            mTvDistance.setText(decimalFormat.format(sportMile));
        } else {
            path.setDistribution(0d);
            mTvSpeed.setText("0.00");
            mTvDistance.setText("0.00");
        }

        //draw the path on the map view
        polylineOptions.add(latLng);
        hwMap.addPolyline(polylineOptions);

        //update map camera
        if (mListener != null) {
            mListener.onLocationChanged(location);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15f);
            hwMap.animateCamera(cameraUpdate);
        }
    }


    public String formatSeconds() {
        String hh = seconds / 3600 > 9 ? seconds / 3600 + "" : "0" + seconds
                / 3600;
        String mm = (seconds % 3600) / 60 > 9 ? (seconds % 3600) / 60 + ""
                : "0" + (seconds % 3600) / 60;

        seconds++;
        return hh + ":" + mm;
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hwMap = huaweiMap;
        hwMap.setMyLocationEnabled(true);
        hwMap.getUiSettings().setZoomControlsEnabled(false);

        //add location source
        hwMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mListener = onLocationChangedListener;
            }

            @Override
            public void deactivate() {

            }
        });

        //get current location and update the map camera
        try {
            checkLocationSettings();
            Task<Location> lastLocation = fusedLocationProviderClient.getLastLocation();
            lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mListener.onLocationChanged(location);
                    if (mListener != null) {
                        mListener.onLocationChanged(location);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                new LatLng(location.getLatitude(), location.getLongitude()), 15f);
                        hwMap.animateCamera(cameraUpdate);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                }
            });
        } catch (Exception e) {

        }
    }

}