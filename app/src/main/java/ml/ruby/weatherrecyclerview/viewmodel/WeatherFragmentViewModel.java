package ml.ruby.weatherrecyclerview.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import ml.ruby.weatherrecyclerview.model.QueryParams;
import ml.ruby.weatherrecyclerview.model.WeatherBean;
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
}
