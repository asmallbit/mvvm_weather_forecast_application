package ml.ruby.weatherrecyclerview.repository;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

import androidx.lifecycle.MutableLiveData;
import ml.ruby.weatherrecyclerview.MainActivity;
import ml.ruby.weatherrecyclerview.model.weather.WeatherBean;
import ml.ruby.weatherrecyclerview.depencyInject.RetrofitClient;
import ml.ruby.weatherrecyclerview.utils.Constants;
import ml.ruby.weatherrecyclerview.view.WeatherFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: jwhan
 * @createTime: 2022/04/27 4:17 PM
 * @description: Repository of the application
 */

public class WeatherRepository {
    private static final String TAG = "WeatherRepository";
    private static WeatherRepository repository = null;
    private MutableLiveData<WeatherBean> weatherLiveData = new MutableLiveData<>();

    private WeatherRepository() {
    }

    public static WeatherRepository getInstance() {
        if (repository == null) {
            repository = new WeatherRepository();
        }
        return repository;
    }

    // Query the weather info
    public void queryWeatherInfo(@NotNull String lat,
                                 @NotNull String lon,
                                 @NotNull String lang,
                                 @NotNull String appid) {
        Call<WeatherBean> weatherCall = RetrofitClient.weatherProvider().getWeatherInfo(lat, lon, lang, appid);
        // Call the api
        weatherCall.enqueue(new Callback<WeatherBean>() {
            @Override
            public void onResponse(Call<WeatherBean> call, Response<WeatherBean> response) {
                // Okay, we get the response successfully
                weatherLiveData.postValue(response.body());
                try {
                    Field progressBar = MainActivity.class.getDeclaredField("progressBar");
                    progressBar.setAccessible(true);
                    ((ProgressBar) progressBar.get(null)).setVisibility(View.GONE);
                    // Send the message to notify the swipeRefresh finish
                    Field handlerField = WeatherFragment.class.getDeclaredField("handler");
                    handlerField.setAccessible(true);
                    Handler handler = ((Handler) handlerField.get(null));
                    Message message = new Message();
                    message.arg1 = Constants.UPDATE_WEATHER_INFO_SUCCESSFULLY;
                    handler.sendMessage(message);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WeatherBean> call, Throwable t) {
                // Use a handle-message item to make a toast on the screen to notify the user
                Message message = new Message();
                message.arg1 = Constants.TOAST_WEATHER_REQUEST_FAILED;
                message.obj = new String(TAG + ": Connect to open weather. Maybe you don't have internet now.");
                try {
                    // Make the progressbar disappear
                    Field progressBar = MainActivity.class.getDeclaredField("progressBar");
                    progressBar.setAccessible(true);
                    ((ProgressBar) progressBar.get(null)).setVisibility(View.GONE);
                    // Send the failed toast to the user
                    Field mainActivityHandlerField = MainActivity.class.getDeclaredField("handler");
                    mainActivityHandlerField.setAccessible(true);
                    Handler mainActivityHandler = (Handler) mainActivityHandlerField.get(null);
                    mainActivityHandler.sendMessage(message);
                    // Send the message to notify the swipeRefresh finish
                    Field weatherFragmentHandlerField = WeatherFragment.class.getDeclaredField("handler");
                    weatherFragmentHandlerField.setAccessible(true);
                    Handler weatherFragmentHandler = ((Handler) weatherFragmentHandlerField.get(null));
                    Message weatherFragmentMessage = new Message();
                    weatherFragmentMessage.arg1 = Constants.UPDATE_WEATHER_INFO_FAILED;
                    weatherFragmentHandler.sendMessage(weatherFragmentMessage);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MutableLiveData<WeatherBean> getWeatherInfo() {
        return weatherLiveData;
    }
}
