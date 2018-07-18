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
import it.acea.android.socialwfm.http.response.PersonInfoResponse;
import it.acea.android.socialwfm.http.response.user.info.PhoneNumbers;

/**
 * Created by Raphael on 04/11/2015.
 */
public class FragmentProfileLavoro extends BaseFragment {

    @Bind(R.id.manager)
    TextView manager;
    @Bind(R.id.subordinati)
    TextView subordinati;
    @Bind(R.id.assistenti)
    TextView assistenti;
    @Bind(R.id.sector_profile)
    TextView sector_profile;
    @Bind(R.id.mobile_profile)
    TextView mobile_profile;

    private Thread profileLavoroFetcher;

    private String userId;
    private String getDistretto;
    private String getReti;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userId = getArguments().getString("USER_ID");
        View view = inflater.inflate(R.layout.fragment_profile_lavoro, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileLavoroFetcher = new Thread() {
            @Override
            public void run() {
                HttpClientRequest.executeRequestGetMemberProfile(getActivity(), userId, new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            return;
                        }
                        PersonInfoResponse response = new Gson()
                                .fromJson(result.getAsJsonObject("d")
                                        .get("results"), PersonInfoResponse.class);
                        if (response == null) {
                            return;
                        }
                        getReti = "";
                        getDistretto = "";
                        for (PhoneNumbers numbers : response.getPhoneNumbers().getResults()) {
                            if (numbers.getPhoneNumberType().equals("Other"))
                                getReti = numbers.getPhoneNumber();
                            if (numbers.getPhoneNumberType().equals("Pager"))
                                getDistretto = numbers.getPhoneNumber();
                        }
                        sector_profile.setText(getReti);
                        mobile_profile.setText(getDistretto);
                    }
                });
                HttpClientRequest.executeRequestGetManagers(getActivity(), userId, new FutureCallback<JsonObject>() {
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
                            return;
                        }
                        String value = "";
                        for (Profile profile : profiles) {
                            if (value.length() == 0) {
                                value = profile.getFullName();
                            } else {
                                value = value + ", " + profile.getFullName();
                            }
                        }
                        final String finalValue = value;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    manager.setText(finalValue);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                });
                HttpClientRequest.executeRequestGetAssistants(getActivity(), userId, new FutureCallback<JsonObject>() {
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
                        if (profiles != null && !profiles.isEmpty()) {
                            String value = "";
                            for (Profile profile : profiles) {
                                if (value.length() == 0) {
                                    value = profile.getFullName();
                                } else {
                                    value = value + ", " + profile.getFullName();
                                }
                            }
                            assistenti.setText(value);
                        }
                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });*/
                    }
                });
                HttpClientRequest.executeRequestGetDirectReports(getActivity(), userId, new FutureCallback<JsonObject>() {
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
                            return;
                        }
                        String value = "";
                        for (Profile profile : profiles) {
                            if (value.length() == 0) {
                                value = profile.getFullName();
                            } else {
                                value = value + ", " + profile.getFullName();
                            }
                        }
                        final String finalValue = value;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                subordinati.setText(finalValue);
                            }
                        });
                    }
                });
            }
        };
        profileLavoroFetcher.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            profileLavoroFetcher.interrupt();
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
