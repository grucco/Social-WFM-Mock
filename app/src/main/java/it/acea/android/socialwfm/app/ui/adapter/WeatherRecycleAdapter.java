package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.johnhiott.darkskyandroidlib.models.DataPoint;
import com.mikepenz.iconics.view.IconicsTextView;
import com.thbs.skycons.library.CloudFogView;
import com.thbs.skycons.library.CloudHvRainView;
import com.thbs.skycons.library.CloudMoonView;
import com.thbs.skycons.library.CloudRainView;
import com.thbs.skycons.library.CloudSnowView;
import com.thbs.skycons.library.CloudSunView;
import com.thbs.skycons.library.CloudThunderView;
import com.thbs.skycons.library.CloudView;
import com.thbs.skycons.library.WindView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;

/**
 * Created by Raphael on 19/10/2015.
 */
public class WeatherRecycleAdapter extends RecyclerView.Adapter<WeatherRecycleAdapter.MainViewHolder> {

    private List<DataPoint> data;
    private Context context;

    public WeatherRecycleAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.row_weather,
                        parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        DataPoint item = data.get(position);

        holder.sun_view.setVisibility(View.GONE); //*
        holder.moon_view.setVisibility(View.GONE); //*
        holder.cloud_sun_view.setVisibility(View.GONE); //*
        holder.cloud_moon_view.setVisibility(View.GONE); //*
        holder.cloud_view.setVisibility(View.GONE);//*
        holder.cloud_rain_view.setVisibility(View.GONE); //*
        holder.cloud_hvrain_view.setVisibility(View.GONE); //*
        holder.cloud_snow_view.setVisibility(View.GONE); //*
        holder.wind_view.setVisibility(View.GONE); //*
        holder.cloud_fog_view.setVisibility(View.GONE); //*
        holder.thunder_view.setVisibility(View.GONE); //*

        switch (WeatherIcon.fromString(item.getIcon().trim())) {
            case CLEAR_DAY:
                holder.sun_view.setVisibility(View.VISIBLE);
                break;
            case CLEAR_NIGHTS:
                holder.moon_view.setVisibility(View.VISIBLE);
                break;
            case RAIN:
                holder.cloud_rain_view.setVisibility(View.VISIBLE);
                break;

            case SLEET:
            case SNOW:
                holder.cloud_snow_view.setVisibility(View.VISIBLE);
                break;

            case WIND:
                holder.wind_view.setVisibility(View.VISIBLE);
                break;
            case CLOUDY:
                holder.cloud_view.setVisibility(View.VISIBLE);
                break;
            case FOG:
                holder.cloud_fog_view.setVisibility(View.VISIBLE);
                break;

            case PARTY_CLOUDY_DAY:
                holder.cloud_sun_view.setVisibility(View.VISIBLE);
                break;

            case PARTY_CLOUDY_NIGHT:
                holder.cloud_moon_view.setVisibility(View.VISIBLE);
                break;

            case HAIL:
                holder.cloud_hvrain_view.setVisibility(View.VISIBLE);
                break;

            case TORNADO:
            case THUNDERSTORM:
                holder.thunder_view.setVisibility(View.VISIBLE);
                break;
        }

        holder.weather_temp.setText(context.getResources().getString(
                R.string.temp_label, "" + item.getApparentTemperature()));

        holder.weather_prec
                .setText(context.getResources().getString(R.string.precip_label,
                        Utils.cleanNullableValue(item.getPrecipProbability())));

        holder.weather_umi.setText(context.getResources().getString(R.string.umid_label, item.getHumidity()));
        holder.weather_wind.setText(context.getResources().getString(R.string.wind_label, item.getWindSpeed()));

        int hour = new Date(item.getTime()).getHours();
        int minutes = new Date(item.getTime()).getMinutes();
        String date = ((hour <= 9) ? "0" : "") + hour + ":" + ((minutes <= 9) ? "0" : "") + minutes;
        holder.weather_time.setText(date);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void insert(List<DataPoint> data) {
        int count = getItemCount();
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
        notifyItemRangeInserted(count, data.size());
    }

