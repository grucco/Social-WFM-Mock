package it.acea.android.socialwfm.app.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.mikepenz.materialdrawer.view.BezelImageView;

import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.Log;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.fingerlinks.mobile.android.utils.widget.SlidingTabLayout;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.events.FeedListUpdate;
import it.acea.android.socialwfm.app.events.FollowMember;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.model.ess.DatiPersonali;
import it.acea.android.socialwfm.app.ui.adapter.ProfileAdapter;
import it.acea.android.socialwfm.app.ui.dialog.ChangeImageDialog;
import it.acea.android.socialwfm.app.ui.dialog.FullScreenImageDialog;
import it.acea.android.socialwfm.app.ui.dialog.NewPostDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroDettaglioContatto;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.factory.PopolateFeed;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;
import it.acea.android.socialwfm.http.response.ResponseManager;
import tr.xip.errorview.ErrorView;

/**
 * Created by Raphael on 03/11/2015.
 */
public class ProfileFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, PopolateFeed.CallBack {
    public static final String ARGUMENT_PROFILE_ID = "ID_PROFILE";

    private static final String TAG = ProfileFragment.class.getName();
    private String id;
    private MaterialDialog progressDialog;
    private boolean contactMode = false;
    private boolean isFirstTime = true;

    @Bind(R.id.progress_layout)
    View progress_layout;

