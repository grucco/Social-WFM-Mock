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
import it.acea.android.socialwfm.app.model.ess.MonteOre;

/**
 * Created by n.fiorillo on 02/03/2016.
 */
public class MonteOreAdapter extends RecyclerView.Adapter<MonteOreAdapter.MonteOreViewHolder> {

    private List<MonteOre> monteOreList;

    public MonteOreAdapter(Context context, List<MonteOre> monteOreList) {
        this.monteOreList = monteOreList;
    }


    @Override
    public MonteOreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monte_ore, parent, false);
        return new MonteOreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MonteOreViewHolder holder, int position) {
        MonteOre monteOre = this.monteOreList.get(position);
        holder.descrizione.setText(monteOre.getDescrizione());
        holder.inizioRiduzione.setText(monteOre.getInizioRiduzioneString());
        holder.fineRiduzione.setText(monteOre.getFineRiduzioneString());
        holder.pianificato.setText(monteOre.getPianificato());
        holder.goduto.setText(monteOre.getGoduto());

    }

    @Override
    public int getItemCount() {
        return (this.monteOreList != null) ? this.monteOreList.size() : 0;
    }

    public static class MonteOreViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.row_monte_ore_descrizione_tv)
        TextView descrizione;
        @Bind(R.id.row_monte_row_inizio_riduzione_tv)
        TextView inizioRiduzione;
        @Bind(R.id.row_monte_ore_fine_riduzione_tv)
        TextView fineRiduzione;
        @Bind(R.id.row_monte_ore_pianificato_tv)
        TextView pianificato;
        @Bind(R.id.row_monte_ore_goduto_tv)
        TextView goduto;

        public MonteOreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