    public void update(List<DataPoint> data) {
        int count = getItemCount();
        this.data.addAll(data);
        this.notifyDataSetChanged();
        notifyItemRangeInserted(count, data.size());
    }


    public DataPoint getItem(int position) {
        return this.data.get(position);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        public com.thbs.skycons.library.SunView sun_view;
        public com.thbs.skycons.library.MoonView moon_view;
        public CloudSunView cloud_sun_view;
        public CloudMoonView cloud_moon_view;
        public CloudView cloud_view;
        public CloudRainView cloud_rain_view;
        public CloudHvRainView cloud_hvrain_view;
        public CloudSnowView cloud_snow_view;
        public WindView wind_view;
        public CloudFogView cloud_fog_view;
        public CloudThunderView thunder_view;

        public IconicsTextView weather_temp;
        public IconicsTextView weather_prec;
        public IconicsTextView weather_umi;
        public IconicsTextView weather_wind;
        public TextView weather_time;

        public MainViewHolder(View itemView) {
            super(itemView);

            sun_view = (com.thbs.skycons.library.SunView) itemView.findViewById(R.id.sun_view);
            moon_view = (com.thbs.skycons.library.MoonView) itemView.findViewById(R.id.moon_view);
            cloud_sun_view = (com.thbs.skycons.library.CloudSunView) itemView.findViewById(R.id.cloud_sun_view);
            cloud_moon_view = (com.thbs.skycons.library.CloudMoonView) itemView.findViewById(R.id.cloud_moon_view);
            cloud_view = (com.thbs.skycons.library.CloudView) itemView.findViewById(R.id.cloud_view);
            cloud_rain_view = (com.thbs.skycons.library.CloudRainView) itemView.findViewById(R.id.cloud_rain_view);
            cloud_hvrain_view = (com.thbs.skycons.library.CloudHvRainView) itemView.findViewById(R.id.cloud_hvrain_view);
            cloud_snow_view = (com.thbs.skycons.library.CloudSnowView) itemView.findViewById(R.id.cloud_snow_view);
            wind_view = (com.thbs.skycons.library.WindView) itemView.findViewById(R.id.wind_view);
            cloud_fog_view = (com.thbs.skycons.library.CloudFogView) itemView.findViewById(R.id.cloud_fog_view);
            thunder_view = (com.thbs.skycons.library.CloudThunderView) itemView.findViewById(R.id.thunder_view);

            weather_temp = (IconicsTextView) itemView.findViewById(R.id.weather_temp);
            weather_prec = (IconicsTextView) itemView.findViewById(R.id.weather_prec);
            weather_umi = (IconicsTextView) itemView.findViewById(R.id.weather_umi);
            weather_wind = (IconicsTextView) itemView.findViewById(R.id.weather_wind);
            weather_time = (TextView) itemView.findViewById(R.id.weather_time);
        }

    }

    ;

    public enum WeatherIcon {
        CLEAR_DAY("clear-day"),
        CLEAR_NIGHTS("clear-night"),
        RAIN("rain"),
        SNOW("snow"),
        SLEET("sleet"),
        WIND("wind"),
        FOG("fog"),
        CLOUDY("cloudy"),
        PARTY_CLOUDY_DAY("partly-cloudy-day"),
        PARTY_CLOUDY_NIGHT("partly-cloudy-night"),
        HAIL("hail"),
        THUNDERSTORM("thunderstorm"),
        TORNADO("tornado");

        private final String type;

        WeatherIcon(String type) {
            this.type = type;
        }

        public String getValue() {
            return type;
        }

        public static WeatherIcon fromString(String text) {
            if (text != null) {
                for (WeatherIcon b : WeatherIcon.values()) {
                    if (text.equalsIgnoreCase(b.getValue())) {
                        return b;
                    }
                }
            }
            return null;
        }
    }
};