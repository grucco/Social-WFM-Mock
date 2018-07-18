package it.acea.android.socialwfm.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.events.MemberSelectedFromSearch;
import it.acea.android.socialwfm.app.model.BeanAutocomplete;
import it.acea.android.socialwfm.app.ui.adapter.AutocompleteGruppiContattiAdapter;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.feed.CheckError;
import it.acea.android.socialwfm.http.response.group.GroupSuggestion;
import it.acea.android.socialwfm.http.response.user.UserSuggestion;


/**
 * Created by a.simeoni on 22/07/2016.
 */
public class SearchGrouppiContattiDialog extends Dialog {

   public enum SearchMode
    {
        SEARCHMODE_GRUPPI,
        SEARCHMODE_CONTATTI
    }

    private int MINIMUM_AUTOCOMPLETE_CHARACTERS = 1;

    @Bind(R.id.overview)
    TextView tvOverview;
    @Bind(R.id.text)
    EditText searchInput;
    @Bind(R.id.suggestionRecyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressWheel progress;

    private AutocompleteGruppiContattiAdapter adapter;

    private SearchMode searchMode;


    public SearchGrouppiContattiDialog(Context context, SearchMode searchMode){
        super(context);
        this.searchMode = searchMode;
        initUi();
        ButterKnife.bind(this);
        initTitles();
        initRecyclerView();
        searchInput.addTextChangedListener(new AutocompleteTextWatcher());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMemberFromSearch(MemberSelectedFromSearch event) {
        dismiss();
    }

    private void initRecyclerView(){
        adapter = new AutocompleteGruppiContattiAdapter(getContext(),
                new ArrayList<BeanAutocomplete>(),this.searchMode);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initUi(){
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.md_default_dialog_width);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_search_group_contatti, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view, params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void initTitles(){
            switch(this.searchMode){
                case SEARCHMODE_GRUPPI:{
                    this.tvOverview.setText(R.string.cerca_un_gruppo);
                    this.searchInput.setHint(R.string.nome_gruppo);
                    break;
                }
                case SEARCHMODE_CONTATTI:{
                    this.tvOverview.setText(R.string.cerca_un_contatto);
                    this.searchInput.setHint(R.string.nome_contatto);
                    break;
                }
            }
    }

    private void gruppiAutocomplete(String query){
        progress.setVisibility(View.VISIBLE);
        HttpClientRequest.gruppiAutoComplete(getContext(), query, new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                progress.setVisibility(View.INVISIBLE);
                if (e != null) return;
                GsonBuilder builder = Utils.getRealmGsonBuilder();
                Gson gson = builder.create();
                CheckError error = gson.fromJson(result, CheckError.class);
                if (error.getError() != null) {
                    Toast.makeText(getContext(), error.getError().getMessage().getValue(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Type listType = new TypeToken<List<GroupSuggestion>>() {
                }.getType();
                List<BeanAutocomplete> suggestions = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                if (suggestions.size() != 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.update(suggestions);
                    ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPositionWithOffset(0,0);
                }
                else{
                    adapter.clean();
                }
            }
        });
    }

    private void contattiAutocomplete(String query){
        progress.setVisibility(View.VISIBLE);
        HttpClientRequest.userAutoComplete(getContext(), query,null, new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                progress.setVisibility(View.INVISIBLE);
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
                List<BeanAutocomplete> suggestions = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                if (suggestions.size() != 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.update(suggestions);
                }
                else{
                    adapter.clean();
                }

            }
        });
    }

    private class AutocompleteTextWatcher implements TextWatcher{

        private Timer timer=new Timer();
        private final long DELAY = 500; // milliseconds

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            final String text = s.toString();
            if(text.length() > MINIMUM_AUTOCOMPLETE_CHARACTERS){
                //Aspetta un certo delay prima di effettuare la chiamata
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                switch(searchMode){
                                    case SEARCHMODE_CONTATTI:{
                                        contattiAutocomplete(text);
                                        break;
                                    }
                                    case SEARCHMODE_GRUPPI:{
                                        gruppiAutocomplete(text);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                },DELAY);
            }
        }
    }

}
