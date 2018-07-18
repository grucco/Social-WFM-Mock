package it.acea.android.socialwfm.app.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Quadratura;

/**
 * Created by a.simeoni on 29/03/2016.
 */
public class QuadraturaAdapter extends RecyclerView.Adapter<QuadraturaAdapter.QuadraturaViewHolder> {

    public List<Quadratura> list;

    public QuadraturaAdapter(List<Quadratura> list) {
        this.list = list;
    }

    @Override
    public QuadraturaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_quadratura, parent, false);
        return new QuadraturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuadraturaViewHolder holder, int position) {
        Quadratura bean = list.get(position);
        holder.tvTipo.setText(bean.getError());
        holder.tvMessaggio.setText(bean.getEtext());
        holder.tvCid.setText(bean.getPernr());
        holder.tvDipendente.setText(bean.getEname());
        holder.tvData.setText(Utils.dateFormat(bean.getLdate()));
    }

    @Override
    public int getItemCount() {
        return (this.list != null) ? list.size() : 0;
    }


    public static class QuadraturaViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.row_quardatura_tv_tipo)
        TextView tvTipo;
        @Bind(R.id.row_quardatura_tv_messaggio)
        TextView tvMessaggio;
        @Bind(R.id.row_quardatura_tv_cid)
        TextView tvCid;
        @Bind(R.id.row_quardatura_tv_dipendente)
        TextView tvDipendente;
        @Bind(R.id.row_quardatura_tv_data)
        TextView tvData;

        public QuadraturaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
