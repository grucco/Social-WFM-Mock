package org.fingerlinks.mobile.android.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AreaMobile
 * Date Aug 1, 2012
 *
 * @author Andrea Tortorella (andreatortorella@areamobile.eu)
 */
@SuppressWarnings("deprecation")
public class CodeUtils {

    public final static String INSPECT = "INSPECT";

    public static final int COMPRESSION_QUALITY = 85;
    public final static UnreachableCode __unreachable__ = new UnreachableCode();
    private final static long ticks = 621355968000000000L;
    private final static String HASH_ALGORITHM_MD5 = "MD5";
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    /*@TargetApi(11)
    public static void enableStrictMode() {
        if (CodeUtils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (CodeUtils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                vmPolicyBuilder
                    .setClassInstanceLimit(PostcardActivity.class, 1);
                        //.setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }*/
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final static String TAG = CodeUtils.class.getName();

    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static <T> WeakReference<T> weak(T ref) {
        if (ref == null) {
            throw new IllegalArgumentException("ref cannot be null");
        }
        return new WeakReference<T>(ref);
    }

    public static <T> T ensure_cast(Class<T> clazz, Object o) {
        try {
            @SuppressWarnings("unchecked")
            T ret = (T) o;
            return ret;
        } catch (ClassCastException e) {
            ClassCastException ex = new ClassCastException(o + " (" + o.getClass().getName() + ") should implement " + clazz.getName());
            ex.initCause(e);
            throw ex;
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * <p>Checks if the field isn't null and length of the field is greater
     * than zero not including whitespace.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if blank or null.
     */
    public static boolean isBlankOrNull(String value) {
        return ((value == null) || (((value.trim().length() == 0) || value.equalsIgnoreCase("null"))));
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex hex for validation
     * @return true valid hex, false invalid hex
     */
    public static boolean isEmailValid(final String hex) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    /**
     * @param mContext
     * @return
     */
    @TargetApi(12)
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context mContext) {
        int width = 0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        } else {
            width = display.getWidth();  // deprecated
        }
        return width;
    }

    /**
     * @param mContext
     * @return
     */
    @TargetApi(12)
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context mContext) {
        int height = 0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        } else {
            height = display.getHeight();  // deprecated
        }
        return height;
    }

    /**
     * Convert long to Tick
     *
     * @return
     */
    public static long getDateTimeInTick() {
        long millis = (ticks + (System.currentTimeMillis() * 10000));
        return millis;
    }

    /**
     * Convert Tick to long
     *
     * @param _tickMillis
     * @return
     */
    public static long getDateTimeFromTick(long _tickMillis) {
        long millis = (_tickMillis - ticks) / 10000;
        return millis;
    }

    public static int convertDp2px(int dip, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Float.valueOf(dip * scale + 0.5f).intValue();
    }

    public static String formatDistanceFromYou(float meters) {
        if (meters < 1000) {
            return ((int) meters) + " m";
        } else if (meters < 10000) {
            return formatDec(meters / 1000f, 1) + " km";
        } else {
            return ((int) (meters / 1000f)) + " km";
        }
    }

    static String formatDec(float val, int dec) {
        int factor = (int) Math.pow(10, dec);

        int front = (int) (val);
        int back = (int) Math.abs(val * (factor)) % factor;

        return front + "." + back;
    }

    /**
     * @param resource
     * @param context
     * @return
     */
    public static Drawable loadNinePatch(int resource, Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        byte[] chunk = bitmap.getNinePatchChunk();
        if (NinePatch.isNinePatchChunk(chunk)) {
            return new NinePatchDrawable(context.getResources(), bitmap, chunk, new Rect(), null);
        } else return new BitmapDrawable(bitmap);
    }

    /**
     * @param path
     * @param context
     * @return
     */
    public static Drawable loadNinePatch(String path, Context context) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        byte[] chunk = bitmap.getNinePatchChunk();
        if (NinePatch.isNinePatchChunk(chunk)) {
            return new NinePatchDrawable(context.getResources(), bitmap, chunk, new Rect(), null);
        } else return new BitmapDrawable(bitmap);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getCurrentAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem != null) {
                listItem.setLayoutParams(new AbsListView.LayoutParams(0, 0));
                listItem.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                //listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight() + 10;
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * Fix horizontal dimension of ListView to prevent scroll effect
     *
     * @param view
     */
    public static void setViewHeightBasedOnChildren(LinearLayout view) {

        int totalHeight = 0;
        for (int i = 0; i < view.getChildCount(); i++) {
            View listItem = view.getChildAt(i);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredWidth() + 10;
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = totalHeight + (5 * (view.getChildCount() - 1));
        view.setLayoutParams(params);
    }

    /*
  * Making image in circular shape
  */
    public static Bitmap getRoundedShape(Context ctx, Bitmap scaleBitmapImage, int width, int height) {
        int targetWidth = ctx.getResources().getDimensionPixelSize(width);
        int targetHeight = ctx.getResources().getDimensionPixelSize(height);
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

    /**
     * *************************************************
     * Funzione per il controllo della partita IVA.
     * Linguaggio: Java.
     * Versione del: 2002-07-07
     * ***************************************************
     */
    public static boolean ControllaPIVA(String pi) {
        int i, c, s;
        if (pi.length() == 0) return true;
        if (pi.length() != 11)
            return false;
        for (i = 0; i < 11; i++) {
            if (pi.charAt(i) < '0' || pi.charAt(i) > '9')
                return false;
        }
        s = 0;
        for (i = 0; i <= 9; i += 2)
            s += pi.charAt(i) - '0';
        for (i = 1; i <= 9; i += 2) {
            c = 2 * (pi.charAt(i) - '0');
            if (c > 9) c = c - 9;
            s += c;
        }
        if ((10 - s % 10) % 10 != pi.charAt(10) - '0')
            return false;
        return true;
    }

    /**
     * @param dateOne
     * @param dateTwo
     * @return
     */
    public static boolean isDateOneBefore(Date dateOne, Date dateTwo) {
        long timeOne = dateOne.getTime();
        long timeTwo = dateTwo.getTime();
        long oneDay = 1000 * 60 * 60 * 24;
        long delta = (timeTwo - timeOne) / oneDay;

        if (delta > 0 || delta == 0) {
            return true; //"dateTwo is " + delta + " days after dateOne";
        } else {
            delta *= -1;
            return false; //"dateTwo is " + delta + " days before dateOne";
        }
    }

    /**
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * [0] "originalEvent" (id=830007842672)
     * [1] "availabilityStatus" (id=830007842752)
     * [2] "ownerAccount" (id=830007842840)
     * [3] "_sync_account_type" (id=830007842920)
     * [4] "visibility" (id=830007843008)
     * [5] "rrule" (id=830007843080)
     * [6] "lastDate" (id=830007843144)
     * [7] "hasAlarm" (id=830007843216)
     * [8] "guestsCanModify" (id=830007843288) [9] "guestsCanSeeGuests" (id=830007843376)
     * [10] "exrule" (id=830007843464)
     * [11] "rdate" (id=830007843528)
     * [12] "transparency" (id=830007843592)
     * [13] "timezone" (id=830007843672)
     * [14] "selected" (id=830007843744)
     * [15] "dtstart" (id=830007843816) [16] "title" (id=830007843888)
     * [17] "_sync_time" (id=830007843952)
     * [18] "_id" (id=830007844024) [19] "hasAttendeeData" (id=830007844088) [20] "_sync_id" (id=830007844176)
     * [21] "commentsUri" (id=830007844248) [22] "description" (id=830007844328) [23] "htmlUri" (id=830007844408) [24] "_sync_account" (id=830007844480)
     * [25] "_sync_version" (id=830007844560)
     * [26] "hasExtendedProperties" (id=830007844640)
     * [27] "calendar_id" (id=830007844736)
     *
     * @param ctx
     * @param title
     * @param addInfo
     * @param place
     * @param status
     * @param startDate
     * @param needReminder
     * @param needMailService
     * @return
     */
    public static long pushAppointmentsToCalender(Context ctx, String title, String addInfo, String place, int status, long startDate, int allDay, int needReminder, int needMailService) {
        long eventID = -1000;
        try {
            String eventUriString = "content://com.android.calendar/events";
            ContentValues eventValues = new ContentValues();
            eventValues.put("calendar_id", 1); // id, We need to choose from our mobile for primary its 1
            eventValues.put("title", title);
            eventValues.put("description", addInfo);
            if (!CodeUtils.isBlankOrNull(place))
                eventValues.put("eventLocation", place);

            startDate = startDate + 1000 * 60 * 60;
            long endDate = startDate + 1000 * 60 * 60; // For next 1hr
            eventValues.put("dtstart", startDate);
            eventValues.put("dtend", endDate);
            eventValues.put("allDay", allDay); //which should remind me for whole day 0 for false, 1 for true
            eventValues.put("eventTimezone", TimeZone.getDefault().toString());
            eventValues.put("eventStatus", status); // This information is sufficient for most entries tentative (0), confirmed (1) or canceled (2):

            // eventValues.put("visibility", 3); // visibility to default (0),
            // confidential (1), private
            // (2), or public (3):
            //  eventValues.put("transparency", 0); // You can control whether
            // an event consumes time
            // opaque (0) or transparent
            // (1).

            eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

            Uri eventUri = ctx.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
            eventID = Long.parseLong(eventUri.getLastPathSegment());
            if (needReminder != -1) {
                String reminderUriString = "content://com.android.calendar/reminders";
                ContentValues reminderValues = new ContentValues();
                reminderValues.put("event_id", eventID);
                reminderValues.put("minutes", 10); // Default value of the system. Minutes is a integer
                reminderValues.put("method", 1); // Alert Methods: Default(0), Alert(1), Email(2),SMS(3)
                Uri reminderUri = ctx.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
            }

            /*if (needMailService != -1) {
                String attendeuesesUriString = "content://com.android.calendar/attendees";
                ContentValues attendeesValues = new ContentValues();
                attendeesValues.put("event_id", eventID);
                attendeesValues.put("attendeeName", "xxxxx"); // Attendees name
                attendeesValues.put("attendeeEmail", "xxx");// Attende E mail id
                attendeesValues.put("attendeeRelationship", 0); // Relationship_Attende(1),Relationship_None(0), Organizer(2),Performer(3),Speaker(4)
                attendeesValues.put("attendeeType", 0); // None(0), Optional(1),Required(2), Resource(3)
                attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1), Decline(2),Invited(3),Tentative(4)
                Uri attendeuesesUri = ctx.getApplicationContext().getContentResolver().insert(Uri.parse(attendeuesesUriString), attendeesValues);
            }*/
        } catch (Exception _ex) {
            eventID = -1000;
        }
        return eventID;
    }

    /**
     * @param ctx
     * @return
     */
    public static String getValueFromManifest(Context ctx, String key) {
        ApplicationInfo ai = null;
        String checkVersionUrl = null;
        try {
            ai = ctx.getPackageManager().getApplicationInfo(
                    ctx.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            checkVersionUrl = bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return checkVersionUrl;
    }

    /**
     * @param ctx
     * @return
     */
    public static int getApplicationVersion(Context ctx) {
        int value = 1;
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            value = pi.versionCode;
        } catch (Exception _ex) {

        }
        return value;
    }

    public static String md5hash(String key) {
        MessageDigest hash = null;
        try {
            hash = MessageDigest.getInstance(HASH_ALGORITHM_MD5);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        hash.update(key.getBytes());
        byte[] digest = hash.digest();
        StringBuilder builder = new StringBuilder();
        for (int b : digest) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString((b >> 0) & 0xf));
        }
        return builder.toString();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    //validate password
    public static boolean isValidPassword(String password) {
        /*Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }*/
        boolean returnValue = true;
        if (TextUtils.isEmpty(password)) {
            returnValue = false;
        } else {
            if (password.length() <= 7) {
                returnValue = false;
            }
        }
        return returnValue; //(!TextUtils.isEmpty(password) && password.length() > 7);
    }

    public static File saveImage(Context context, Bitmap bitmap) {
        Bitmap originalImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());

        String iconsStoragePath = Environment.getExternalStorageDirectory().getPath() + "/moneymize/";
        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            String filePath = sdIconStorageDir + "/avatar.jpg";
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

    public static File getOutgoingImageFile(Context context) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory().getPath(),
                "moneymize");
        createMedia(folder);
        File f = new File(folder, "avatar_take.jpg");
        f.createNewFile();
        return f;
    }

    public static File resizeImage(Context context, Uri uri, int maxWidth, int maxHeight, int quality)
            throws FileNotFoundException {

        final int MAX_IMAGE_SIZE = 1200000; // 1.2MP

        ContentResolver cr = context.getContentResolver();

        // compute optimal image scale size
        int scale = 1;
        InputStream in = cr.openInputStream(uri);

        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            // calculate optimal image scale size
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > MAX_IMAGE_SIZE)
                scale++;

            Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);
        } catch (IOException e) {
            Log.d(TAG, "unable to calculate optimal scale size, using original image");
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // ignored
            }
        }

        // open image again for the actual scaling
        Bitmap bitmap = null;

        try {
            in = cr.openInputStream(uri);
            BitmapFactory.Options o = new BitmapFactory.Options();

            if (scale > 1) {
                o.inSampleSize = scale - 1;
            }

            bitmap = BitmapFactory.decodeStream(in, null, o);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // ignored
            }
        }

        if (bitmap == null) {
            return null;
        }
        float photoW = bitmap.getWidth();
        float photoH = bitmap.getHeight();
        if (photoW == 0 || photoH == 0) {
            return null;
        }
        float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
        int w = (int) (photoW / scaleFactor);
        int h = (int) (photoH / scaleFactor);
        if (h == 0 || w == 0) {
            return null;
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);

        // check for rotation data
        scaledBitmap = bitmapOrientation(context, uri, scaledBitmap);

        String filename = String.format("avatar.jpg");
        final File compressedFile = new File(context.getCacheDir(), filename);

        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(compressedFile);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

            return compressedFile;
        } finally {
            try {
                stream.close();
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public static Bitmap bitmapOrientation(Context context, Uri media, Bitmap bitmap) {
        // check if we have to (and can) rotate the thumbnail
        try {
            Cursor cursor = context.getContentResolver().query(media,
                    new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int orientation = cursor.getInt(0);
                cursor.close();

                if (orientation != 0) {
                    Matrix m = new Matrix();
                    m.postRotate(orientation);

                    Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    bitmap.recycle();
                    bitmap = rotated;

                }
            }
        } catch (Exception e) {
            Log.w(TAG, e.getLocalizedMessage());
        }

        return bitmap;
    }

    private static boolean createMedia(File path) {
        return path.isDirectory() || path.mkdirs();
    }

    public static String md5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(password.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return null;
    }

    public static double formatCurrency(String value) {
        try {
            String pattern = "([.,\\W])+";
            String clean = value.replaceAll(pattern, "");
            String str = new StringBuffer(clean).insert(clean.length() - 2, ".").toString();
            Log.d(TAG, String.valueOf(Float.valueOf(str)));
            return Double.valueOf(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return  mWifi.isConnected();
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        boolean result = netInfo != null && netInfo.isConnectedOrConnecting();
        return result;
    }

    public static double round(double value, int places) {
        /*BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(dp, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();*/
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String formatBigNumber(long value) {
        if (value == Long.MIN_VALUE) return formatBigNumber(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatBigNumber(-value);
        if (value < 1000) return Long.toString(value);

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static void startHomeUserActivity(Context ctx, String HOME_CATEGORY, Class next_activity) {
        startHomeUserActivity(ctx, HOME_CATEGORY, next_activity, null);
    }

    public static void startHomeUserActivity(Context ctx, String HOME_CATEGORY, Class next_activity, int... anim) {
        Class home = null;
        Intent _homeIntent = null;
        Activity act = null;
        try {

            act = (Activity) ctx;
            home = Class.forName(next_activity.getName());
            _homeIntent = new Intent(ctx, home);
            _homeIntent.addCategory(HOME_CATEGORY);
            _homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            act.startActivity(_homeIntent);
            if (anim != null) {
                act.overridePendingTransition(
                        anim[0],
                        anim[1]);
            }
            act.finish();
        } catch (Exception _ex) {
            Log.e(act.getClass(), _ex);
            throw new RuntimeException(_ex);
        }
    }

    private final static class UnreachableCode extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}