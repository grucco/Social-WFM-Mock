package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.melnykov.fab.FloatingActionButton;

import org.fingerlinks.mobile.android.utils.CodeUtils;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.Group;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.feed.CheckError;

/**
 * Created by Raphael on 11/11/2015.
 */
public class NewGroupDialog {

    private EditText name;
    private EditText description;
    private FloatingActionButton save;
    private Context context;
    private MaterialDialog dialog;
    private MaterialDialog progress;
    private CallBack callBack;

    public NewGroupDialog(Context mContext) {
        context = mContext;
        dialog = new MaterialDialog.Builder(context).customView(R.layout.dialog_new_group, false).build();
        name = (EditText) dialog.getCustomView().findViewById(R.id.name);
        description = (EditText) dialog.getCustomView().findViewById(R.id.description);
        save = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(context)) {
                    Utils.errorToast((context));
                    return;
                }
                if (name.getText().length() != 0 && description.getText().length() != 0) {
                    progress = new MaterialDialog.Builder(context)
                            .content(R.string.new_group_message)
                            .progress(true, 0)
                            .build();
                    progress.show();
                    JsonObject body = new JsonObject();
                    body.addProperty("Name", name.getText().toString());
                    body.addProperty("Description", description.getText().toString());
                    body.addProperty("IsActive", true);
                    body.addProperty("Participation", "full");
                    body.addProperty("UploadPolicy", "all");
                    body.addProperty("InvitePolicy", "followers");
                    body.addProperty("GroupType", "private_internal");

                    HttpClientRequest.executeRequestNewGroup(context, body, new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progress.dismiss();
                            if (e != null) return;
                            GsonBuilder builder = Utils.getRealmGsonBuilder();
                            Gson gson = builder.create();
                            CheckError error = gson.fromJson(result, CheckError.class);
                            if (error.getError() != null) {
                                Toast.makeText(context,
                                        error.getError().getMessage().getValue(),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Group group = gson.fromJson(result.getAsJsonObject("d").get("results"), Group.class);
                            if (callBack != null) callBack.onSuccess(group);
                            dialog.dismiss();
                        }
                    });
                }
                if (name.getText().length() == 0) {
                    name.setError(context.getString(R.string.campo_richiesto));
                } else {
                    name.setError(null);
                }
                if (description.getText().length() == 0) {
                    description.setError(context.getString(R.string.campo_richiesto));
                } else {
                    description.setError(null);
                }
            }
        });
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void show() {
        dialog.show();
    }

    public interface CallBack {
        void onSuccess(Group group);
    }

}
