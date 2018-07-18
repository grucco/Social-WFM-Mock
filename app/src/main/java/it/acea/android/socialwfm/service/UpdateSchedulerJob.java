package it.acea.android.socialwfm.service;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.evernote.android.job.Job;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import it.acea.android.socialwfm.BuildConfig;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.model.Notification;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.factory.SAPMobilePlatformFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;

/**
 * Created by fabio on 18/11/15.
 */
public class UpdateSchedulerJob extends Job {

    public static final int PERIOD_MS = 60000*15; // 15 minuti
    public static final String TAG_FULL_NAME = "it.acea.android.socialwfm.service.UpdateSchedulerJob";
    public static final String TAG = UpdateSchedulerJob.class.getSimpleName();
    public static final String ODL_RESULT = R.class.getPackage().getName() + ".REQUEST_PROCESSED";
    public static final String NOTIFICATION_RESULT = R.class.getPackage().getName() + ".NOTIFICATION_PROCESSED";

    public static final String RESPONSE_MESSAGE = UpdateSchedulerJob.class.getName() + ".RESPONSE_MSG";

    LocalBroadcastManager broadcaster = null;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {

        if (broadcaster == null) {
            broadcaster = LocalBroadcastManager.getInstance(getContext());
        }

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        android.util.Log.i("UpdateSchedulerJob_TAG", "JOB RUN AT [" + System.currentTimeMillis() + "] [" + format.format(new Date()) + "]");

        loadRequestOrder();

        updateNotificationNumber();

        return Result.SUCCESS;
    }

    public void sendResult(String result, String message) {
        Intent intent = new Intent(result);
        if (message != null) {
            intent.putExtra(RESPONSE_MESSAGE, message);
        }
        broadcaster.sendBroadcast(intent);
    }

    private void updateNotificationNumber() {

        if (!startNotificationUpdate) {
            startNotificationUpdate = true;

                HttpClientRequest.executeRequestGetNotification(getContext().getApplicationContext(), new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        try {
                            CheckErrorInJamResponse.check(e, result);
                            GsonBuilder builder = Utils.getRealmGsonBuilder();
                            Gson gson = builder.create();
                            Type listType = new TypeToken<List<Notification>>() {
                            }.getType();
                            List<Notification> notificationBeanList = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                            // Lascio solo le notifiche con eventtype gestiti
                            notificationBeanList = Utils.getCleanedNotifications(notificationBeanList);
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(notificationBeanList);
                            realm.commitTransaction();
                            int num = Realm.getDefaultInstance().where(Notification.class).equalTo("Read", false).findAll().size();
                            SharedPreferenceAdapter.setNumNotification(getContext().getApplicationContext(), num);
                            sendResult(NOTIFICATION_RESULT, "RESULT_OK");
                        } catch (Exception e1) {
                            Log.e(TAG, "updateNotificationNumber", e1);
                        } finally {
                            startNotificationUpdate = false;
                        }
                    }
                });
        }
    }


    private boolean startOdlUpdate = false;
    private boolean startNotificationUpdate = false;

    private void loadRequestOrder() {

        if (startOdlUpdate == false) {
            startOdlUpdate = true;

            List<Odl> odlList = SAPMobilePlatformFactory
                    .with(getContext().getApplicationContext())
                    .getActualOdlList();

            if (odlList == null || (odlList != null && odlList.isEmpty())) {
                startOdlUpdate = false;
                return;
            }

            SAPMobilePlatformFactory
                    .with(getContext().getApplicationContext())
                    .test(BuildConfig.DEV_MODE)
                    .sourceDropbox(BuildConfig.DROPBOX_SOURCE)
                    .withAuthorization()
                    .retrieveOdl(new SAPMobilePlatformFactory.CallBack() {

                        @Override
                        public void onFailure(String message) {
                            sendResult(ODL_RESULT, "RESULT_KO");
                            startOdlUpdate = false;
                            return;
                        }

                        @Override
                        public void onSuccess(List<Odl> odlList) {
                            sendResult(ODL_RESULT, "RESULT_OK");
                            startOdlUpdate = false;
                            return;
                        }

                        @Override
                        public void onFailure() {
                            sendResult(ODL_RESULT, "RESULT_KO");
                            startOdlUpdate = false;
                            return;
                        }
                    });
        }
    }


};