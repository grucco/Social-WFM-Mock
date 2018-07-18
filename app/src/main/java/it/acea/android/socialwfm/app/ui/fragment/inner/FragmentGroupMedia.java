package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.events.FollowGroupEvent;
import it.acea.android.socialwfm.app.model.Group;
import it.acea.android.socialwfm.app.model.JamError;
import it.acea.android.socialwfm.app.ui.ErrorViewManager;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.adapter.MediaAdapter;
import it.acea.android.socialwfm.app.ui.dialog.NewFolderDialog;
import it.acea.android.socialwfm.app.ui.fragment.DetailDocumentFragment;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;
import it.acea.android.socialwfm.http.response.content.ContentItem;
import it.acea.android.socialwfm.http.response.feed.CheckError;
import tr.xip.errorview.ErrorView;

/**
 * Created by Raphael on 04/11/2015.
 */
public class FragmentGroupMedia extends BaseFragment implements MediaAdapter.CallBack {

    private static final String TAG = FragmentGroupMedia.class.getName();
    private static final int PICKFILE_REQUEST_CODE = 200;
    private static final int MEDIA_PICKER_REQUEST_CODE = 201;
    private static final int REQUEST_TAKE_PHOTO = 202;
    private static final int REQUEST_VIDEO_CAPTURE = 203;
    @Bind(R.id.list_members)
    ListView list_members;
    private String groupId;
    private MediaAdapter adapter;
    private LinkedHashMap<String, String> path = new LinkedHashMap<>();

    @Bind(R.id.path)
    TextView pathLabel;
    @Bind(R.id.back)
    CardView back;
    @Bind(R.id.loader)
    RelativeLayout loader;
    @Bind(R.id.no_data)
    ErrorView empty;

    @Bind(R.id.menu)
    FloatingActionMenu fabMenu;
    @Bind(R.id.menu_item_add_folder)
    FloatingActionButton fabMenuAddFolder;
    @Bind(R.id.menu_item_add_media)
    FloatingActionButton fabMenuAddMedia;
    @Bind(R.id.menu_item_add_document)
    FloatingActionButton fabMenuAddDocument;
    @Bind(R.id.menu_item_take_image)
    FloatingActionButton fabMenuTakeImage;
    @Bind(R.id.menu_item_take_video)
    FloatingActionButton fabMenuTakeVideo;

    FutureCallback<Boolean> actionCallback = new FutureCallback<Boolean>() {
        @Override
        public void onCompleted(Exception e, Boolean result) {
            if (e != null || !result) {
                loader.setVisibility(View.GONE);
                showErrorMessage();
                return;
            }
            loadResourceContent(getLastFolderId(), getLastFolderName(), FolderCallback.ACTIONS.REFRESH);
        }
    };

