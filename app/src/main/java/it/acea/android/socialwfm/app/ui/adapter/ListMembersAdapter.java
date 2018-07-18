package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikepenz.materialdrawer.view.BezelImageView;

import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.http.response.group.members.Members;

/**
 * Created by Raphael on 19/11/2015.
 */
public class ListMembersAdapter extends BaseAdapter {

    private Context context;
    private List<Members> membersList;
    private LayoutInflater layoutInflater;
    private MemberRecycleAdapter.CallBack callback;

    public ListMembersAdapter(Context context, List<Members> membersList) {
        this.context = context;
        this.membersList = membersList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCallback(MemberRecycleAdapter.CallBack callback){
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return membersList.size();
    }

    @Override
    public Object getItem(int position) {
        return membersList.get(position);
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
        final Profile profile = membersList.get(position).getMember();
        Drawable drawable = Utils.avatarPlaceholder(context, profile.getFirstName(), profile.getLastName());
        new ImageFactory(context, profile.getId(), ImageFactory.TYPE.MEMBER, drawable).into(holder.avatar);
        holder.name.setText(profile.getFullName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(profile);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        BezelImageView avatar;
    }

}
