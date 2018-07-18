package it.acea.android.socialwfm.app.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.koushikdutta.async.future.FutureCallback;
import com.melnykov.fab.FloatingActionButton;

import org.fingerlinks.mobile.android.utils.CodeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.http.HttpClientRequest;

/**
 * Created by Raphael on 11/11/2015.
 */
public class ChangeImageDialog extends Dialog {

    private static final int GALLERY_REQUEST = 100;
    private static final int CAMERA_REQUEST = 101;

    private static final String TAG = ChangeImageDialog.class.getName();
    private ImageView image;
    private File current = null;
    private File currentPhoto = null;
    private MaterialDialog progress;

    public ChangeImageDialog(Context context, final Activity activity, final String id, final TYPE type) {
        super(context);
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.md_default_dialog_width);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_change_image, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view, params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.save);
        Button gallery = (Button) findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setType("image/*");
                activity.startActivityForResult(pickPhoto, GALLERY_REQUEST);
            }
        });
        final Button camera = (Button) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentPhoto = Utils.getOutgoingImageFile(getContext());
                    Intent take = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    take.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPhoto));
                    activity.startActivityForResult(take, CAMERA_REQUEST);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), R.string.change_image_dialog_errore, Toast.LENGTH_SHORT).show();
                }
            }
        });
        image = (ImageView) findViewById(R.id.image);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(getContext())) {
                    Utils.errorToast((getContext()));
                    return;
                }
                if (current == null) return;
                progress = new MaterialDialog.Builder(getContext())
                        .content(R.string.post_message)
                        .progress(true, 0)
                        .build();
                progress.show();
                switch (type) {
                    case GROUP:
                        HttpClientRequest.loadGroupImage(getContext(), current, id, new FutureCallback<Boolean>() {
                            @Override
                            public void onCompleted(Exception e, Boolean result) {
                                if (callBack != null) callBack.onSucces(id);
                                progress.dismiss();
                                dismiss();
                            }
                        });
                        break;
                    case USER:
                        HttpClientRequest.loadProfileImage(getContext(), current, id, new FutureCallback<Boolean>() {
                            @Override
                            public void onCompleted(Exception e, Boolean result) {
                                if (callBack != null) callBack.onSucces(id);
                                progress.dismiss();
                                dismiss();
                            }
                        });
                        break;
                }

            }
        });
    }

    public void setImage(Drawable drawable) {
        image.setImageDrawable(drawable);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                    new AsyncTask<Intent, Void, File>() {
                        @Override
                        protected File doInBackground(Intent... params) {
                            Bitmap bitMap;
                            InputStream inputStream;
                            Intent data = params[0];
                            try {
                                if (data.getData() == null) {
                                    bitMap = (Bitmap) data.getExtras().get("data");
                                } else {
                                    Uri uri = data.getData();
                                    if (uri.getScheme().equals("content")) {
                                        inputStream = getContext().getContentResolver().openInputStream(uri);
                                    } else {
                                        inputStream = new FileInputStream(uri.toString());
                                    }
                                    bitMap = BitmapFactory.decodeStream(inputStream);
                                }
                            } catch (Exception _ex) {
                                bitMap = null;
                            }
                            return Utils.saveImage(getContext(), bitMap);
                        }

                        @Override
                        protected void onPostExecute(File file) {
                            super.onPostExecute(file);
                            current = file;
                            Glide.with(getContext())
                                    .load(current)
                                    .into(image);
                        }

                    }.execute(data);
                    break;
                case CAMERA_REQUEST:
                    if (currentPhoto != null) {
                        new AsyncTask<Void, Void, File>() {
                            @Override
                            protected File doInBackground(Void... voids) {
                                return currentPhoto;
                            }

                            @Override
                            protected void onPostExecute(File file) {
                                super.onPostExecute(file);
                                current = file;
                                Glide.with(getContext())
                                        .load(current)
                                        .into(image);
                            }
                        }.execute();

                    }
                    break;
            }
        }

    }

    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onSucces(String id);
    }

    public enum TYPE {
        GROUP, USER
    }
};