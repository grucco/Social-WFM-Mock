package it.acea.android.socialwfm.app.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.activity.BaseActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.ui.adapter.SearchAdapter;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.feed.CheckError;
import it.acea.android.socialwfm.http.response.search.ObjectReference;
import it.acea.android.socialwfm.http.response.search.SearchResponse;

/**
 * Created by raphaelbussa on 11/01/16.
 */
public class SearchActivity extends BaseActivity {

    private static final String TAG = SearchActivity.class.getName();

    private EditText searchInput;
    private ListView list;
    private ImageView clear;
    private ProgressWheel progressWheel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView back = (ImageView) findViewById(R.id.back);
        list = (ListView) findViewById(R.id.list);
        clear = (ImageView) findViewById(R.id.clear);
        progressWheel = (ProgressWheel) findViewById(R.id.progress);
        searchInput = (EditText) findViewById(R.id.search_input);

        Uri data = getIntent().getData();
        if (data != null) {
            String[] parse = data.toString().split("#");
            String path = parse[parse.length - 1];
            searchInput.setText(path);
            search(path);
            KeyboardUtil.hideKeyboard(SearchActivity.this);
        }

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    clear.setVisibility(View.GONE);
                } else {
                    clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (s.length() >= 3) {
                    search(s.toString());
                }*/
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setText("");
            }
        });
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (searchInput.getText().length() != 0) {
                    search(searchInput.getText().toString());
                    KeyboardUtil.hideKeyboard(SearchActivity.this);
                }
                return false;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void search(String input) {
        if (!CodeUtils.checkInternetConnection(SearchActivity.this)) {
            Utils.errorToast((SearchActivity.this));
            return;
        }
        Log.d(TAG, input);
        progressWheel.setVisibility(View.VISIBLE);
        HttpClientRequest.executeRequestSearch(SearchActivity.this, input, new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                progressWheel.setVisibility(View.GONE);
                if (e != null || result == null) {
                    List<SearchResponse> searchResponseList = new ArrayList<>();
                    SearchResponse response = new SearchResponse();
                    ObjectReference reference = new ObjectReference();
                    reference.setType("separator");
                    reference.setTitle("Nessun risultato trovato");
                    response.setObjectReference(reference);
                    searchResponseList.add(response);
                    SearchAdapter adapter = new SearchAdapter(searchResponseList, SearchActivity.this);
                    list.setAdapter(adapter);
                    return;
                }
                CheckError error = new Gson().fromJson(result, CheckError.class);
                if (error.getError() != null) {
                    Toast.makeText(SearchActivity.this, error.getError().getMessage().getValue(), Toast.LENGTH_SHORT).show();
                    return;
                }

                Type listType = new TypeToken<List<SearchResponse>>() {
                }.getType();
                List<SearchResponse> responseList = new Gson().fromJson(result.getAsJsonObject("d").get("results"), listType);
                Collections.sort(responseList, new Comparator<SearchResponse>() {
                    @Override
                    public int compare(SearchResponse lhs, SearchResponse rhs) {
                        String a = String.valueOf(lhs.getObjectReference().getType());
                        String b = String.valueOf(rhs.getObjectReference().getType());
                        return a.compareTo(b);
                    }
                });
                List<SearchResponse> searchResponseList = new ArrayList<>();
                String currentType = "";
                for (SearchResponse response : responseList) {
                    if (response.getObjectReference().getType().toLowerCase().equals("member") ||
                            response.getObjectReference().getType().toLowerCase().equals("group") ||
                            response.getObjectReference().getType().toLowerCase().equals("feedentry") ||
                            response.getObjectReference().getType().toLowerCase().equals("comment")) {
                        if (!currentType.equals(response.getObjectReference().getType())) {
                            currentType = response.getObjectReference().getType();
                            ObjectReference reference = new ObjectReference();
                            switch (response.getObjectReference().getType().toLowerCase()) {
                                case "member":
                                    reference.setTitle("Membri");
                                    break;
                                case "group":
                                    reference.setTitle("Gruppi");
                                    break;
                                case "feedentry":
                                    reference.setTitle("Post");
                                    break;
                                case "comment":
                                    reference.setTitle("Commenti");
                                    break;
                            }
                            reference.setType("separator");
                            SearchResponse response1 = new SearchResponse();
                            response1.setObjectReference(reference);
                            searchResponseList.add(response1);
                        }
                        SearchResponse searchResponse = new SearchResponse();
                        searchResponse.setObjectReference(response.getObjectReference());
                        searchResponseList.add(searchResponse);
                    }
                }

                if (searchResponseList.size() == 0) {
                    SearchResponse response = new SearchResponse();
                    ObjectReference reference = new ObjectReference();
                    reference.setType("separator");
                    reference.setTitle("Nessun risultato trovato");
                    response.setObjectReference(reference);
                    searchResponseList.add(response);
                }

                SearchAdapter adapter = new SearchAdapter(searchResponseList, SearchActivity.this);
                list.setAdapter(adapter);
            }
        });
    }

    @Override
    protected int getToolbarProjectId() {
        return 0;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }
}
