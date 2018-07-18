package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.fingerlinks.mobile.android.utils.widget.ImageSquare;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;

/**
 * Created by Raphael on 22/12/2015.
 */
public class LastPhotoAdapter extends RecyclerView.Adapter<LastPhotoAdapter.MainViewHolder> {

    private List<String> strings;
    private Context context;
    private CallBack callBack;

    public LastPhotoAdapter(Context context) {
        this.context = context;
        this.strings = new ArrayList<>();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void update(List<String> strings) {
        this.strings.clear();
        this.strings.addAll(strings);
        this.notifyDataSetChanged();
    }

    public void clean() {
        this.strings.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_last_image, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        String s = strings.get(position);
        Glide.with(context)
                .load(new File(s))
                .placeholder(R.drawable.img_place_holder)
                .into(holder.item);
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageSquare item;

        public MainViewHolder(View itemView) {
            super(itemView);
            item = (ImageSquare) itemView.findViewById(R.id.item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (callBack != null) callBack.onClick(strings.get(getPosition()));
        }
    }

    public interface CallBack {
        void onClick(String s);
    }

}
