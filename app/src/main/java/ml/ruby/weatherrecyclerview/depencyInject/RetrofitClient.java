package ml.ruby.weatherrecyclerview.depencyInject;

import ml.ruby.weatherrecyclerview.api.WeatherApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: jwhan
 * @createTime: 2022/04/27 4:25 PM
 * @description:
 */
public class RetrofitClient {
    static Retrofit objRetrofit = null;

    public static WeatherApi weatherProvider() {
        if (objRetrofit == null) {
            objRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return objRetrofit.create(WeatherApi.class);
    }
}
