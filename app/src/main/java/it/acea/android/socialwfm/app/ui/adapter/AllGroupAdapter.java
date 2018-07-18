package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.materialdrawer.view.BezelImageView;

import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.GroupList;
import it.acea.android.socialwfm.factory.ImageFactory;

/**
 * Created by Raphael on 16/11/2015.
 */
public class AllGroupAdapter extends RecyclerView.Adapter<AllGroupAdapter.MainViewHolder> {

    private List<GroupList> searchGroups;
    private Context context;
    private CallBack callBack;

    public AllGroupAdapter(Context context) {
        this.context = context;
        this.searchGroups = new ArrayList<>();
    }

    public void update(List<GroupList> searchGroups) {
        this.searchGroups = new ArrayList<>();
        this.searchGroups = searchGroups;
        this.notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        GroupList bean = searchGroups.get(position);
        holder.nome.setText(bean.getTitle());
        
        int lockVisibility, autogroupVisibility, adminVisibility;
        lockVisibility = (bean.getGroupType().equals("private_internal"))?View.VISIBLE:View.GONE;
        autogroupVisibility = (bean.isAutoGroup())?View.VISIBLE:View.GONE;
        adminVisibility = (bean.isAdmin())?View.VISIBLE:View.GONE;

        holder.lock.setVisibility(lockVisibility);
        holder.auto.setVisibility(autogroupVisibility);
        holder.admin.setVisibility(adminVisibility);

        new ImageFactory(context, bean.getId(), ImageFactory.TYPE.GROUP).into(holder.logo);
        holder.item.setTag(bean.getId());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) callBack.onClick((String) v.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchGroups.size();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onClick(String groupId);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout item;
        private BezelImageView logo;
        private TextView nome;
        private View lock;
        private View auto;
        private View admin;
        //private TextView numero;

        public MainViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.item);
            logo = (BezelImageView) itemView.findViewById(R.id.logo);
            nome = (TextView) itemView.findViewById(R.id.nome);
            lock = itemView.findViewById(R.id.icon_lock);
            auto = itemView.findViewById(R.id.icon_auto);
            admin = itemView.findViewById(R.id.icon_admin);
            //numero = (TextView) itemView.findViewById(R.id.numero);
        }

    }

}
