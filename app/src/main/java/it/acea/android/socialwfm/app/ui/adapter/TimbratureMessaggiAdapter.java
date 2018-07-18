package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.ess.TimbraturaMessaggio;

/**
 * Created by n.fiorillo on 11/03/2016.
 */
public class TimbratureMessaggiAdapter extends RecyclerView.Adapter<TimbratureMessaggiAdapter.TimbratureMessaggiViewHolder> {

    private List<TimbraturaMessaggio> timbraturaMessaggioList;
    private Context context;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(TimbraturaMessaggio timbraturaMessaggio);
    }

    public TimbratureMessaggiAdapter(Context context, List<TimbraturaMessaggio> timbraturaMessaggioList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.timbraturaMessaggioList = timbraturaMessaggioList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public TimbratureMessaggiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_timbratura_messaggio, parent, false);
        return new TimbratureMessaggiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimbratureMessaggiViewHolder holder, int position) {
        final TimbraturaMessaggio timbraturaMessaggio = this.timbraturaMessaggioList.get(position);
        holder.data.setText(timbraturaMessaggio.getDataString());
        holder.messaggio.setText(timbraturaMessaggio.getMessaggio());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(timbraturaMessaggio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (this.timbraturaMessaggioList != null) ? this.timbraturaMessaggioList.size() : 0;
    }

    public static class TimbratureMessaggiViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.row_timbratura_messaggio_data_tv)
        TextView data;

        @Bind(R.id.row_timbratura_messaggio_messaggio_tv)
        TextView messaggio;

        public TimbratureMessaggiViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
