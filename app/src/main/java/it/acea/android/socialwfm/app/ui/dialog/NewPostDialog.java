package it.acea.android.socialwfm.app.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikepenz.materialdrawer.view.BezelImageView;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.utils.Log;
import org.fingerlinks.mobile.android.utils.widget.EmptyRecyclerView;
import org.fingerlinks.mobile.android.utils.widget.MyMultiAutoCompleteTextView;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.events.FeedCreatedEvent;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.ui.adapter.SuggestionAdapterRecycler;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;
import it.acea.android.socialwfm.http.response.user.UserSuggestion;

/**
 * Created by Raphael on 11/11/2015.
 */
public class NewPostDialog extends Dialog implements DialogInterface.OnShowListener, SuggestionAdapterRecycler.OnUserSuggestionClick {

    private static final String TAG = NewPostDialog.class.getName();

    private Activity activity;
    protected MyMultiAutoCompleteTextView text;
    private EmptyRecyclerView suggestionRecyclerView;
    private ProgressWheel progressWheel;
    private View addMention;
    private View post;
    private CallBack callBack;
    private MaterialDialog progress;
    private String documentType;
    private SuggestionAdapterRecycler adapterRecycler;
    private List<UserSuggestion> userSuggestions;
    private TYPE type;
    private String id;
    private String customGroupId = null;
    private View searchBox;
    private View emptySearchResult;
    private View addTag;



    public NewPostDialog(Activity activity, TYPE mType, String mId) {
        super(activity);
        this.activity = activity;
        initDialog(activity, mType, mId);
    }

    public NewPostDialog(Context context, TYPE mType, String mId) {
        super(context);
        initDialog(context, mType, mId);
    }

    protected String getPostText() {
        return this.text.getText().toString();
    }

