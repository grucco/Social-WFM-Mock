package it.acea.android.socialwfm.app.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.Log;
import org.fingerlinks.mobile.android.utils.activity.BaseActivity;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.events.CanReloadList;
import it.acea.android.socialwfm.app.events.EventPasswordExpired;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.ui.fragment.FragmentMember;
import it.acea.android.socialwfm.app.ui.fragment.FragmentMyWorkContainer;
import it.acea.android.socialwfm.app.ui.fragment.GroupsFragment;
import it.acea.android.socialwfm.app.ui.fragment.MonteOreFragment;
import it.acea.android.socialwfm.app.ui.fragment.ProfileFragment;
import it.acea.android.socialwfm.app.ui.fragment.SettingsFragment;
import it.acea.android.socialwfm.service.UpdateSchedulerJob;

import static it.acea.android.socialwfm.factory.UserHelperFactory.getAuthenticatedUser;


/**
 * Created by fabio on 15/10/15.
 */
public class MainActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    private final static String TAG = MainActivity.class.getName();

    private Drawer drawer;
    private Realm realm;

    View footerCustomView;


    private JobManager mJobManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mJobManager = JobManager.instance();
        mJobManager.setVerbose(true);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        ForecastApi.create("5b3af8cbf1cc877a45910aa3472d2261");

        footerCustomView = LayoutInflater.from(MainActivity.this).inflate(R.layout.include_footer_view, null);

        prepareMenu(savedInstanceState);

        mJobManager = JobManager.instance();


        if (savedInstanceState == null) {
            Navigator.with(MainActivity.this)
                    .build()
                    .goTo(Fragment.instantiate(MainActivity.this,
                            FragmentMyWorkContainer.class.getName()),
                            R.id.container)
                    .tag(Constant.TAG_HOME)
                    .addToBackStack()
                    .add()
                    .commit();
        }
    }


    @Override
    public void onStart() {
        //http://greenrobot.org/eventbus/documentation/how-to-get-started/
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onStop() {
        //http://greenrobot.org/eventbus/documentation/how-to-get-started/
        EventBus.getDefault().unregister(this);
        super.onStop();
        try {
            if (realm != null)
                realm.close();
        } catch (Exception _ex) {
        }
        realm = null;
    }

    @Override
    public void onBackPressed() {

        //settings fragment non estende base activity quindi non puÃ² richiamare onSupportBackPressed
        //controllo che il fragment visibile non sia quello dei settings
        if (!Navigator.with(MainActivity.this).utils().getActualTag().equals(Constant.TAG_SETTINGS)) {
            if (((BaseFragment) getSupportFragmentManager().findFragmentById(R.id.container)).onSupportBackPressed(null)) {
                return;
            }
        }

        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }


        if (Navigator.with(MainActivity.this).utils().isActualShowing(Constant.TAG_HOME)) {
            Fragment actual = getSupportFragmentManager().findFragmentByTag(Constant.TAG_HOME);
            if (actual instanceof ProfileFragment ||
                    actual instanceof SettingsFragment) {
                Navigator.with(MainActivity.this)
                        .utils().goToPreviousBackStack();
            } else {
                Navigator.with(MainActivity.this)
                        .utils()
                        .confirmExitWithMessage(R.string.app_confirm_exit);
            }
            return;
        }

        if (Navigator.with(MainActivity.this).utils().canGoBack(getSupportFragmentManager())) {
            Navigator.with(MainActivity.this)
                    .utils()
                    .goToPreviousBackStack();
        } else {
            Navigator.with(MainActivity.this)
                    .utils()
                    .confirmExitWithMessage(R.string.app_confirm_exit);
        }
    }

    private boolean isStart = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isStart) {
            isStart = true;
            /**
             * Launch Job
             */
            lauchPeriodiUpdateJob();
        }
        updateFooterView();
    }

    private Activity getThisInstance() {
        return this;
    }

    private synchronized void updateFooterView() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        String _date = format.format(new Date());

        TextView menu_footer_update = (TextView) drawer.getStickyFooter().findViewById(R.id.menu_footer_update);
        if (menu_footer_update != null) {
            menu_footer_update.setText(getResources().getString(R.string.label_menu_last_update, _date));
        }
    }

    private ProfileDrawerItem profileDrawerItem = null;
    private AccountHeader accountHeader = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private void prepareMenu(Bundle bundle) {

        final User user = getAuthenticatedUser(MainActivity.this);
        profileDrawerItem = new ProfileDrawerItem()
                .withName(user.getFullName() + " - " + user.getRole())
                .withEmail(user.getEmail());

        if (TextUtils.isEmpty(user.getProfilePhoto())) {
            profileDrawerItem.
                    withIcon(Utils.
                            avatarPlaceholder(MainActivity.this, user.getFirstName(), user.getLastName()));
        } else {
            String uriPhotoImage = getResources().getString(R.string.oauth_consumer_base_url)
                    + "/api/v1/OData/" + user.getProfilePhoto() + "/$value?$format=json";
            Log.e(TAG, "uriPhotoImage [" + uriPhotoImage + "]");
            profileDrawerItem.withIcon(uriPhotoImage);
        }

        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.primary)
                .withSavedInstance(bundle)
                .withSelectionListEnabled(false)
                .withCompactStyle(false)
                .withCurrentProfileHiddenInList(true)
                .withProfileImagesClickable(true)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        drawer.setSelection(R.id.menu_my_profile, true);
                        return false;
                    }
                })
                .addProfiles(profileDrawerItem)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(MainActivity.this)
                .withSavedInstance(bundle)
                .withActionBarDrawerToggleAnimated(true)
                .withToolbar(getToolbar())
                //.inflateMenu(R.menu.main_menu)

                .addDrawerItems(
                        new PrimaryDrawerItem().
                                withName(R.string.label_menu_home).
                                withIcon(R.drawable.ic_ab_home).
                                withSelectable(true).
                                withIdentifier(R.id.menu_home),
                        new PrimaryDrawerItem().
                                withName(R.string.label_menu_group).
                                withIcon(R.drawable.ic_ab_groups).
                                withSelectable(false).
                                withIdentifier(R.id.menu_group),
                        new PrimaryDrawerItem().
                                withName(R.string.label_menu_profile).
                                withIcon(R.drawable.ic_action_user).
                                withSelectable(true).
                                withIdentifier(R.id.menu_my_profile),
                        /*new PrimaryDrawerItem().
                                withName(R.string.label_jabber).
                                withIcon(R.drawable.ic_chat_black_24dp).
                                withSelectable(true).
                                withIdentifier(R.id.menu_jabber),*/
                        new PrimaryDrawerItem().
                                withName(R.string.label_menu_contacts).
                                withIcon(R.drawable.ic_ab_contacts).
                                withSelectable(false).
                                withIdentifier(R.id.menu_contacts),
                        new PrimaryDrawerItem().
                                withName(R.string.label_menu_wmanager).
                                withIcon(R.drawable.ic_ab_work_manager).
                                withSelectable(false).
                                withIdentifier(R.id.menu_wmanager),
                        new PrimaryDrawerItem().
                                withName(R.string.label_menu_ess).
                                withIcon(R.drawable.ic_ab_ess).
                                withSelectable(true).
                                withIdentifier(R.id.menu_ess),
                        new PrimaryDrawerItem().
                                withName(R.string.label_menu_concorsi).
                                withIcon(R.drawable.ic_trophy).
                                withSelectable(false).
                                withIdentifier(R.id.menu_concorsi),
                        new PrimaryDrawerItem().
                                withName(R.string.label_menu_survey).
                                withIcon(R.drawable.ic_assignment_black_24dp).
                                withSelectable(false).
                                withIdentifier(R.id.menu_survey)


                )

                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        final User user = getAuthenticatedUser(MainActivity.this);

                        if (TextUtils.isEmpty(user.getProfilePhoto())) {
                            profileDrawerItem.withIcon(Utils.avatarPlaceholder(MainActivity.this, user.getFirstName(), user.getLastName()));
                        } else {
                            String uriPhotoImage = getResources().getString(R.string.oauth_consumer_base_url)
                                    + "/api/v1/OData/" + user.getProfilePhoto() + "/$value?$format=json";
                            Log.e(TAG, "uriPhotoImage [" + uriPhotoImage + "]");
                            profileDrawerItem.withIcon(uriPhotoImage);
                        }
                        accountHeader.removeProfile(0);
                        accountHeader.addProfiles(profileDrawerItem);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (itemSelected == drawerItem.getIdentifier()) {
                            return false;
                        }

                        Bundle bundle;
                        Bundle supportBundle;
                        switch (drawerItem.getIdentifier()) {
                            case R.id.menu_home:
                                if (Navigator.with(MainActivity.this).utils().canGoBackToSpecificPoint(
                                        Constant.TAG_HOME,
                                        R.id.container,
                                        getSupportFragmentManager())) {
                                    Navigator.with(MainActivity.this)
                                            .utils()
                                            .goBackToSpecificPoint(Constant.TAG_HOME);
                                }
                                itemSelected = drawerItem.getIdentifier();

                                return false;
                            case R.id.menu_contacts:
                                bundle = new Bundle();
                                supportBundle = new Bundle();
                                supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
                                supportBundle.putString(Constant.SUPPORT_FRAGMENT_TAG, Constant.TAG_MEMBERS);
                                supportBundle.putString(Constant.SUPPORT_FRAGMENT, FragmentMember.class.getName());
                                Navigator.with(MainActivity.this)
                                        .build()
                                        .goTo(SupportActivity.class, supportBundle)
                                        .commit();
                                return false;
                            case R.id.menu_group:
                                bundle = new Bundle();
                                supportBundle = new Bundle();
                                supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
                                supportBundle.putString(Constant.SUPPORT_FRAGMENT, GroupsFragment.class.getName());
                                Navigator.with(MainActivity.this)
                                        .build()
                                        .goTo(SupportActivity.class, supportBundle)
                                        .commit();
                                return false;

                            case R.id.menu_my_profile:
                                Bundle bundle_id = new Bundle();
                                bundle_id.putString(ProfileFragment.ARGUMENT_PROFILE_ID, user.getId());
                                Navigator.with(MainActivity.this)
                                        .build()
                                        .goTo(Fragment.instantiate(MainActivity.this,
                                                ProfileFragment.class.getName()),
                                                bundle_id,
                                                R.id.container)
                                        .tag(Constant.TAG_MY_PROFILE)
                                        .addToBackStack()
                                        .add()
                                        .commit();
                                itemSelected = drawerItem.getIdentifier();

                                return false;

                            case R.id.menu_ess:
                                String tag = Constant.TAG_ESS + MonteOreFragment.class.getName();
                                Navigator.with(MainActivity.this)
                                        .build()
                                        .goTo(Fragment.instantiate(MainActivity.this,
                                                MonteOreFragment.class.getName()),
                                                R.id.container)
                                        .tag(tag)
                                        .addToBackStack()
                                        .add()
                                        .commit();
                                itemSelected = drawerItem.getIdentifier();
                                return false;

                            case R.id.menu_wmanager:
                                Utils.openWorkManagerApp(MainActivity.this, WORKMANAGER_PROCESS_NAME);
                                return false;

                            case R.id.menu_jabber:
                                onOpenChatIntent();
                                return false;

                            case R.id.menu_concorsi:
                                Navigator.with(MainActivity.this)
                                        .build()
                                        .goTo(ConcorsiActivity.class)
                                        .commit();
                                return false;

                            case R.id.menu_survey:
                                Navigator.with(MainActivity.this)
                                        .build()
                                        .goTo(SurveyActivity.class)
                                        .commit();
                                return false;
                        }
                        return false;
                    }
                })
                .withStickyFooterDivider(false)
                .withStickyFooterShadow(false)
                .withFooterDivider(false)
                .withStickyFooter(R.layout.include_footer_view)
                .withAccountHeader(accountHeader)
                .build();
    }


    @Override
    protected void setNavigationOnClickListener() {
    }

    @Override
    protected int getToolbarProjectId() {
        return R.id.toolbar;
    }

    boolean jobIsStart = false;

    private void lauchPeriodiUpdateJob() {

        Iterator<JobRequest> iterator = mJobManager.getAllJobRequests().iterator();
        while (iterator.hasNext()) {
            JobRequest _job = iterator.next();
            if (_job.getTag().equalsIgnoreCase(UpdateSchedulerJob.TAG_FULL_NAME)) {
                jobIsStart = true;
                break;
            }
        }

        if (!jobIsStart) {
            JobRequest job = new JobRequest.Builder(UpdateSchedulerJob.TAG_FULL_NAME)
                    .setPeriodic(UpdateSchedulerJob.PERIOD_MS)
                    .setRequiresDeviceIdle(false) //if true Job start ony when Device is in idle mode
                    .setPersisted(true)
                    .build();

            job.schedule();
            mJobManager.schedule(job);
        }
    }

    @Override
    public void onBackStackChanged() {
        String tag = Navigator.with(MainActivity.this).utils().getActualTag();
        switch (tag) {
            case Constant.TAG_HOME:
                setSubtitle("");
                if (drawer != null) drawer.setSelection(R.id.menu_home, false);
                itemSelected = R.id.menu_home;
                EventBus.getDefault().post(new CanReloadList());
                break;

            case Constant.TAG_MY_PROFILE:
                setSubtitle(R.string.mio_profilo_subtitle);
                if (drawer != null) drawer.setSelection(R.id.menu_my_profile, false);
                itemSelected = R.id.menu_my_profile;
                break;
        }

        //switch for setup theme application in different section

        if (tag.startsWith(Constant.TAG_ESS)) {
            getToolbar().setTitleTextColor(getResources().getColor(R.color.accent));
            getToolbar().setSubtitleTextColor(getResources().getColor(R.color.accent));
            Utils.setStatusBarColor(this, R.color.primary_ess);
            EventBus.getDefault().post(new BackStackChanged());

        } else {
            getToolbar().setTitleTextColor(getResources().getColor(R.color.primary));
            getToolbar().setSubtitleTextColor(getResources().getColor(R.color.primary));
            Utils.setStatusBarColor(this, R.color.primary);
        }
    }

    public void onOpenChatIntent() {
        String url = "xmpp:";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try{
            startActivity(i);
        }catch(ActivityNotFoundException ex){
            Log.d(TAG, "ActivityNotFoundException");
            showJabberNotFoundDialog();
        }
    }

    private void showJabberNotFoundDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.alert_jabber_non_trovato)
                .content(R.string.alert_jabber_non_trovato_msg)
                .positiveText(R.string.ok)
                .show();
    }

    @Subscribe
    public void onPasswordExpired(EventPasswordExpired event) {
        new MaterialDialog.Builder(this)
                .title(R.string.password_scaduta)
                .content(R.string.password_scaduta_content)
                .positiveText("OK")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SharedPreferenceAdapter.removeBasicAuthenticationToken(getApplicationContext());
                        SharedPreferenceAdapter.removeAppcidRegistered(getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }


    public static class BackStackChanged {
    }

    private int itemSelected = R.id.menu_home;

    private static final String WORKMANAGER_PROCESS_NAME = "com.acea.syclo.agentry.client.android";
};