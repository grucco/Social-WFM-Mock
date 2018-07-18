package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.factory.ImageFactory;

/**
 * Created by Raphael on 19/10/2015.
 */
public class MemberRecycleAdapter extends RecyclerView.Adapter<MemberRecycleAdapter.MainViewHolder> implements INameableAdapter {

    private List<Profile> data;
    private Context context;
    private CallBack callBack;

    public MemberRecycleAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_member, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Profile item = data.get(position);
        holder.member_name.setText(Utils.cleanNullableValue(item.getFullName()));
        new ImageFactory(context, item.getId(), ImageFactory.TYPE.MEMBER).into(holder.member_type);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void update(List<Profile> data) {
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onClick(Profile data);
    }

    public Profile getItem(int position) {
        return this.data.get(position);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView member_type;
        public TextView member_name;

        public MainViewHolder(View itemView) {
            super(itemView);
            selectedItems.clear();
            member_type = (ImageView) itemView.findViewById(R.id.member_type);
            member_name = (TextView) itemView.findViewById(R.id.member_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (callBack != null && !data.isEmpty()) callBack.onClick(data.get(getPosition()));
        }

    }

    @Override
    public Character getCharacterForElement(int i) {
        Character c = getItem(i).getTitle().charAt(0);
        if (Character.isDigit(c)) {
            c = '#';
        }
        return c;
    }

    public SparseBooleanArray selectedItems = new SparseBooleanArray();
}