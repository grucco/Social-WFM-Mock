package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.materialdrawer.view.BezelImageView;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.CodeUtils;

import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.fragment.GroupFragment;
import it.acea.android.socialwfm.app.ui.fragment.ProfileFragment;
import it.acea.android.socialwfm.factory.FeedDialogFactory;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.http.response.search.ObjectReference;
import it.acea.android.socialwfm.http.response.search.SearchResponse;

/**
 * Created by raphaelbussa on 11/01/16.
 */
public class SearchAdapter extends BaseAdapter {

    private List<SearchResponse> searchResponses;
    private Context context;
    private LayoutInflater layoutInflater;

    public SearchAdapter(List<SearchResponse> searchResponses, Context context) {
        this.searchResponses = searchResponses;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return searchResponses.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResponses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ObjectReference objectReference = searchResponses.get(position).getObjectReference();
        if (objectReference.getType().equals("separator")) {
            convertView = layoutInflater.inflate(R.layout.row_search_separetor, null);
            TextView label = (TextView) convertView.findViewById(R.id.label);
            label.setText(objectReference.getTitle());
            return convertView;
        }
        convertView = layoutInflater.inflate(R.layout.row_search_normal, null);
        BezelImageView image = (BezelImageView) convertView.findViewById(R.id.image);
        LinearLayout row = (LinearLayout) convertView.findViewById(R.id.row);
        TextView label = (TextView) convertView.findViewById(R.id.label);
        label.setText(objectReference.getTitle());
        switch (objectReference.getType().toLowerCase()) {
            case "member":
                new ImageFactory(context, objectReference.getId(), ImageFactory.TYPE.MEMBER).into(image);
                break;
            case "group":
                new ImageFactory(context, objectReference.getId(), ImageFactory.TYPE.GROUP).into(image);
                break;
            case "feedentry":

                break;
            case "comment":

                break;
        }
        row.setTag(objectReference);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(context)) {
                    Utils.errorToast((context));
                    return;
                }
                ObjectReference reference = (ObjectReference) v.getTag();
                Bundle bundle;
                Bundle supportBundle;
                switch (reference.getType().toLowerCase()) {
                    case "member":
                        bundle = new Bundle();
                        bundle.putString(ProfileFragment.ARGUMENT_PROFILE_ID, reference.getId());
                        supportBundle = new Bundle();
                        supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
                        supportBundle.putString(Constant.SUPPORT_FRAGMENT, ProfileFragment.class.getName());
                        Navigator.with(context)
                                .build()
                                .goTo(SupportActivity.class, supportBundle)
                                .commit();
                        break;
                    case "group":
                        bundle = new Bundle();
                        bundle.putString("ID_GROUP", reference.getId());
                        supportBundle = new Bundle();
                        supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
                        supportBundle.putString(Constant.SUPPORT_FRAGMENT, GroupFragment.class.getName());
                        Navigator.with(context)
                                .build()
                                .goTo(SupportActivity.class, supportBundle)
                                .commit();
                        break;
                    case "feedentry":
                        new FeedDialogFactory(context, reference.getODataURL()).show();
                        break;
                    case "comment":
                        new FeedDialogFactory(context, reference.getODataURL()).show();
                        break;
                }
            }
        });
        return convertView;
    }
}
