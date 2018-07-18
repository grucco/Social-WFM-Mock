package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.fingerlinks.mobile.android.utils.Log;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.http.HttpClientRequest;

/**
 * Created by Raphael on 04/11/2015.
 */
public class FragmentProfileSocial extends BaseFragment {


    private Thread profileFetcher;


    private String userId;

    @Bind(R.id.following_profile)
    TextView following_profile;

    @Bind(R.id.followers_profile)
    TextView followers_profile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userId = getArguments().getString("USER_ID");
        View view = inflater.inflate(R.layout.fragment_profile_social, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileFetcher = new Thread() {
            @Override
            public void run() {
                HttpClientRequest.executeRequestGetFollowers(getActivity(), userId, new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            return;
                        }
                        GsonBuilder builder = Utils.getRealmGsonBuilder();
                        Gson gson = builder.create();
                        Type listType = new TypeToken<List<Profile>>() {
                        }.getType();
                        final List<Profile> profiles = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (profiles == null) {
                                    followers_profile.setText(getString(R.string.followers_profile, 0));
                                    return;
                                }
                                followers_profile.setText(getString(R.string.followers_profile, profiles.size()));
                            }
                        });
                    }
                });
                HttpClientRequest.executeRequestGetFollowing(getActivity(), userId, new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            return;
                        }
                        GsonBuilder builder = Utils.getRealmGsonBuilder();
                        Gson gson = builder.create();
                        Type listType = new TypeToken<List<Profile>>() {
                        }.getType();
                        List<Profile> profiles = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                        if (profiles == null) {
                            following_profile.setText(getString(R.string.following_profile, 0));
                            return;
                        }
                        following_profile.setText(getString(R.string.following_profile, profiles.size()));
                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });*/
                    }
                });
            }
        };

        profileFetcher.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            profileFetcher.interrupt();
            Ion.getDefault(getActivity()).cancelAll(getActivity());
            Log.d(this.getClass(), "------------------------------ DESTROY");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }
}
