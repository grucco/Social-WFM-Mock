package it.acea.android.socialwfm.factory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.utils.HttpMethod;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Raphael on 03/11/2015.
 */
public class ImageFactory {

    private static final String TAG = ImageFactory.class.getName();

    private Context context;
    private String id;
    private TYPE type;
    private Drawable placeholder;

    public ImageFactory(Context context, String id, TYPE type) {
        this.context = context;
        this.id = id;
        this.type = type;
        this.placeholder = context.getResources().getDrawable(R.drawable.img_place_holder);
    }

    public ImageFactory(Context context, String id, TYPE type, Drawable placeholder) {
        this.context = context;
        this.id = id;
        this.type = type;
        this.placeholder = placeholder;
    }

    public void into(final ImageView imageView) {
        String urlImage = context.getResources().getString(R.string.oauth_consumer_base_url);
        switch (type) {
            case MEMBER:
                urlImage = urlImage + "/api/v1/OData/Members('" + id + "')/ProfilePhoto/$value";
                break;
            case GROUP:
                urlImage = urlImage + "/api/v1/OData/Groups('" + id + "')/ProfilePhoto/$value";
                break;
            case FEED:
                urlImage = urlImage + "/api/v1/OData/FeedEntryImages('" + id + "')/$value";
                break;
            case COMMENT:
                urlImage = urlImage + "/api/v1/OData/ThumbnailImages(Id='" + id + "',ThumbnailImageType='48x48')/$value";
                break;
        }
        OauthFactory factory = new OauthFactory(context, urlImage);

        Ion.with(context).load(factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                       if(result!=null){
                           imageView.setImageBitmap(result);
                       }
                        else{
                           if(placeholder!=null){
                               imageView.setImageDrawable(placeholder);
                           }
                       }

                    }
                });
    }

    public enum TYPE {
        MEMBER(0),
        GROUP(1),
        FEED(2),
        COMMENT(3);

        private final int getType;

        TYPE(int getType) {
            this.getType = getType;
        }

    }

}
