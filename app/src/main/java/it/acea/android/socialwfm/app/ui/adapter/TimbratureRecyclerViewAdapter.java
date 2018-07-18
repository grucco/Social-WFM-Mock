package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Timbratura;
import it.acea.android.socialwfm.app.ui.dialog.TimbratureDettaglioItemDialog;

/**
 * Created by a.simeoni on 17/03/2016.
 */


public class TimbratureRecyclerViewAdapter extends RecyclerView.Adapter<TimbratureRecyclerViewAdapter.TimbratureRowViewHolder> {
    private List<Timbratura.TimbraturaOrario> timbratureList;
    private Context context;

    public TimbratureRecyclerViewAdapter(Context context, List<Timbratura.TimbraturaOrario> list) {
        this.timbratureList = list;
        this.context = context;
    }

    public void sortTimbraturaMenoRecente() {
        Collections.sort(timbratureList, Timbratura.TimbraturaOrario.TIMBRATURA_MENO_RECENTE);
    }

    public void sortTimbraturaPiuRecente() {
        Collections.sort(timbratureList, Timbratura.TimbraturaOrario.TIMBRATURA_PIU_RECENTE);
    }

    @Override
    public TimbratureRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_timbrature, parent, false);
        return new TimbratureRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimbratureRowViewHolder holder, int position) {
        final Timbratura.TimbraturaOrario item = this.timbratureList.get(position);
        holder.tvData.setText(item.getGiorno().getGiornoString());
        holder.tvPianificato.setText(item.getGiorno().getPianificato());
        holder.tvPianificatoColor.setBackgroundColor(ContextCompat.getColor(context, Utils.ESS_COLORS.valueOf(item.getTipo()).getColorRes()));
        holder.tvOra.setText(item.getOraDescrizione());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getDettaglio() != null)
                    new TimbratureDettaglioItemDialog(context, item.getDettaglio()).show();
            }
        });
        if (item.getDettaglio() != null) {
            holder.tvStato.setText(item.getDettaglio().getStato());
            holder.tvAppunto.setText(item.getDettaglio().getNota());
            holder.imDettaglio.setVisibility(View.VISIBLE);
        } else {
            holder.tvStato.setText("");
            holder.tvAppunto.setText("");
            holder.imDettaglio.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return this.timbratureList != null ? this.timbratureList.size() : 0;
    }

    // VIEW HOLDER
    public class TimbratureRowViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.row_timbrature_data_tv)
        TextView tvData;
        @Bind(R.id.row_timbrature_pianificato_tv)
        TextView tvPianificato;
        @Bind(R.id.row_timbrature_pianificato_color)
        TextView tvPianificatoColor;
        @Bind(R.id.row_timbrature_ora_tv)
        TextView tvOra;
        @Bind(R.id.row_timbrature_appunto_tv)
        TextView tvAppunto;
        @Bind(R.id.row_timbrature_stato_tv)
        TextView tvStato;
        @Bind(R.id.row_timbrature_action_bt)
        CardView imDettaglio;


        public TimbratureRowViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAdapterPosition();
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }
}
