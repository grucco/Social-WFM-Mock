package it.acea.android.socialwfm.factory;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

/**
 * Created by a.simeoni on 15/11/2016.
 */

public class GoogleMapsRequestFactory {


    public enum MarkerSize {
        tiny("tiny"), mid("mid"), small("small");
        String text;
        MarkerSize(String s){
            this.text = s;
        }

        public String getText() {
            return text;
        }
    }


    private Context context;
    private String lat;
    private String lng;
    private int scale;
    private int zoomLevel;
    private int size;
    private MarkerSize markerSize;
    private String apiKey;
    private String mapUrl;
    private ImageView imageView;

    private @DrawableRes int imagePlaceHolder;


    public GoogleMapsRequestFactory with(Context context) {
        this.context = context;
        return this;
    }

    public GoogleMapsRequestFactory loadInto(ImageView imageView){
        this.imageView = imageView;
        return this;
    }

    public GoogleMapsRequestFactory setPlaceHolder(@DrawableRes int placeHolder){
        this.imagePlaceHolder = placeHolder;
        return this;
    }

    public void load(){
        Picasso.with(context).load(this.mapUrl).into(this.imageView);
    }

    public GoogleMapsRequestFactory build(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("maps.googleapis.com")
                .appendEncodedPath("/maps/api/staticmap")
                .appendQueryParameter("size",size+"x"+size)
                .appendQueryParameter("scale",scale+"")
                .appendQueryParameter("markers","size:"+markerSize.getText()+"|"+lat+","+lng)
                .appendQueryParameter("zoom",zoomLevel+"")
                .appendQueryParameter("key",apiKey);
        this.mapUrl = builder.build().toString();
        return this;
    }

    public GoogleMapsRequestFactory setLat(String lat) {
        this.lat = lat;
        return this;
    }

    public GoogleMapsRequestFactory setLng(String lng) {
        this.lng = lng;
        return this;
    }

    public GoogleMapsRequestFactory setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public GoogleMapsRequestFactory setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
        return this;
    }

    public GoogleMapsRequestFactory setMarkerSize(MarkerSize size) {
        this.markerSize = size;
        return this;
    }

    public GoogleMapsRequestFactory setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public GoogleMapsRequestFactory setSize(int size) {
        this.size = size;
        return this;
    }
}
