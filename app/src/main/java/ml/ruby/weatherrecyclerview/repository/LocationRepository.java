package ml.ruby.weatherrecyclerview.repository;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import ml.ruby.weatherrecyclerview.model.LocationBean;
import ml.ruby.weatherrecyclerview.utils.ContextSupplier;
import ml.ruby.weatherrecyclerview.utils.NumberOperation;

/**
 * @author: jwhan
 * @createTime: 2022/05/02 11:28 AM
 * @description: The repository of location service
 */
public class LocationRepository {
    private static final String TAG = "LocationRepository";
    private static LocationRepository locationRepository = null;
    private MutableLiveData<LocationBean> locationLiveData = new MutableLiveData<>();

    private LocationRepository() {
    }

    public static LocationRepository getInstance() {
        if (locationRepository == null) {
            locationRepository = new LocationRepository();
        }
        return locationRepository;
    }

    public void updateLocation() {
        askGPSEnable();
        if (ContextCompat.checkSelfPermission(ContextSupplier.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if the user doesn't get us the permission of FINE_LOCATION, we will return the London's lat and lon
            locationLiveData.setValue(new LocationBean(NumberOperation.round(51.509865, 2),
                    NumberOperation.round(-0.118092, 2)));
            return;
        }
        LocationManager locationManager = (LocationManager) ContextSupplier.getContext().getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // we will use getCurrentLocation() method if the device is running android R or higher
            locationManager.getCurrentLocation(LocationManager.FUSED_PROVIDER, null,
                    ContextSupplier.getContext().getMainExecutor(),
                    location -> {
                        if (location != null) {
                            double lat = NumberOperation.round(location.getLatitude(), 2);
                            double lon = NumberOperation.round(location.getLongitude(), 2);
                            locationLiveData.postValue(new LocationBean(lat, lon));
                        }
                    });
        } else {
            // use getLastKnownLocation()
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1000,
                        location -> {
                            if (location != null) {
                                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                double lat = NumberOperation.round(lastKnownLocation.getLatitude(), 2);
                                double lon = NumberOperation.round(lastKnownLocation.getLongitude(), 2);
                                locationLiveData.setValue(new LocationBean(lat, lon));
                            }
                        });
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "updateLocation: " + "The device doesn't support GPS");
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1000,
                    location -> {
                        if (location != null) {
                            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            double lat = NumberOperation.round(lastKnownLocation.getLatitude(), 2);
                            double lon = NumberOperation.round(lastKnownLocation.getLongitude(), 2);
                            locationLiveData.setValue(new LocationBean(lat, lon));
                        }
                    });
        }
    }

    public LiveData<LocationBean> getLocation() {
        return locationLiveData;
    }

    private void askGPSEnable() {
        LocationManager locationManager = (LocationManager) ContextSupplier.getContext().getSystemService(Context.LOCATION_SERVICE);
        PackageManager packageManager = ContextSupplier.getContext().getPackageManager();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ContextSupplier.getContext());
            builder.setTitle("Please open the gps")
                    .setMessage("We need the gps to get your location. Please enable the gps")
                    .setPositiveButton("Yes", (dialog, which) ->
                            ContextSupplier.getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("No", (dialog, which) ->
                            Toast.makeText(ContextSupplier.getContext(),
                                    "Okay, we will show London's weather as we cant get your location", Toast.LENGTH_SHORT).show())
                    .setCancelable(false)
                    .show();
        }
    }
}
