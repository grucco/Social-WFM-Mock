package it.acea.android.socialwfm.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greenfrvr.hashtagview.HashtagView;
import com.koushikdutta.async.future.FutureCallback;
import com.melnykov.fab.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.utils.CodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.Group;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.ui.adapter.AddMemberAdapter;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.feed.CheckError;
import it.acea.android.socialwfm.http.response.user.UserSuggestion;

/**
 * Created by Raphael on 11/11/2015.
 */
public class InviteGroupDialog extends Dialog {

    private static final String TAG = InviteGroupDialog.class.getName();

    private EditText text;
    private HashtagView tag;
    private Group group;
    private AddMemberAdapter adapter;
    private List<UserSuggestion> suggestions;
    private CallBack callBack;
    private RecyclerView list;
    private ProgressWheel progress;
    private FloatingActionButton save;

    public InviteGroupDialog(Context context, Group mGroup) {
        super(context);
        this.group = mGroup;
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.md_default_dialog_width);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_invite_group, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view, params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        save = (FloatingActionButton) findViewById(R.id.save);
        list = (RecyclerView) findViewById(R.id.add);
        ImageView image = (ImageView) findViewById(R.id.image);
        TextView name = (TextView) findViewById(R.id.name);
        text = (EditText) findViewById(R.id.text);
        tag = (HashtagView) findViewById(R.id.tag);
        text.addTextChangedListener(new MentionTextWatcher());
        new ImageFactory(getContext(), group.getId(), ImageFactory.TYPE.GROUP, getContext().getResources().getDrawable(R.drawable.background)).into(image);
        name.setText(group.getName());
        suggestions = new ArrayList<>();
        adapter = new AddMemberAdapter(getContext());

        progress = (ProgressWheel)findViewById(R.id.progress);

        adapter.setCallBack(new AddMemberAdapter.CallBack() {
            @Override
            public void onClick(UserSuggestion suggestion) {
                text.setText("");
                adapter.clean();
                list.setVisibility(View.GONE);
                for (UserSuggestion userSuggestion : suggestions) {
                    if (userSuggestion.getId().equals(suggestion.getId())) return;
                }
                suggestions.add(suggestion);
                tag.setData(suggestions, new HashtagView.DataTransform<UserSuggestion>() {
                    @Override
                    public CharSequence prepare(UserSuggestion item) {
                        return item.getFullName();
                    }
                });
            }
        });

        tag.addOnTagClickListener(new HashtagView.TagsClickListener() {
            @Override
            public void onItemClicked(Object item) {
                UserSuggestion suggestion = (UserSuggestion) item;
                suggestions.remove(suggestion);
                if (suggestions.size() == 0) {
                    tag.removeAllViews();
                    return;
                }
                tag.setData(suggestions, new HashtagView.DataTransform<UserSuggestion>() {
                    @Override
                    public CharSequence prepare(UserSuggestion item) {
                        return item.getFullName();
                    }
                });
            }
        });


        GridLayoutManager manager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
        list.setLayoutManager(manager);
        list.setAdapter(adapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(getContext())) {
                    Utils.errorToast((getContext()));
                    return;
                }
                if (suggestions.size() == 0) {
                    if (callBack != null) callBack.onSuccess(group);
                    dismiss();
                    return;
                }

                List<String> membersIds = new ArrayList<>();
                for (UserSuggestion s: suggestions) {
                    membersIds.add(s.getId());
                }

                showProgress();
                HttpClientRequest.inviteGroupByMembersId(getContext(), group.getId(), membersIds, new FutureCallback<Boolean>() {
                    @Override
                    public void onCompleted(Exception e, Boolean result) {
                        hideProgress();
                        if (result){
                            if (callBack != null) callBack.onSuccess(group);
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.invite_user_group_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onSuccess(Group group);
    }


    private void showProgress(){
        save.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        progress.setVisibility(View.INVISIBLE);
        save.setVisibility(View.VISIBLE);
    }

    private class MentionTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 2) {
                HttpClientRequest.userAutoComplete(getContext(), s.toString(), null, new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) return;
                        GsonBuilder builder = Utils.getRealmGsonBuilder();
                        Gson gson = builder.create();
                        CheckError error = gson.fromJson(result, CheckError.class);
                        if (error.getError() != null) {
                            Toast.makeText(getContext(), error.getError().getMessage().getValue(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Type listType = new TypeToken<List<UserSuggestion>>() {
                        }.getType();
                        List<UserSuggestion> suggestions = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                        List<UserSuggestion> suggestionsFilter = new ArrayList<>();
                        User logged = UserHelperFactory.getAuthenticatedUser(getContext());
                        for (UserSuggestion suggestion : suggestions) {
                            if (!suggestion.getId().equals(logged.getId())) {
                                suggestionsFilter.add(suggestion);
                            }
                        }
                        if (suggestionsFilter.size() != 0) list.setVisibility(View.VISIBLE);
                        adapter.update(suggestionsFilter);
                    }
                });
            }
        }
    }
};