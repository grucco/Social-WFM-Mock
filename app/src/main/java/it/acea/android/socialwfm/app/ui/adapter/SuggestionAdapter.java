package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mikepenz.materialdrawer.view.BezelImageView;

import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.http.response.user.UserSuggestion;

/**
 * Created by Raphael on 10/12/2015.
 */
public class SuggestionAdapter extends ArrayAdapter<UserSuggestion> {

    private List<UserSuggestion> userSuggestions;
    private LayoutInflater layoutInflater;
    private Context context;

    public SuggestionAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userSuggestions = new ArrayList<>();
    }

    public void update(List<UserSuggestion> userSuggestions) {
        this.userSuggestions.clear();
        this.userSuggestions.addAll(userSuggestions);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userSuggestions.size();
    }

    @Override
    public UserSuggestion getItem(int position) {
        return userSuggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_member, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.member_name);
            holder.avatar = (BezelImageView) convertView.findViewById(R.id.member_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserSuggestion userSuggestion = userSuggestions.get(position);
        new ImageFactory(context, userSuggestion.getId(), ImageFactory.TYPE.MEMBER).into(holder.avatar);
        holder.name.setText(userSuggestion.getFullName());
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        BezelImageView avatar;
    }

}
