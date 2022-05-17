package com.example.mylocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mylocation.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //  int LOCATION_REQUEST_CODE = 1001;
    int REQUEST_CODE = 121;
    List<Address> list ;

    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private ActivityMainBinding binding;


    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding.btnLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               getLastLocation();
              //  Toast.makeText(getBaseContext(), "Your answer is correct!", Toast.LENGTH_SHORT).show();
            }

        });


    }


    private void getLastLocation() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {


                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();

                        if (location !=null) {

                            Geocoder geocoder =new Geocoder(getBaseContext(),Locale.getDefault());

                            try {
                              list= geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }



                            binding.tvLatitude.setText(location.getLatitude() + "");
                            binding.tvLongitude.setText(location.getLongitude()+"");
                           binding.tvCountryName.setText(list.get(0).getCountryName());
                           binding.tvLocality.setText(list.get(0).getLocality() );
                           binding.tvStateName.setText(list.get(0).getAdminArea());
                           binding.tvAddress2.setText(list.get(0).getAddressLine(0));

                        } else {
                            requestNewLocationData();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }


        } else {
            requestPermissions();
        }
    }

    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,null, Looper.myLooper());
    }
//    private LocationCallback mLocationCallback = new LocationCallback() {
//
//        @SuppressLint("SetTextI18n")
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            Location mLastLocation = locationResult.getLastLocation();binding.btnLocation.setText("Longitude: " + mLastLocation);
//            binding.tvLatitude.setText("Latitude: " + mLastLocation.getLatitude() + "");
//
//        }
//    };



        private Boolean checkPermissions(){
    return   ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//
        //fusedLocationProviderClient.getLastLocation().addOnCompleteListener({ta

  }


         private void  requestPermissions(){
      //  ActivityCompat.requestPermissions(this,PERMISSIONS,REQUEST_CODE);

             ActivityCompat.requestPermissions(this, new String[]{
                     Manifest.permission.ACCESS_COARSE_LOCATION,
                     Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE );
    }


private boolean isLocationEnabled(){



    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
}





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode ==REQUEST_CODE){
            if (grantResults.length >0 && grantResults[0]==PackageManager.PERMISSION_DENIED){
                //permission granted

               getLastLocation();
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}