    private void initDialog(Context context, TYPE mType, String mId) {

        this.id = mId;
        this.type = mType;

        userSuggestions = new ArrayList<>();

        int size = getContext().getResources().getDimensionPixelSize(R.dimen.md_default_dialog_width);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_new_post, null);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view, params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        text = (MyMultiAutoCompleteTextView) findViewById(R.id.text);
        searchBox = findViewById(R.id.searchBox);
        suggestionRecyclerView = (EmptyRecyclerView) findViewById(R.id.suggestionRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        suggestionRecyclerView.setLayoutManager(layoutManager);
        adapterRecycler = new SuggestionAdapterRecycler(this);
        emptySearchResult = findViewById(R.id.emptySearchResult);
        suggestionRecyclerView.setAdapter(adapterRecycler);
        suggestionRecyclerView.setEmptyView(emptySearchResult);
        addTag = findViewById(R.id.social_add_tag);
        text.setTokenizer(new MentionTokenizer());
        text.addTextChangedListener(new MentionTextWatcher());
        text.addTextChangedListener(hashtagTextWatcher);

        progressWheel = (ProgressWheel)findViewById(R.id.progress);

        BezelImageView avatar = (BezelImageView) findViewById(R.id.avatar);
        addMention = findViewById(R.id.social_add_mention_post);
        post = findViewById(R.id.social_post);

        User user = UserHelperFactory.getAuthenticatedUser(getContext());

        ImageFactory imageFactory = new ImageFactory(context,
                user.getId(),
                ImageFactory.TYPE.MEMBER,
                Utils.avatarPlaceholder(context, user.getFirstName(), user.getLastName()));
        imageFactory.into(avatar);
        text.setHint("Scrivi un post");

        this.setOnShowListener(this);

        addMention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.append("@");
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().isEmpty()) {
                    progress = new MaterialDialog.Builder(getContext())
                            .content(R.string.post_message)
                            .progress(true, 0)
                            .build();
                    progress.show();

                    HttpClientRequest.executeRequestPost(getContext(), getUrl(type, id), getPostText(), userSuggestions, new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            try {
                                CheckErrorInJamResponse.check(e, result);
                                EventBus.getDefault().post(new FeedCreatedEvent());
                                if (callBack != null) callBack.onSucces();
                            } catch (Exception e1) {
                                Toast.makeText(getContext(), e1.getMessage(), Toast.LENGTH_SHORT).show();
                            } finally {
                                progress.dismiss();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), R.string.newfeed_empty_text_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.append("#");
            }
        });
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    private void showProgress(){
        post.setVisibility(View.GONE);
        progressWheel.setVisibility(View.VISIBLE);
    }
    private void hideProgress(){
        progressWheel.setVisibility(View.GONE);
        post.setVisibility(View.VISIBLE);
    }

    private String getUrl(TYPE type, String id) {
        String url = getContext().getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/";
        switch (type) {
            case SELF:
                url = url + "FeedEntries?$format=json";
                break;
            case GROUP:
                url = url + "Groups('" + id + "')/FeedEntries?$format=json";
                break;
            case MEMBER:
                url = url + "Members('" + id + "')/FeedEntries?$format=json";
                break;
            case COMMENT:
                url = url + "FeedEntries('" + id + "')/Replies?$format=json";
                break;
            case DOCUMENT:
                url = url + "ContentItems(Id='" + id + "',ContentItemType='" + documentType + "')/FeedEntries?$format=json";
                break;
        }
        return url;
    }

    /**
     * Add documentType only if TYPE is DOCUMENT
     *
     * @param documentType
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }


    /**
     * Add customGroupId only if new post is linked in a group
     *
     * @param customGroupId
     */
    public void setCustomGroupId(String customGroupId) {
        this.customGroupId = customGroupId;
    }

    @Override
    public void onShow(DialogInterface dialog) {

    }

    @Override
    public void onClick(UserSuggestion userSuggestion) {
        text.updateText(userSuggestion.getFullName());
        String currentText = text.getText().toString();
        currentText = currentText.replace("@","");
        text.setText(currentText);
        userSuggestions.add(userSuggestion);
        Pattern p;
        for(UserSuggestion s:userSuggestions){
            p  = Pattern.compile(s.getFullName());
            Linkify.addLinks(text, p, "");
        }
        text.setSelection(text.getText().length());//sposta il cursore alla fine del testo
    }

    public enum TYPE {
        SELF,
        GROUP,
        MEMBER,
        COMMENT,
        DOCUMENT
    }

    public interface CallBack {
        void onSucces();
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
           String testo = s.toString();
            if (testo.contains("@")) {
                showSearchBox();
                String[] split = testo.split("@");
                String query;

                if(split.length>0){
                    query = split[split.length-1];
                }
                else{
                    return;
                }

                if (query.length() > 2 && query.length()<=15) {
                    Log.d(TAG, query);
                    final String arg = query;
                    showProgress();
                            HttpClientRequest.userAutoComplete(getContext(), arg, customGroupId, new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    try {
                                        CheckErrorInJamResponse.check(e, result);
                                        Type listType = new TypeToken<List<UserSuggestion>>() {
                                        }.getType();
                                        GsonBuilder builder = Utils.getRealmGsonBuilder();
                                        Gson gson = builder.create();
                                        List<UserSuggestion> suggestions = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                                        adapterRecycler.update(suggestions);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                        Toast.makeText(getContext(), e1.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    finally {
                                        hideProgress();
                                    }
                                }
                            });
                        }
            } else {
                hideSearchBox();
            }
        }
    }

    private void showSearchBox() {
        searchBox.setVisibility(View.VISIBLE);
    }

    private void hideSearchBox() {
        searchBox.setVisibility(View.GONE);
    }

    private void setHashtag() {
        text.removeTextChangedListener(hashtagTextWatcher);
        Pattern hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+)");
        Linkify.addLinks(text, hashtagPattern, "");
        text.setSelection(text.getText().length());
        text.addTextChangedListener(hashtagTextWatcher);

    }

    private TextWatcher hashtagTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Pattern hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+\\s)$");
            Matcher matcher = hashtagPattern.matcher(s);
            if (matcher.find())
                setHashtag();

        }
    };

    private class MentionTokenizer implements MultiAutoCompleteTextView.Tokenizer {

        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != '@') {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }
            if (i == 0) return 999999999;
            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == '@') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text) {
            return text + " ";
        }
    }

}