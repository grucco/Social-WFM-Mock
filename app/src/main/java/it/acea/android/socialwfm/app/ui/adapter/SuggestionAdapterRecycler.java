package it.acea.android.socialwfm.app.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.materialdrawer.view.BezelImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.http.response.user.UserSuggestion;

/**
 * Created by nicola on 26/10/16.
 */

public class SuggestionAdapterRecycler extends RecyclerView.Adapter<SuggestionAdapterRecycler.SuggestionViewHolder> {

    List<UserSuggestion> data;
    OnUserSuggestionClick onUserSuggestionClick;
    public interface OnUserSuggestionClick {
        void onClick(UserSuggestion userSuggestion);
    }

    public SuggestionAdapterRecycler(@NonNull OnUserSuggestionClick onUserSuggestionClick) {
        data = new ArrayList<>();
        this.onUserSuggestionClick = onUserSuggestionClick;
    }

    public void update(List<UserSuggestion> data) {
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_member, null);
        return new SuggestionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {
        final UserSuggestion user = data.get(position);
        holder.name.setText(user.getFullName());
        new ImageFactory(holder.avatar.getContext(), user.getId(), ImageFactory.TYPE.MEMBER).into(holder.avatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserSuggestionClick.onClick(user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.member_name)
        TextView name;

        @Bind(R.id.member_type)
        BezelImageView avatar;

        @Bind(R.id.base_divider)
                View divider;

        SuggestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            divider.setVisibility(View.GONE);
        }
    }
}
