package ml.ruby.weatherrecyclerview.viewmodel;

import android.location.Location;

import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import ml.ruby.weatherrecyclerview.model.LocationBean;
import ml.ruby.weatherrecyclerview.model.QueryParams;
import ml.ruby.weatherrecyclerview.model.weather.WeatherBean;
import ml.ruby.weatherrecyclerview.repository.LocationRepository;
import ml.ruby.weatherrecyclerview.repository.WeatherRepository;

/**
 * @author: jwhan
 * @createTime: 2022/04/27 5:56 PM
 * @description: ViewModel of WeatherFragment fragment
 */
public class WeatherFragmentViewModel extends ViewModel {

    public WeatherFragmentViewModel() {
    }

    public LiveData<WeatherBean> getWeatherLiveData() {
        return WeatherRepository.getInstance().getWeatherInfo();
    }

    public void queryWeatherInfo(@NonNull QueryParams params) {
        WeatherRepository.getInstance().queryWeatherInfo(params.lat, params.lon, params.lang, params.getAppId());
    }

    public LiveData<LocationBean> getLocationLiveData() {
        return LocationRepository.getInstance().getLocation();
    }

    public void updateLocation() {
        LocationRepository.getInstance().updateLocation();
    }
}