    FolderCallback updateContentCallback = new FolderCallback() {
        @Override
        public void onCompleted(Exception e, JsonObject result) {
            try {
                if (e != null) throw e;
                checkResultIsError(result);
                updateUiContentFromResult(result);
                updatePath(this.getAction(), this.getId(), this.getName());
            } catch (JamError e1) {
                Toast.makeText(getContext(), e1.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {
                showErrorMessage();
            } finally {
                loader.setVisibility(View.GONE);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        groupId = getArguments().getString("GROUP_ID");
        View view = inflater.inflate(R.layout.fragment_group_media, null);
        ButterKnife.bind(this, view);
        ErrorViewManager.setErrorViewNoDocuments(empty);
        EventBus.getDefault().register(this);
        return view;
    }

    @Subscribe(sticky = true)
    public void followEvent(FollowGroupEvent event) {
        fabMenu.setVisibility(Group.authUserCanUploadMedia(event.group, event.follow) ? View.VISIBLE: View.INVISIBLE );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.back)
    void goBack() {
        if (!CodeUtils.checkInternetConnection(getActivity())) {
            Utils.errorToast((getActivity()));
            return;
        }
        try {
            String prevId = new ArrayList<>(path.keySet()).get(path.keySet().size()-2);
            String prevName = new ArrayList<>(path.values()).get(path.values().size()-2);
            loadResourceContent(prevId, prevName, FolderCallback.ACTIONS.PREV);
        } catch (IndexOutOfBoundsException e) {
            android.util.Log.d(TAG, "goBack", e);
        }
    }

    @OnClick({R.id.menu_item_add_media, R.id.menu_item_add_document, R.id.menu_item_add_folder,
            R.id.menu_item_take_image, R.id.menu_item_take_video})
    public void menuAction(View view) {
        switch (view.getId()) {
            case R.id.menu_item_add_media:
                showMediaChooser();
                break;
            case R.id.menu_item_add_document:
                showFileChooser();
                break;
            case R.id.menu_item_add_folder:
                newFolderDialog();
                break;
            case R.id.menu_item_take_image:
                catturaFoto();
                break;
            case R.id.menu_item_take_video:
                catturaVideo();
                break;
        }
        fabMenu.close(true);
    }

    public void showMediaChooser() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/* video/*");
        getParentFragment().startActivityForResult(pickIntent, MEDIA_PICKER_REQUEST_CODE);
    }

    public void showFileChooser() {
        if (!CodeUtils.checkInternetConnection(getActivity())) {
            Utils.errorToast((getActivity()));
            return;
        }

        Intent i = new Intent(getActivity(), FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Fix per il bug dei nested fragment con onActivityResult
        // http://stackoverflow.com/questions/6147884/onactivityresult-not-being-called-in-fragment?lq=1
        getParentFragment().startActivityForResult(i, PICKFILE_REQUEST_CODE);
    }

    public void newFolderDialog() {
        if (!CodeUtils.checkInternetConnection(getActivity())) {
            Utils.errorToast((getActivity()));
            return;
        }
        new NewFolderDialog(getActivity(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                createFolder(input.toString());
            }
        }).show();
    }

    public void catturaFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                getParentFragment().startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
    }

    public void catturaVideo() {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
                getParentFragment().startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
    }

    private File createImageFile(Bitmap bitmap) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "image_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(imageFileName, ".jpg", storageDir);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes.toByteArray());
        fileOutputStream.close();
        return file;
    }

    private void galleryAddPic(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
    }

    private void createFolder(String name) {
        String id = getLastFolderId();
        loader.setVisibility(View.VISIBLE);
        if (id.equals(groupId)) {
            HttpClientRequest.createFolderInGroup(getActivity(), this, id, name, actionCallback);
        } else {
            HttpClientRequest.createFolderInParentFolder(getActivity(), this, id, name, actionCallback);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new MediaAdapter(getActivity(), empty);
        adapter.setCallBack(this);
        list_members.setAdapter(adapter);
        loadResourceContent(groupId, "/", FolderCallback.ACTIONS.NEXT);
    }


    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onFileClick(String documentId, String documentType, String documentName) {
        if (!CodeUtils.checkInternetConnection(getActivity())) {
            Utils.errorToast((getActivity()));
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        bundle.putString("documentType", documentType);
        bundle.putString("documentName", documentName);
        bundle.putString("groupId", groupId);
        Bundle supportBundle = new Bundle();
        supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
        supportBundle.putString(Constant.SUPPORT_FRAGMENT_TAG, DetailDocumentFragment.class.getName());
        supportBundle.putString(Constant.SUPPORT_FRAGMENT, DetailDocumentFragment.class.getName());
        Navigator.with(getActivity())
                .build()
                .goTo(SupportActivity.class, supportBundle)
                .commit();
    }

    @Override
    public void onFolderClick(final String folderId, final String folderName) {
        if (!CodeUtils.checkInternetConnection(getActivity())) {
            Utils.errorToast((getActivity()));
            return;
        }
        loadResourceContent(folderId, folderName, FolderCallback.ACTIONS.NEXT);
    }

    @Override
    public void deleteContentItem(ContentItem contentItem) {
        final MaterialDialog progress = new MaterialDialog.Builder(getContext()).progress(true, 0).show();
        HttpClientRequest.executeRequestDeleteContentItem(getContext(), this, contentItem, new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    CheckErrorInJamResponse.check(e, result);
                    Toast.makeText(getContext(), "Cancellato", Toast.LENGTH_SHORT).show();
                    loadResourceContent(getLastFolderId(), getLastFolderName(), FolderCallback.ACTIONS.REFRESH);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Toast.makeText(getContext(), e1.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    progress.dismiss();
                }
            }
        });
    }

    private void setPath() {
        pathLabel.setText(getPathString());
        back.setVisibility(path.size() > 1 ? View.VISIBLE : View.GONE);
    }


    private void updatePath(FolderCallback.ACTIONS action, String id, String name) {
        switch (action) {
            case NEXT:
                path.put(id, name);
                break;
            case PREV:
                    String lastId = getLastFolderId();
                    if (lastId != null) path.remove(lastId);
                break;
        }
        setPath();
    }

    private String getPathString() {
        return TextUtils.join("/", path.values());
    }

    private String getLastFolderId() {
        String id = null;
        try {
            id = new ArrayList<>(path.keySet()).get(path.keySet().size()-1);
        } catch (ArrayIndexOutOfBoundsException e) {
            android.util.Log.e(TAG, "getLastFolderId", e);
        }
        return id;
    }

    private String getLastFolderName() {
        String name = null;
        try {
            name = new ArrayList<>(path.values()).get(path.values().size()-1);
        } catch (ArrayIndexOutOfBoundsException e) {
            android.util.Log.e(TAG, "getLastFolderName", e);
        }
        return name;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Ion.getDefault(getActivity()).cancelAll(this);
    }

    private void uploadFile(String filePath) {
        File file = new File(filePath);
        loader.setVisibility(View.VISIBLE);
        String id = getLastFolderId();
        if (id.equals(groupId)) {
            // aggiungi al gruppo
            HttpClientRequest.uploadFileToGroup(getActivity(), this, file, id, actionCallback);
        } else {
            // aggiungi a cartella corrente
            HttpClientRequest.uploadFileToPublicFolder(getActivity(), this, file, id, actionCallback);
        }
    }

    private void loadResourceContent(String id, String name, FolderCallback.ACTIONS actions) {
        loader.setVisibility(View.VISIBLE);
        updateContentCallback.setAction(actions);
        updateContentCallback.setId(id);
        updateContentCallback.setName(name);
        if (id.equals(groupId)) {
            HttpClientRequest.executeRequestGetRootContent(getActivity(), this, id, updateContentCallback);
        } else {
            HttpClientRequest.executeRequestGetFolderContent(getActivity(), this, id, updateContentCallback);
        }
    }

    private void updateUiContentFromResult(JsonObject result) throws NullPointerException {
        Type listType = new TypeToken<List<ContentItem>>() {
        }.getType();
        List<ContentItem> contentItems = new Gson().fromJson(result.getAsJsonObject("d").get("results"), listType);
        adapter.update(contentItems);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICKFILE_REQUEST_CODE:
                    String path = data.getData().getPath();
                    uploadFile(path);
                    break;
                case MEDIA_PICKER_REQUEST_CODE:
                    Uri uriData = data.getData();
                    uploadFile(Utils.getPathFromMediaUri(getContext(), uriData));
                    break;
                case REQUEST_TAKE_PHOTO:
                    Bitmap mphoto = (Bitmap) data.getExtras().get("data");
                    try {
                        File photoFile = createImageFile(mphoto);
                        uploadFile(photoFile.getAbsolutePath());
                        galleryAddPic(photoFile);
                    } catch (IOException e) {
                        showErrorMessage();
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_VIDEO_CAPTURE:
                    Uri videoUri = data.getData();
                    uploadFile(Utils.getPathFromMediaUri(getContext(), videoUri));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkResultIsError(JsonObject result) throws JamError {
        CheckError error = new Gson().fromJson(result, CheckError.class);
        if (error.getError() != null) throw new JamError(error.getError());
    }

    private void showErrorMessage() {
        Toast.makeText(getActivity(), R.string.si_e_verificato_un_errore, Toast.LENGTH_LONG).show();
    }


    static abstract class FolderCallback implements FutureCallback<JsonObject> {
        enum ACTIONS { PREV, REFRESH, NEXT}
        private ACTIONS action = ACTIONS.REFRESH;
        private String name;
        private String id;

        public String getId() {
            return id;

        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAction(ACTIONS action) {
            this.action = action;
        }

        public ACTIONS getAction() {
            return this.action;
        }
    }
}
