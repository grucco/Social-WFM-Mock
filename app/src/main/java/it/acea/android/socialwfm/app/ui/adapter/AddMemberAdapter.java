package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.materialdrawer.view.BezelImageView;

import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.http.response.user.UserSuggestion;

/**
 * Created by Raphael on 22/12/2015.
 */
public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.MainViewHolder> {

    private List<UserSuggestion> suggestions;
    private Context context;
    private CallBack callBack;

    public AddMemberAdapter(Context context) {
        this.context = context;
        this.suggestions = new ArrayList<>();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void update(List<UserSuggestion> suggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(suggestions);
        this.notifyDataSetChanged();
    }

    public void clean() {
        this.suggestions.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_member, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        UserSuggestion suggestion = suggestions.get(position);
        new ImageFactory(context, suggestion.getId(), ImageFactory.TYPE.MEMBER).into(holder.member_type);
        holder.member_name.setText(suggestion.getFullName());
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private BezelImageView member_type;
        private TextView member_name;

        public MainViewHolder(View itemView) {
            super(itemView);
            member_type = (BezelImageView) itemView.findViewById(R.id.member_type);
            member_name = (TextView) itemView.findViewById(R.id.member_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (callBack != null) callBack.onClick(suggestions.get(getPosition()));
        }
    }

    public interface CallBack {
        void onClick(UserSuggestion suggestion);
    }

}
