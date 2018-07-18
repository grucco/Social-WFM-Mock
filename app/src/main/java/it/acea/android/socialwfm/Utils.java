package it.acea.android.socialwfm;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.primitives.Doubles;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.fingerlinks.mobile.android.utils.Log;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.staticmap.Callback;
import dmax.staticmap.Config;
import dmax.staticmap.Marker;
import dmax.staticmap.StaticMap;
import io.realm.Realm;
import io.realm.RealmObject;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.SocialWFMApplication;
import it.acea.android.socialwfm.app.model.DeltaTokenUrl;
import it.acea.android.socialwfm.app.model.Notification;
import it.acea.android.socialwfm.app.model.odl.Plant;
import it.acea.android.socialwfm.factory.GoogleMapsRequestFactory;

import android.os.Build;

/**
 * @author Andrea Cappelli
 */
@SuppressWarnings("deprecation")
public class Utils {

    public static boolean isEmptyList(List list) {
        return (list == null || list.size() == 0);
    }

    /**
     * Open another app.
     *
     * @param context     current Context, like Activity, App, or Service
     * @param packageName the full package name of the app to open
     * @return true if likely successful, false if unsuccessful
     */
    public static boolean openWorkManagerApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new PackageManager.NameNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void storeDeltaTokenInfo(String url) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        DeltaTokenUrl d = new DeltaTokenUrl();
        d.setDeltaUrl(url);
        d.setId(1);
        realm.copyToRealmOrUpdate(d);
        realm.commitTransaction();
    }

    public static GsonBuilder getRealmGsonBuilder() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        //builder.registerTypeAdapter(BaseResult.class, new );
        builder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });

        return builder;
    }

    /**
     * @param context
     * @param serviceClass
     * @return
     */
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void executeGoogleMaps(Context context, String address) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public static void executeGoogleMaps(Context context, String lat, String lon) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public static String cleanJson(JsonObject jsonObject) {
        String clean = jsonObject.toString().replaceAll("\\[]", "null");
        Log.e(TAG, "clean json [" + clean + "]");
        return clean;
    }

    public static String cleanNullableValue(String toClean) {
        return TextUtils.isEmpty(toClean) ? "--" : toClean;
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String formattaDataAnno(String data) throws Exception {
        long dataParse = Long.parseLong(data.replace("/Date(", "").replace(")/", "").trim());
        Date date = new Date(dataParse);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); //, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    public static Date getDataMesePrecedente(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    public static String formattaData(Date data) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); //, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(data);
    }

    public static String formattaData(String data) {
        long dataParse = Long.parseLong(data.replace("/Date(", "").replace(")/", "").trim());
        Date date = new Date(dataParse);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM - HH:mm:ss"); //, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    private static final SimpleDateFormat pthmsFormat = new SimpleDateFormat("'PT'HH'H'mm'M'ss'S'", Locale.ITALY);

    // Ritorna l'oggetto Date che rappresenta l'ora oppure 00:00:00
    public static Date getTimePTHMS(String timePTHMS) {
        try {
            return pthmsFormat.parse(timePTHMS);
        } catch (ParseException e) {
            e.printStackTrace();
            Calendar zero = Calendar.getInstance();
            zero.set(Calendar.HOUR_OF_DAY, 0);
            zero.set(Calendar.MINUTE, 0);
            zero.set(Calendar.SECOND, 0);
            return zero.getTime();
        }
    }

    public static String timeToPTHMS(Date time) {
        return pthmsFormat.format(time);
    }

    public static String formatTimeToHHmm(Date date) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        return sdf2.format(date);
    }

    public static String formatTimePTHMS(String timePTHMS) {
        return formatTimeToHHmm(getTimePTHMS(timePTHMS));
    }

    public static Date getDateFromString(String data) {
        long dataParse = Long.parseLong(data.replace("/Date(", "").replace(")/", "").trim());
        return new Date(dataParse);
    }

    public static int formattaDataHour(String data) {
        long dataParse = Long.parseLong(data.replace("/Date(", "").replace(")/", "").trim());
        Date date = new Date(dataParse);
        SimpleDateFormat format = new SimpleDateFormat("HH"); //, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Integer.parseInt(format.format(date));
    }

    public static String dataRelativa(Context context, long date) {
        DateTime start = new DateTime(date);
        DateTime end = new DateTime(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        int seconds = Seconds.secondsBetween(start, end).getSeconds();
        int minutes = Minutes.minutesBetween(start, end).getMinutes();
        int hours = Hours.hoursBetween(start, end).getHours();
        int days = Days.daysBetween(start, end).getDays();
        if (days > 1) {
            return format.format(new Date(date));
        }
        if (hours > 0) {
            if (hours == 1) {
                return context.getString(R.string.ora_fa, hours);
            } else {
                return context.getString(R.string.ore_fa, hours);
            }
        }
        if (minutes > 0) {
            if (minutes == 1) {
                return context.getString(R.string.minuto_fa, minutes);
            } else {
                return context.getString(R.string.minuti_fa, minutes);
            }
        }
        if (seconds > 0) {
            if (seconds == 1) {
                return context.getString(R.string.secondo_fa, seconds);
            } else {
                return context.getString(R.string.secondi_fa, seconds);
            }
        }
        return context.getString(R.string.un_attimo_fa);
    }

    public static Drawable avatarPlaceholder(Context context, String firstName, String lastName) {
        String label = cleanNullableValue(firstName).substring(0, 1) + cleanNullableValue(lastName).substring(0, 1);
        return TextDrawable.builder()
                .beginConfig()
                .textColor(context.getResources().getColor(R.color.accent))
                .bold()
                .endConfig()
                .buildRect(label, Color.WHITE);
    }

    public static JsonObject parseJson(Object object) {
        return parseJson(object, false);
    }

    public static JsonObject parseJson(Object object, boolean showLog) {
        try {
            Gson gson = getRealmGsonBuilder().create();
            JSONObject jsonObject = new JSONObject(gson.toJson(object));
            if (showLog) {
                Log.d(TAG, jsonObject.toString());
            }
            return (new JsonParser()).parse(jsonObject.toString()).getAsJsonObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isZeroCoordinates(String lat, String lng) {
        try {
            int lt = (int) Double.parseDouble(lat);
            int ln = (int) Double.parseDouble(lng);
            return (lt == 0 && ln == 0);
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static void printStaticMap(Context ctx, String lat, String lng, final ImageView google_map_preview) {
        if (!isZeroCoordinates(lat, lng)) {
            new GoogleMapsRequestFactory().with(ctx)
                    .loadInto(google_map_preview)
                    .setLat(lat)
                    .setLng(lng)
                    .setMarkerSize(GoogleMapsRequestFactory.MarkerSize.mid)
                    .setScale(2)
                    .setZoomLevel(Constant.STATIC_MAP_ZOOM_LEVEL)
                    .setSize(ctx.getResources().getDimensionPixelSize(R.dimen.static_map_width))
                    .setApiKey(ctx.getResources().getString(R.string.google_map_api_key))
                    .build()
                    .load();
        }
    }

    public static boolean isGeoLocationComplete(Plant p) {
        return ((!TextUtils.isEmpty(p.getLatitude()) &&
                !p.getLatitude().startsWith("*")) && (!TextUtils.isEmpty(p.getLongitude())
                && !p.getLongitude().startsWith("*")));
    }

    public static LatLng retrievePosition(Plant p) {
        LatLng position = null;
        if (isGeoLocationComplete(p)) {
            position = new LatLng(Double.parseDouble(p.getLatitude()),
                    Double.parseDouble(p.getLongitude()));
        } else {
            /**
             * Reverse geocoding information
             */
            try {
                List<Address> lists = new Geocoder(SocialWFMApplication.instance,
                        Locale.getDefault()).getFromLocationName(
                        p.getAddressComplete(), 1);
                if (lists != null && !lists.isEmpty()) {
                    Address address = lists.get(0);
                    position = new LatLng(
                            address.getLatitude(),
                            address.getLongitude());
                }
            } catch (Exception _ex) {
                position = null;
            }
        }
        return position;
    }

    public static void noActive(Context context) {
        Toast.makeText(context, context.getString(R.string.funzione_non_attiva), Toast.LENGTH_SHORT).show();
    }

    public static void errorToast(Context context) {
        Toast.makeText(context, context.getString(R.string.error_message_network), Toast.LENGTH_SHORT).show();
    }

    public static File getOutgoingImageFile(Context context) throws IOException {
        String iconsStoragePath = Environment.getExternalStorageDirectory().getPath() + "/" + context.getString(R.string.app_name) + "/";
        File sdIconStorageDir = new File(iconsStoragePath);
        sdIconStorageDir.mkdirs();
        File f = new File(sdIconStorageDir, "temp" + System.currentTimeMillis() + ".jpg");
        f.createNewFile();
        return f;
    }

    public static File saveImage(Context context, Bitmap bitmap) {
        Bitmap originalImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
        String iconsStoragePath = Environment.getExternalStorageDirectory().getPath() + "/" + context.getString(R.string.app_name) + "/";
        File sdIconStorageDir = new File(iconsStoragePath);
        sdIconStorageDir.mkdirs();
        try {
            String filePath = sdIconStorageDir + "/temp" + System.currentTimeMillis() + ".jpg";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            originalImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

            return new File(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Ripulisce una lista di notifiche, eliminando quelle con eventType non gestito. Effettua sideffect
     */
    public static List<Notification> getCleanedNotifications(List<Notification> notifications) {
        String[] eventTypes = {"AutoGroupMember", "InviteToGroup", "Like",
                "ReplyInFeed", "MentionInFeed", "PostOnWall"};
        ArrayList<Notification> result = new ArrayList<Notification>();
        for (Notification n : notifications) {
            boolean add = false;
            for (String type : eventTypes) {
                if (n.getEventType().equals(type)) {
                    add = true;
                    break;
                }
            }//@end-for
            if (add) {
                result.add(n);
            }
        }//@end-for
        return result;
    }

    // https://developer.android.com/guide/topics/providers/document-provider.html
    // http://codetheory.in/android-pick-select-image-from-gallery-with-intents/

    @Nullable
    public static String getPathFromMediaUri(Context context, Uri uri) {
        String picturePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(projection[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return picturePath;
    }

    public static Date cleanDateField(String datetoClean) {
        long l = Long.parseLong(datetoClean.replace("/Date(", "").replace(")/", "").trim());
        return new Date(l);
    }

    public static String dateToEdmDatetime(Date date) {
        return String.format("/Date(%s)/", date.getTime());
    }

    public static boolean cleanBooleanField(String booleanToClean) {
        return booleanToClean.equals("X");
    }

    public static String booleanToABAP(boolean value) {
        return value ? "X" : "";
    }

    public static int getHourFromTimeField(String time) {
        return Integer.parseInt(time.substring(2, 4));
    }


    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);


    public static String dateFormatUTC(Date date) {
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
        d.setTimeZone(TimeZone.getTimeZone("UTC"));
        return d.format(date);
    }

    public static String dateFormat(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String dateFormatWithoutSlashes(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyy", Locale.ITALY);
        return sdf.format(d);
    }


    public static final DateTimeFormatter ISODATE_FORMAT = ISODateTimeFormat.dateHourMinuteSecond();

    public static String dateToISOFormat(Date date) {
        return ISODATE_FORMAT.print(date.getTime());
    }

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final static String TAG = Utils.class.getName();
    private static final String PWD_PATTERN = "^[a-z0-9._-]{4,255}$";
    private static final String USERNAME_PATTERN = "^[a-z0-9._-]{4,255}$";

/*  Timbrature
    ASS (Assenza)
    FST (Festivo)
    ERR (Errore)
    COR (Correzione)
    LAV (Al lavoro)
    NLA (Giorno non lavorativo)

    Giustificativi
    FST  (Festivo)
    INV (Inviato)
    ASS (Assente)
    CNC  (Canc. Richiesto)
    NLA (Giorno non lavorativo)
    INS (Più inserimenti)
  */

    public static enum ESS_COLORS {
        ASS(R.color.legenda_assenza),
        FST(R.color.legenda_festivo),
        ERR(R.color.legenda_errore),
        COR(R.color.legenda_correzione),
        LAV(R.color.legenda_al_lavoro),
        NLA(R.color.legenda_giorno_non_lavorativo),
        INV(R.color.legenda_inviato),
        CNC(R.color.legenda_canc_richiesto),
        INS(R.color.legenda_piu_inserimenti);

        private int colorRes;

        private ESS_COLORS(int colorRes) {
            this.colorRes = colorRes;
        }

        public int getColorRes() {
            return this.colorRes;
        }
    }

    public static void launchOpenPdfIntent(Context context, File file) {
        Intent openFileIntent = new Intent();
        openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        openFileIntent.setAction(android.content.Intent.ACTION_VIEW);


        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext()
                .getPackageName() + ".it.acea.android.provider", file);
        String mime = "application/pdf";
        openFileIntent.setDataAndType(uri, mime);
        context.startActivity(openFileIntent);
    }

    public static List<String> getRegexMatches(String patternString, String input) {
        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile(patternString).matcher(input);
        while (m.find()) {
            allMatches.add(m.group());
        }
        return allMatches;
    }

    public static void cleanMatchFromResultSet(String pattern, List<String> input) {
        int index = 0;
        int indexToRemove = -1;
        boolean match = false;
        for (String s : input) {
            if (getRegexMatches(pattern, s).size() > 0) {
                indexToRemove = index;
                match = true;
                break;
            }
            index++;
        }
        if (match) {
            input.remove(indexToRemove);
        }
    }

    public static int getStatusBarHeight(Activity context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setStatusBarColor(Activity context, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = context.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = getStatusBarHeight(context);

            View view = new View(context);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(context.getResources().getColor(color));
        }
    }
}