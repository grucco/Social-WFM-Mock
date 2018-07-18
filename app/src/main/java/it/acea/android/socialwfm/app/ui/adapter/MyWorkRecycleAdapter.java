package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fingerlinks.mobile.android.utils.widget.recycleview.multiselector.SingleSelector;
import org.fingerlinks.mobile.android.utils.widget.recycleview.multiselector.SwappingHolder;

import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;
//import it.acea.android.socialwfm.app.model.odl.MockOdl;
import it.acea.android.socialwfm.app.model.odl.Odl;

/**
 * Created by Raphael on 19/10/2015.
 */
public class MyWorkRecycleAdapter extends RecyclerView.Adapter<MyWorkRecycleAdapter.MainViewHolder> {

    private List<Odl> data;
    //private List<MockOdl> mdata;
    private Context context;
    private CallBack callBack;

    private SingleSelector mMultiSelector = new SingleSelector();

    public MyWorkRecycleAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        //this.mdata = new ArrayList<>();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_odl, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        //MockOdl item = mdata.get(position);
        Odl item = data.get(position);

        if (position == 0) {
            holder.odl_status.setImageResource(R.drawable.ic_work_play_wm);
        } else {
            holder.odl_status.setImageResource(R.drawable.ic_work_play_wm_disabled);
        }
        holder.odl_number.setText(
                context.getResources().getString(R.string.odl_number_label, item.getAufnr(), item.getVornr()));

        if (item.isPreposto()) {
            holder.odl_people_type.setImageResource(R.drawable.ic_capopreposto);
        } else {
            if (!item.isTeam()) {
                holder.odl_people_type.setImageResource(R.drawable.ic_work_single_operator_odl_list);
            } else {
                holder.odl_people_type.setImageResource(R.drawable.ic_work_groups_odl_list);
            }
        }

        holder.odl_title.setText(Html.fromHtml(
                item.getOdltype()
        ));

        holder.odl_description.setText(Html.fromHtml(
                item.getTaskdesc()
        ));

        /**
         * Fix http://www.hostedredmine.com/issues/507572
         */
        if (item.isApp()) {
            holder.odl_people_is_app.setVisibility(View.VISIBLE);
        } else {
            holder.odl_people_is_app.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void insert(List<Odl> data) {
        int count = getItemCount();
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
        notifyItemRangeInserted(count, data.size());
    }

    /*public void insertt(List<MockOdl> data) {
        int count = getItemCount();
        this.mdata.clear();
        this.mdata.addAll(data);
        this.notifyDataSetChanged();
        notifyItemRangeInserted(count, data.size());
    }*/

    public void update(List<Odl> data) {
        int count = getItemCount();
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
        notifyItemRangeInserted(count, data.size());
    }

    /*public void updater(List<MockOdl> data) {
        int count = getItemCount();
        this.mdata.clear();
        this.mdata.addAll(data);
        this.notifyDataSetChanged();
        notifyItemRangeInserted(count, data.size());
    }*/

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onClick(String id);
    }

    public Odl getItem(int position) {
        return this.data.get(position);
    }

    /*public MockOdl getItemm(int position) {
        return this.mdata.get(position);
    }*/


    public class MainViewHolder extends SwappingHolder implements View.OnClickListener {

        public ImageView odl_status;
        public TextView odl_number;
        public ImageView odl_people_type;
        public TextView odl_title;
        public TextView odl_description;
        public LinearLayout row_odl_card;
        public ImageView odl_people_is_app;

        public MainViewHolder(View itemView) {
            super(itemView, mMultiSelector);

            row_odl_card = (LinearLayout) itemView.findViewById(R.id.row_odl_card);
            odl_status = (ImageView) itemView.findViewById(R.id.odl_status);
            odl_number = (TextView) itemView.findViewById(R.id.odl_number);
            odl_people_type = (ImageView) itemView.findViewById(R.id.odl_people_type);
            odl_title = (TextView) itemView.findViewById(R.id.odl_title);
            odl_description = (TextView) itemView.findViewById(R.id.odl_description);
            odl_people_is_app = (ImageView) itemView.findViewById(R.id.odl_people_is_app);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!mMultiSelector.tapSelection(this)) {
                if (callBack != null) callBack.onClick(data.get(getPosition()).getAufnr());
                mMultiSelector.setSelected(this, true);
            }
            notifyDataSetChanged();
        }

    }

    ;
};