    @Bind(R.id.avatar)
    BezelImageView avatar;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.message_profile)
    ImageView message;

    @Bind(R.id.chat_profile)
    ImageView chat;

    @Bind(R.id.chat_button)
    ImageView chatButton;

    @Bind(R.id.preferred_profile)
    ImageView preferred;

    @Bind(R.id.tab_layout)
    SlidingTabLayout slidingTabLayout;

    @Bind(R.id.pager)
    ViewPager pager;

    @Bind(R.id.list_post)
    RecyclerView listPost;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.profile_edit)
    ImageView profile_edit;

    @Bind(R.id.error)
    ErrorView errorView;

    private PopolateFeed popolateFeed;
    private Context context;

    private NewPostDialog newPostDialog;
    private ChangeImageDialog dialog;

    public static ProfileFragment newInstance(String profileId) {
        ProfileFragment p = new ProfileFragment();
        Bundle b = new Bundle();
        b.putString(ARGUMENT_PROFILE_ID, profileId);
        p.setArguments(b);
        return p;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.contacts_menu, menu);
        menu.findItem(R.id.action_search).setVisible(contactMode);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void startDetailIntro(){
            new IntroDettaglioContatto(getActivity(), preferred, listPost.getChildAt(0)).start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        id = bundle.getString(ARGUMENT_PROFILE_ID);
        setTitle(R.string.app_name_title);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dialog != null)
            dialog.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        refreshLayout.setColorSchemeResources(R.color.accent, R.color.primary);
        refreshLayout.setOnRefreshListener(this);
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                getProfile();
                getFeedProfile();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isFirstTime) {
            isFirstTime = false;
            getProfile();
            getFeedProfile();
        }
    }

    private void fetchPersonalData() {
        showProgressDialog();
        EssClient.fetchDatiPersonali(getContext(), new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    //List<DatiPersonali> datiPersonali = ResponseManager.getDatiPersonali(e, result);
                    DatiPersonali d = new DatiPersonali();
                    d.setCittadinanza("Italiana");
                    d.setCodiceFiscale("SMNNDR12A34B567C");
                    d.setCognome("Simeoni");
                    Calendar c = new GregorianCalendar();
                    c.set(1993,10,7);
                    d.setDataDiNascita(c.getTime());
                    DatiPersonali.Indirizzo i = new DatiPersonali.Indirizzo();
                    i.setCap("00147");
                    i.setCitta("Roma");
                    i.setDomicilioIsResidenza(true);
                    i.setDomicilioIsResidenzaDescrizione("Si");
                    i.setTipologiaVia("Via");
                    i.setIndirizzo("Vittore Carpaccio");
                    i.setNumeroCivico("26");
                    i.setNumeroDiTelefono("3921234567");
                    i.setPaese("Italia");
                    i.setProvincia("Roma");
                    i.setTipologiaIndirizzo("RESIDENZA");
                    i.setRegione("Lazio");
                    i.setProvinciaSigla("Roma");
                    List<DatiPersonali.Indirizzo> ii = new ArrayList<>();
                    ii.add(i);
                    d.setIndirizzi(ii);
                    d.setGruppo("ACEA");
                    d.setLingua("Italiano");
                    d.setLuogoDiNascita("Roma");
                    d.setNome("Andrea");
                    d.setNumeroDiFigli(0);
                    d.setPaeseDiOrigine("Italia");
                    d.setProvincia("Roma");
                    d.setSesso("Maschio");
                    d.setTitolo("Sig.");
                    d.setStatoCivile("Celibe");
                    List<DatiPersonali> datiPersonali = new ArrayList<>();
                    datiPersonali.add(d);
                    EventBus.getDefault().postSticky(new DatiPersonaliPrelevati(datiPersonali.get(0)));
                } catch (Exception e1) {
                    e1.printStackTrace();
                    showErrorMessage();
                } finally {
                    dismissProgressDialog();
                }
            }
        });
    }


    private boolean isThisMyProfile(Profile profile){
        final User user = UserHelperFactory.getAuthenticatedUser(context);
        return user.getId().equals(profile.getId());
    }

    private void setProfileMode(Profile profile) {
        contactMode = !isThisMyProfile(profile);
        setSubtitle(contactMode ? R.string.label_menu_contacts : R.string.mio_profilo_subtitle);
        getActivity().supportInvalidateOptionsMenu();
    }

    private void setView(final Profile profile) {
        final User user = UserHelperFactory.getAuthenticatedUser(context);
        ProfileAdapter adapter = new ProfileAdapter(getChildFragmentManager(), profile.getId(), context);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
        slidingTabLayout.setTextColor(R.color.white);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(pager);

        if (user.getId().equals(profile.getId())) {
            fetchPersonalData();
            message.setVisibility(View.INVISIBLE);
            chat.setVisibility(View.INVISIBLE);
            preferred.setVisibility(View.INVISIBLE);
            profile_edit.setVisibility(View.VISIBLE);
        } else {
            profile_edit.setVisibility(View.GONE);
        }
        if (profile.isIsFollowing()) {
            preferred.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_preferred_profile));
        } else {
            preferred.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unpreferred_profile));
        }
        preferred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(getActivity())) {
                    Utils.errorToast((getActivity()));
                    return;
                }
                if (profile.isIsFollowing()) {
                    HttpClientRequest.setUnFollowingUser(context, user.getId(), profile.getId(), new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e != null) {
                                return;
                            }
                            if (result.getHeaders().code() == 204) {
                                preferred.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unpreferred_profile));
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                profile.setIsFollowing(false);
                                realm.commitTransaction();
                                Toast.makeText(getActivity(), "Hai smesso di seguire questo contatto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    HttpClientRequest.setFollowingUser(context, user.getId(), profile.getId(), new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e != null) {
                                return;
                            }
                            if (result.getHeaders().code() == 204) {
                                preferred.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_preferred_profile));
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                profile.setIsFollowing(true);
                                realm.commitTransaction();
                                EventBus.getDefault().post(new FollowMember());
                                Toast.makeText(getActivity(), "Stai seguendo questo contatto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        name.setText(profile.getFullName());
        new ImageFactory(context, profile.getId(),
                ImageFactory.TYPE.MEMBER,
                Utils.avatarPlaceholder(context,
                        profile.getFirstName(),
                        profile.getLastName()))
                .into(avatar);

        avatar.setTag(avatar.getDrawable());
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenImageDialog dialog = new FullScreenImageDialog(getActivity(), (Drawable) v.getTag());
                dialog.show();
            }
        });
        progress_layout.setVisibility(View.GONE);

        // Se sto visualizzando il profilo di un altro utente
        // attivo il pulsante della chat
        if(!user.getId().equals(profile.getId())){
            // TODO: Ripristinare per l'integrazione con Spark
            //chatButton.setVisibility(View.VISIBLE);
            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChatButtonPressed(profile.getEmail());
                }
            });
        }

    }

    public void onChatButtonPressed(String email){
        String url = "xmpp:"+email;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try{
            startActivity(i);
        }catch(ActivityNotFoundException ex){
            Log.d(TAG, "ActivityNotFoundException");
            showJabberNotFoundDialog();
        }

    }

    private void showJabberNotFoundDialog(){
        new MaterialDialog.Builder(getContext())
                .title(R.string.alert_jabber_non_trovato)
                .content(R.string.alert_jabber_non_trovato_msg)
                .positiveText(R.string.ok)
                .show();
    }

    @OnClick(R.id.profile_edit)
    public void updateUserAvatar() {
        User user = UserHelperFactory.getAuthenticatedUser(getActivity());
        dialog = new ChangeImageDialog(
                getActivity(),
                getActivity(),
                user.getId(),
                ChangeImageDialog.TYPE.USER);
        dialog.setCallBack(new ChangeImageDialog.CallBack() {
            @Override
            public void onSucces(String id) {
                popolateFeed.update();
                getProfile();
            }
        });
        dialog.show();
        dialog.setImage(avatar.getDrawable());
    }

    private void getProfile() {
            errorView.setVisibility(View.GONE);
            HttpClientRequest.executeRequestGetProfile(context, id, new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    try {
                        CheckErrorInJamResponse.check(e, result);
                        GsonBuilder builder = Utils.getRealmGsonBuilder();
                        Gson gson = builder.create();
                        Profile profile = gson.fromJson(result.getAsJsonObject("d").getAsJsonObject("results"), Profile.class);
                        if (profile != null) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(profile);
                            realm.commitTransaction();
                            setProfileMode(profile);
                            setView(profile);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        errorView.setVisibility(View.VISIBLE);
                    }
                }
            });
    }

    private void getFeedProfile() {
        refreshLayout.setRefreshing(true);
        String url = getActivity().getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + id + "')/FeedEntries?$format=json&$expand=PreviewImage,Creator,AtMentions";
        popolateFeed = new PopolateFeed(context, url);
        popolateFeed.into(listPost);
        popolateFeed.setCallBack(this);
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onRefresh() {
        popolateFeed.update();
    }

    @Override
    public void onFinish() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onHeaderClick() {
        newPostDialog = new NewPostDialog(getActivity(), NewPostDialog.TYPE.MEMBER, id);
        newPostDialog.setCallBack(new NewPostDialog.CallBack() {
            @Override
            public void onSucces() {
                newPostDialog.dismiss();
                popolateFeed.update();
            }
        });
        newPostDialog.show();
    }

    private void showProgressDialog() {
        progressDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.attendere)
                .content(R.string.download_dati_in_corso)
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(true)
                .show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    private void showErrorMessage() {
        Toast.makeText(getContext(), R.string.errore_download_dati_personali, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void updateFeedList(FeedListUpdate event) {
        popolateFeed.update();
    }

    public static class DatiPersonaliPrelevati {
        public DatiPersonali datiPersonali;
        public DatiPersonaliPrelevati(DatiPersonali datiPersonali) {
            this.datiPersonali = datiPersonali;
        }
    }
}
