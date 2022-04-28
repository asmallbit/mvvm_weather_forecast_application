package ml.ruby.weatherrecyclerview.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ml.ruby.weatherrecyclerview.R;
import ml.ruby.weatherrecyclerview.adapter.WeatherRecyclerViewAdapter;
import ml.ruby.weatherrecyclerview.model.Daily;
import ml.ruby.weatherrecyclerview.model.QueryParams;
import ml.ruby.weatherrecyclerview.model.WeatherBean;
import ml.ruby.weatherrecyclerview.utils.Constants;
import ml.ruby.weatherrecyclerview.viewmodel.WeatherFragmentViewModel;

/**
 * @author: jwhan
 * @createTime: 2022/04/27 11:02 AM
 * @description: Fragment of weather forecast
 */
public class WeatherFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private static Handler handler = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == Constants.UPDATE_WEATHER_INFO_SUCCESSFULLY) {
                    refreshLayout.setRefreshing(false);
                } else if (msg.arg1 == Constants.UPDATE_WEATHER_INFO_FAILED) {
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(WeatherFragment.this.getContext(),
                            "Refresh weather info failed, check your internet connection",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // Get the ViewModel instance
        WeatherFragmentViewModel model = new ViewModelProvider(this).get(WeatherFragmentViewModel.class);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        List<Daily> dailyList = new ArrayList<>();
        WeatherRecyclerViewAdapter adapter = new WeatherRecyclerViewAdapter(dailyList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Do a request when the user launcher the application
        // TODO: We need get the lat, lon and lang from the GPS and device itself but not hard code
        model.queryWeatherInfo(new QueryParams("30.98", "115.89", "zh_cn"));

        // Set a observer to observe the weather info
        model.getWeatherLiveData().observe(WeatherFragment.this, weatherBean -> {
            // We will notify the adapter when the user make a new request successfully
            new Thread(() -> {
                WeatherBean value = model.getWeatherLiveData().getValue();
                if (value != null) {
                    List<Daily> dailies = value.getDaily();
                    dailyList.clear();
                    dailyList.addAll(dailies);
                }
                requireActivity().runOnUiThread(adapter::notifyDataSetChanged);
            }).start();
        });

        // TODO: We need get the lat, lon and lang from the GPS and device itself but not hard code
        refreshLayout.setOnRefreshListener(() ->
                model.queryWeatherInfo(new QueryParams("30.98", "115.89", "zh_cn")));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        recyclerView = view.findViewById(R.id.weather_recyclerview);
        refreshLayout = view.findViewById(R.id.refresh_data);
        return view;
    }
}
