package it.acea.android.socialwfm.app.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.Log;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import it.acea.android.socialwfm.BuildConfig;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.bean.MapBean;
import it.acea.android.socialwfm.app.events.CanReloadList;
import it.acea.android.socialwfm.app.model.Notification;
//import it.acea.android.socialwfm.app.model.odl.MockOdl;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.app.model.odl.Plant;
import it.acea.android.socialwfm.app.ui.SearchActivity;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentMyWorkDetail;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentMyWorkList;
import it.acea.android.socialwfm.app.ui.intro.IntroHome;
import it.acea.android.socialwfm.factory.SAPMobilePlatformFactory;
import it.acea.android.socialwfm.service.UpdateSchedulerJob;

/**
 * Created by fabio on 19/10/15.
 */
public class FragmentMyWorkContainer extends BaseFragment implements
        FragmentMyWorkList.OnOdlItemClickListener,
        ClusterManager.OnClusterItemClickListener<MapBean>,
        ClusterManager.OnClusterClickListener<MapBean>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        FragmentMyWorkList.OnitemsLoadingFinish {

    interface onDbTaskCompleted {
        void onComplete(List<MapBean> mapItems);
        void onError();
    }

    FragmentMyWorkDetail fragment_mywork_detail;

    public static Drawer drawer;

    private List<Odl> odlList;
    //private List<MockOdl> modlList;
    private boolean previousNetworkError=false;

    //Layout Box to show all-size map
    @Bind(R.id.mywork_detail_box)
    View mywork_detail_box;

    @Bind(R.id.progressLayout)
    View progress_layout;

    @Bind(R.id.fragment_container_box)
    View fragment_container_box;

    @Bind(R.id.left_shadow_box)
    View left_shadow_box;

    @Bind(R.id.fab_fullscreen)
    ToggleButton fab_fullscreen;

    @Bind(R.id.close_box)
    View closeBoxButton;




    BroadcastReceiver receiver;

    MaterialDialog progress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(UpdateSchedulerJob.RESPONSE_MESSAGE);
                Log.e(TAG, "UPDATE RESULT DATA FROM JOB [" + s + "]");
                if (s.equalsIgnoreCase("RESULT_OK")) {
                    try {
                        getActivity().invalidateOptionsMenu();
                        //if(previousNetworkError){
                           // previousNetworkError=false;
                        initMarkers();
                        //}
                    } catch (Exception _ex) {
                        Log.e(TAG, "controlled exception");
                    }
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "ONSTART--->");
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver((receiver),
                        new IntentFilter(UpdateSchedulerJob.NOTIFICATION_RESULT)
                );
    }

    @Override
    public void onStop() {
        Log.e(TAG, "ONSTOP--->");
        LocalBroadcastManager.getInstance(getActivity()).
                unregisterReceiver(receiver);
        super.onStop();
    }



    private void prepareNotificationDrawer(Bundle savedInstanceState, View view) {
        drawer = new DrawerBuilder()
                .withActivity(getActivity())
                .withTranslucentStatusBarProgrammatically(false)
                .withTranslucentStatusBar(false)
                .withDisplayBelowStatusBar(true)
                .withDrawerGravity(Gravity.RIGHT)
                .withDrawerWidthRes(R.dimen.drawer_notification_width)
                .withCustomView(view)
                .withSavedInstance(savedInstanceState)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        try {
                            getActivity().supportInvalidateOptionsMenu();
                        } catch (Exception _ex) {
                            Log.e(TAG, _ex);
                        }
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .build();

        Toolbar drawerToolbar = (Toolbar) view.findViewById(R.id.drawer_toolbar);
        drawerToolbar.setTitle(R.string.notifiche);
        drawerToolbar.inflateMenu(R.menu.notification_menu);
        drawerToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.close_notification:
                        drawer.closeDrawer();
                        break;
                    case R.id.refresh_notification:
                        Navigator.with(getActivity())
                                .build()
                                .goTo(Fragment.instantiate(getActivity(), NotificationFragment.class.getName()), R.id.drawer_container)
                                .add()
                                .commit();
                        break;
                }
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mywork_container, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        try {
            menu.clear();
        } catch (Exception _ex) {
        }



        inflater.inflate(R.menu.toolbar_menu, menu);
        int badgeCount = -1;
        try {
            badgeCount = Realm.getDefaultInstance()
                    .where(Notification.class)
                    .equalTo("Read", false)
                    .findAll().size();
            Log.e(TAG, "NEW NOTIFICATION COUNT [" + badgeCount + "]");
            SharedPreferenceAdapter.setNumNotification(getActivity(), badgeCount);
        } catch (Exception _ex) {
            badgeCount = SharedPreferenceAdapter.getNumNotification(getActivity());
        }
        if (badgeCount > 0) {
            ActionItemBadge.
                    update(getActivity(), menu.findItem(R.id.menu_notify),
                            getActivity().getResources().getDrawable(R.drawable.ic_ab_notification), ActionItemBadge.BadgeStyles.RED,
                            badgeCount);
        }
        if (BuildConfig.DEV_MODE) {
            menu.findItem(R.id.menu_refresh_data)
                    .setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_notify:
                if (drawer != null && !drawer.isDrawerOpen()) {
                    drawer.openDrawer();
                }
                return false;
            case R.id.menu_settings:
                Bundle bundle = new Bundle();
                Bundle supportBundle = new Bundle();
                supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
                supportBundle.putString(Constant.SUPPORT_FRAGMENT, SettingsFragment.class.getName());
                Navigator.with(getActivity())
                        .build()
                        .goTo(SupportActivity.class, supportBundle)
                        .commit();
                return false;
            case R.id.menu_refresh_data:

                SAPMobilePlatformFactory
                        .with(getActivity())
                        .test(BuildConfig.DEV_MODE)
                        .sourceDropbox(false)
                        .withAuthorization()
                        .retrieveOdl(new SAPMobilePlatformFactory.CallBack() {

                            @Override
                            public void onFailure(String message) {
                                Log.e(TAG, "onFailure -----> [" + message + "]");
                                //isStartedRequest = false;
                                if (message.equalsIgnoreCase("CID_ERRATO")) {
                                    SharedPreferenceAdapter.setAppCidRegistered(getActivity(), null);
                                    //loadRequestOrder(context);
                                    return;
                                }
                            }

                            @Override
                            public void onSuccess(List<Odl> odlList) {
                                Log.e(TAG, "ODL-SIZE [" + ((odlList != null) ? odlList.size() : 0) + "]");
                                //isStartedRequest = false;
                            }

                            @Override
                            public void onFailure() {
                                Log.e(TAG, "onFailure ----->");
                                //isStartedRequest = false;
                            }
                        });

                return false;
            case R.id.menu_search:
                Navigator.with(getActivity())
                        .build()
                        .goTo(SearchActivity.class)
                        .animation()
                        .commit();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOdlItemSelected(String id) {

        progress_layout.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(id)) {
            mywork_detail_box.setVisibility(View.GONE);
            progress_layout.setVisibility(View.GONE);
            return;
        }

        if (fragment_mywork_detail != null) {

            if (mywork_detail_box.getVisibility() == View.GONE) {
                mywork_detail_box.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_slide_in));
                mywork_detail_box.setVisibility(View.VISIBLE);
                fab_fullscreen.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(id)) {
                fragment_mywork_detail.loadOdlData(id);
            }
        }
        try {
            if (!TextUtils.isEmpty(id)) {
                LatLng latLng = latLngHashMap.get(id);
                if (latLng != null) cameraPosition(googleMap, latLng, 16);
            }
        } catch (Exception _ex) {
            android.util.Log.e(TAG, "onOdlItemSelected", _ex);
        } finally {
            progress_layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setNavigationOnClickListener() {
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return true;
        }
        return false;
    }

    @OnClick(R.id.fab_fullscreen)
    public void clickFabFullscreen() {
        changeMapSize();
    }

    @OnClick(R.id.close_box)
    public void clickFabCloseBox() {
        if (mywork_detail_box.getVisibility() == View.VISIBLE) {
            mywork_detail_box.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_slide_out));
            mywork_detail_box.setVisibility(View.GONE);
            fab_fullscreen.setVisibility(View.VISIBLE);
        }
    }

    private void changeMapSize() {
        int visibility = (fragment_container_box.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
        switch (visibility) {
            case View.VISIBLE:
                fragment_container_box.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left));
                if (mywork_detail_box.getVisibility() == View.GONE)
                    mywork_detail_box.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_slide_in));
                fab_fullscreen.setVisibility(View.GONE);
                break;
            case View.GONE:
                fragment_container_box.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left));
                if (mywork_detail_box.getVisibility() == View.VISIBLE)
                    mywork_detail_box.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_slide_out));
                fab_fullscreen.setVisibility(View.VISIBLE);
                break;
        }
        mywork_detail_box.setVisibility(visibility);
        fragment_container_box.setVisibility(visibility);
        left_shadow_box.setVisibility(visibility);
    }


    @Override
    public void onPause() {
        super.onPause();
        dismissProgress();//evita window leaked se l'activity esce con il dialog attivo
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotification();
        //Lo riceverà FragmentMyWorkList
        EventBus.getDefault().post(new CanReloadList());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View drawer = LayoutInflater.from(getActivity()).inflate(R.layout.drawer_notification, null);
        prepareNotificationDrawer(savedInstanceState, drawer);
        fragment_mywork_detail = (FragmentMyWorkDetail) android.support.v4.app.Fragment
                .instantiate(getActivity(), FragmentMyWorkDetail.class.getName());

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_mywork_detail,
                        fragment_mywork_detail, FragmentMyWorkDetail.class.getName())
                .commit();

        Navigator
                .with(getActivity())
                .build()
                .goTo(Fragment.instantiate(getActivity(),
                        NotificationFragment.class.getName()),
                        R.id.drawer_container)
                .add()
                .commit();

        initGoogleMap();
    }

    private void updateNotification() {
        getActivity().invalidateOptionsMenu();
    }

    /********************* MAP CONFIGURATION ********************************/

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private ClusterManager<MapBean> clusterManager;


    private void initGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gmap) {
                googleMap = gmap;
                setUpMap();
                setUpGoogleApiClient();
                initMarkers();
            }
        });
    }

    private void setUpMap() {
        clusterManager = new ClusterManager<>(getActivity(), googleMap);
        clusterManager.setRenderer(new MapRenderer(getActivity(), googleMap, clusterManager));
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setOnClusterClickListener(this);

        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setTrafficEnabled(true);
        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setOnMarkerClickListener(clusterManager);
    }

    private void setUpGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
            createLocationRequest();

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult locationSettingsResult) {
                    Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            startLocationUpdates();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(getActivity(), CODE_GPS_DIALOG);
                            } catch (IntentSender.SendIntentException ignored) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            startLocationUpdates();
                            break;
                    }
                }
            });
        }
    }

    private void cameraPosition(GoogleMap googleMap, LatLng latLng) {
        cameraPosition(googleMap, latLng, 13);
    }

    private void cameraPosition(GoogleMap googleMap, LatLng latLng, int zoom) {
        CameraUpdate updatePos = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        googleMap.animateCamera(updatePos);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private Location startLocation = null;
    private Location currentLocation = null;

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if(startLocation == null) {
            Log.e(TAG, "LOCATION UPDATE");
            startLocation = location;
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_GPS_DIALOG) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    startLocationUpdates();
                    Log.i(TAG, "User agreed to make required location settings changes.");
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(TAG, "User chose not to make required location settings changes.");
                    break;
            }
        }
    }

    private int CODE_GPS_DIALOG = 1000;

    @Override
    public boolean onClusterItemClick(MapBean bean) {
        /**
         * Fix animation
         */
        onOdlItemSelected(bean.getAufnr());
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<MapBean> cluster) {
        int actualZoom = (int) googleMap.getCameraPosition().zoom;
        LatLng latLng = cluster.getPosition();
        if (actualZoom < 11) {
            cameraPosition(googleMap, latLng, 11);
            return true;
        }
        if (actualZoom < googleMap.getMaxZoomLevel()) {
            actualZoom++;
            actualZoom++;
        }
        cameraPosition(googleMap, latLng, actualZoom);
        return true;
    }

    @Override
    public void items(List<Odl> odlBeanList) {
        this.odlList = odlBeanList;
        initMarkers();
        //Quando ho nuovi elementi dalla lista nascondo il dettaglio dell'ordine
        //correntemente visualizzato. L'ordine relativo potrebbe infatti essere stato rimosso
        //dal DB locale
        hideOdlDetailFragment();
    }

    /*@Override
    public void itemss(List<MockOdl> odlBeanList) {
        this.modlList = odlBeanList;
        initMarkers();
        //Quando ho nuovi elementi dalla lista nascondo il dettaglio dell'ordine
        //correntemente visualizzato. L'ordine relativo potrebbe infatti essere stato rimosso
        //dal DB locale
        hideOdlDetailFragment();
    }*/


    private void hideOdlDetailFragment(){
        this.fragment_mywork_detail.hideContentData();
    }

    private HashMap<String, LatLng> latLngHashMap;

    private class MapRenderer extends DefaultClusterRenderer<MapBean> {

        public MapRenderer(Context context, GoogleMap map, ClusterManager<MapBean> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MapBean item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

            TextView t = new TextView(getActivity());
            t.setBackgroundResource(R.drawable.ic_marker);
            t.setText(String.valueOf(item.getMarkerPosition()));
            if(item.getMarkerPosition() < 10) {
                t.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.font_size_07_sp));
            } else {
                t.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.font_size_06_sp));
            }
            t.setTextColor(Color.parseColor("#212121"));
            t.setTypeface(null, Typeface.BOLD);
            t.setGravity(Gravity.CENTER);

            //NinePatchDrawable patch = getNinePatch();
            //Bitmap bitmap = textAsBitmap(String.valueOf(item.getMarkerPosition()));

            IconGenerator iconGenerator = new IconGenerator(getActivity());
            iconGenerator.setBackground(null);
            iconGenerator.setContentView(t);
            //iconGenerator.setStyle(R.style.MarkerStyleTextLabel);

            Bitmap bitmap = iconGenerator.makeIcon(); //String.valueOf(item.getMarkerPosition()));
            BitmapDescriptor bmpMarker = BitmapDescriptorFactory.fromBitmap(bitmap);

            markerOptions.flat(false);
            markerOptions.rotation(0);
            markerOptions.icon(bmpMarker);
        }

        private NinePatchDrawable getNinePatch() {
            NinePatchDrawable ninepatch = null;
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker);
            if(image.getNinePatchChunk() != null) {
                byte[] chunk = image.getNinePatchChunk();
                Rect paddingRectangle = new Rect(0, 0, 0, 0);
                ninepatch = new NinePatchDrawable(getActivity().getResources(), image, chunk, paddingRectangle, null);
            }
            //int sdk = android.os.Build.VERSION.SDK_INT;
            //if(sdk<android.os.Build.VERSION_CODES.JELLY_BEAN) {
            //linLayout.setBackgroundDrawable(ninepatch);
            //} else {
            //linLayout.setBackground(ninepatch);
            //}
            return ninepatch;
        }

        public Bitmap textAsBitmap(String text) {
            Paint paint = new Paint();
            paint.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.font_size_08_sp));
            paint.setColor(Color.parseColor("#212121"));
            paint.setTextAlign(Paint.Align.CENTER);
            float baseline = -paint.ascent(); // ascent() is negative
            int width = (int) (paint.measureText(text) + 0.5f); // round
            int height = (int) (baseline + paint.descent() + 0.5f);
            Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);
            canvas.drawText(text, 0, baseline, paint);
            return image;
        }
    }

    /**
     * @author: Andrea e Nicola
     * Inizializza i markers sulla mappa ma prima si assicura che la mappa non sia null
     **/
    private synchronized void initMarkers(){
        // Se la mappa è inizializzata e ci sono dati disponibili che ci sono stati notificati in precedenza
        if(clusterManager!=null && odlList!=null){
                clusterManager.clearItems();
                showProgress();
                doInBackground(new onDbTaskCompleted() {
                    @Override
                    public void onComplete(final List<MapBean> mapItems) {
                        final List<MapBean> mapElements = mapItems;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgress();
                                for (MapBean bean:mapElements){
                                    clusterManager.addItem(bean);
                                }
                                clusterManager.cluster();//visualizza i marker aggiunti
                                if(mapItems!=null && mapItems.size()>0 && mapItems.get(0)!=null){
                                    // Mostro all'utente l'intro
                                    new IntroHome(getActivity(),fragment_container_box,fragment_mywork_detail.getContentData(),
                                            closeBoxButton,fab_fullscreen).start();
                                }
                            }
                        });
                    }
                    @Override
                    public void onError() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                previousNetworkError=true;
                                dismissProgress();
                                showNoConnectionialog();
                            }
                        });
                    }
                });
        }
    }

    //Esegue l'accesso ai record del database in background per non bloccare la grafica
    //al termine richiama una callback su handler
    private void doInBackground(final onDbTaskCompleted handler){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                odlList=SAPMobilePlatformFactory.with(getContext()).getActualOdlList();
                latLngHashMap = new HashMap<>();
                latLngHashMap.clear();
                ArrayList<MapBean> beans= new ArrayList<MapBean>() ;

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();

                Odl first_odl = odlList.get(0);
                for (int i = 0; i < odlList.size(); i++) {
                    MapBean bean = new MapBean();
                    Odl currentOdl = odlList.get(i);
                    if(i==1 || i==2 || i==4) {
                        currentOdl.getPlant().setUbicazione(first_odl.getPlant().getUbicazione());
                        currentOdl.getPlant().setRuoloutenza(first_odl.getPlant().getRuoloutenza());
                        currentOdl.getPlant().setAnlage(first_odl.getPlant().getAnlage());
                        if(i==4) {
                            currentOdl.setCustomer(first_odl.getCustomer());
                            currentOdl.getPlant().setLatitude(first_odl.getPlant().getLatitude());
                            currentOdl.getPlant().setLongitude(first_odl.getPlant().getLongitude());
                            currentOdl.getPlant().setLocalita(first_odl.getPlant().getLocalita());
                            currentOdl.getPlant().setAddressComplete(first_odl.getPlant().getAddressComplete());
                            currentOdl.getPlant().setCivico(first_odl.getPlant().getCivico());
                            currentOdl.getPlant().setCap(first_odl.getPlant().getCap());
                            currentOdl.getPlant().setVia(first_odl.getPlant().getVia());
                            currentOdl.getPlant().setProvincia(first_odl.getPlant().getProvincia());
                        } else {
                            currentOdl.getCustomer().setCondcontract(first_odl.getCustomer().getCondcontract());
                            currentOdl.getCustomer().setRuoloutenza(first_odl.getCustomer().getRuoloutenza());
                            currentOdl.getCustomer().setTariftype(first_odl.getCustomer().getTariftype());
                            currentOdl.getCustomer().setImpmor(first_odl.getCustomer().getImpmor());
                            currentOdl.getCustomer().setImpscad(first_odl.getCustomer().getImpscad());
                        }
                    }
                    Plant currentPlant = currentOdl.getPlant();
                    bean.setAufnr(currentOdl.getAufnr());
                    bean.setLatitude(currentPlant.getLatitude());
                    bean.setLongitude(currentPlant.getLongitude());
                    bean.setAddress(currentPlant.getAddressComplete());
                    bean.setMarkerPosition(i + 1);
                    if(!Utils.checkInternetConnection(getContext())){
                        handler.onError();
                        return;
                    }

                    bean.initPositionAndAddress();//Può fare una chiamata di rete per reverse geocoding
                    LatLng position = bean.getPosition();

                    currentOdl.getPlant().setLatitude(position.latitude+"");
                    currentOdl.getPlant().setLongitude(position.longitude+"");
                    currentOdl.getPlant().setAddressComplete(bean.getAddress());

                    latLngHashMap.put(bean.getAufnr(), position);

                    if (!bean.hasDefaultLatLong()) {
                        beans.add(bean);
                    }
                }
                realm.commitTransaction();
                handler.onComplete(beans);
            }
        });
    }


    private void showProgress(){
       if(progress==null){
           progress = buildProgressDialog();
       }
        progress.show();
    }

    private void dismissProgress(){
       if(progress!=null) progress.dismiss();
    }

    private MaterialDialog buildProgressDialog(){
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(R.string.caricamento_punti_mappa)
                .content(R.string.attendere)
                .progress(true, 0)
                .cancelable(false)
                .build();
        return dialog;
    }

    private void showNoConnectionialog(){
        new MaterialDialog.Builder(getActivity())
                .title(R.string.preference_logout_popup_title)
                .content(R.string.error_message_network)
                .iconRes(android.R.drawable.ic_dialog_alert)
                .build()
                .show();
    }

    private final static String TAG = FragmentMyWorkContainer.class.getName();

}