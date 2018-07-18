package it.acea.android.socialwfm.app.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.activity.BaseActivity;

import java.lang.reflect.Type;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.ui.fragment.ProfileFragment;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.feed.CheckError;
import it.acea.android.socialwfm.http.response.user.UserSuggestion;

/**
 * Created by Raphael on 11/12/2015.
 */
public class OpenProfileActivity extends BaseActivity {

    private static final String TAG = OpenProfileActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationIcon(R.drawable.ic_ab_back);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Uri data = getIntent().getData();
        String path = data.getLastPathSegment().replace("@", "").replace(" ", "%20");
        Log.d(TAG, path);
        HttpClientRequest.userAutoComplete(this, path, null, new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e != null) {
                    finish();
                    return;
                }
                GsonBuilder builder = Utils.getRealmGsonBuilder();
                Gson gson = builder.create();
                CheckError error = gson.fromJson(result, CheckError.class);
                if (error.getError() != null) {
                    Toast.makeText(OpenProfileActivity.this, error.getError().getMessage().getValue(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Type listType = new TypeToken<List<UserSuggestion>>() {
                }.getType();
                List<UserSuggestion> suggestions = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                if (suggestions == null || suggestions.size() == 0) {
                    Toast.makeText(OpenProfileActivity.this, "Utente non trovato", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Bundle bundle_id = new Bundle();
                bundle_id.putString(ProfileFragment.ARGUMENT_PROFILE_ID, suggestions.get(0).getId());
                Navigator.with(OpenProfileActivity.this)
                        .build()
                        .goTo(Fragment.instantiate(OpenProfileActivity.this, ProfileFragment.class.getName()), bundle_id, R.id.container)
                        .tag(ProfileFragment.class.getName())
                        .addToBackStack()
                        .add()
                        .commit();
            }
        });
    }

    @Override
    protected int getToolbarProjectId() {
        return R.id.toolbar;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_support;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }
}
