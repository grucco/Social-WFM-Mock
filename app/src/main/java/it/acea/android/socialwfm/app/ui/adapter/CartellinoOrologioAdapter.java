package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.fingerlinks.mobile.android.utils.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.delegate.SWFMRecyclerViewClickDelegate;
import it.acea.android.socialwfm.app.model.ess.CartellinoOrologio;


/**
 * Created by a.simeoni on 03/03/2016.
 */
public class CartellinoOrologioAdapter extends RecyclerView.Adapter<CartellinoOrologioAdapter.CartellinoViewHolder> implements View.OnClickListener {
    private List<CartellinoOrologio> cartellinoList;
    private Context context;
    private SWFMRecyclerViewClickDelegate delegate;

    public static final int ORDINAMENTO_DATA_INIZIO_PIU_RECENTE = 0;
    public static final int ORDINAMENTO_DATA_INIZIO_MENO_RECENTE = 1;
    public static final int ORDINAMENTO_DATA_FINE_PIU_RECENTE = 2;
    public static final int ORDINAMENTO_DATA_FINE_MENO_RECENTE = 3;

    public int ordinamentoCorrente = -1;

    public CartellinoOrologioAdapter(Context context, List<CartellinoOrologio> list) {
        this.context = context;
        this.cartellinoList = list;
    }

    public void setDelegate(SWFMRecyclerViewClickDelegate del) {
        this.delegate = del;
    }

    public int getOrdinamentoCorrente() {
        return this.ordinamentoCorrente;
    }

    @Override
    public CartellinoOrologioAdapter.CartellinoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cartellino, parent, false);
        return new CartellinoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CartellinoOrologioAdapter.CartellinoViewHolder holder, int position) {
        CartellinoOrologio cartellino = this.cartellinoList.get(position);
        holder.tvDataInizio.setText(Utils.cleanNullableValue(cartellino.getBegdaAsString()));
        holder.tvDataFine.setText(Utils.cleanNullableValue(cartellino.getEnddaAsString()));
        holder.tvTeorico.setText(Utils.cleanNullableValue(cartellino.getAmount1() + " Ore"));
        holder.tvAccumulate.setText(Utils.cleanNullableValue(cartellino.getAmount2() + " Ore"));
        holder.btDownloadPdf.setOnClickListener(this);
        holder.btDownloadPdf.setTag(cartellino);
    }

    @Override
    public int getItemCount() {
        return this.cartellinoList != null ? this.cartellinoList.size() : 0;
    }

    @Override
    public void onClick(View v) {
        CartellinoOrologio cartellino = (CartellinoOrologio) v.getTag();
        this.delegate.onItemSelected(cartellino);
    }

    public void sortList(int mode) {
        switch (mode) {
            case ORDINAMENTO_DATA_INIZIO_PIU_RECENTE: {
                this.sortByStartDateDescending();
                this.ordinamentoCorrente = mode;
                break;
            }
            case ORDINAMENTO_DATA_INIZIO_MENO_RECENTE: {
                this.sortByStartDateAscending();
                this.ordinamentoCorrente = mode;
                break;
            }
            case ORDINAMENTO_DATA_FINE_PIU_RECENTE: {
                this.sortByEndDateDescending();
                this.ordinamentoCorrente = mode;
                break;
            }
            case ORDINAMENTO_DATA_FINE_MENO_RECENTE: {
                this.sortByEndDateAscending();
                this.ordinamentoCorrente = mode;
                break;
            }
        }
    }

    private void sortByStartDateAscending() {
        Collections.sort(this.cartellinoList, new Comparator<CartellinoOrologio>() {
            @Override
            public int compare(CartellinoOrologio lhs, CartellinoOrologio rhs) {
                return lhs.getBegda().compareTo(rhs.getBegda());
            }
        });
    }

    private void sortByStartDateDescending() {
        Collections.sort(this.cartellinoList, new Comparator<CartellinoOrologio>() {
            @Override
            public int compare(CartellinoOrologio lhs, CartellinoOrologio rhs) {
                return (-1) * (lhs.getBegda().compareTo(rhs.getBegda()));
            }
        });
    }


    private void sortByEndDateAscending() {
        Collections.sort(this.cartellinoList, new Comparator<CartellinoOrologio>() {
            @Override
            public int compare(CartellinoOrologio lhs, CartellinoOrologio rhs) {
                return lhs.getEndda().compareTo(rhs.getEndda());
            }
        });
    }

    private void sortByEndDateDescending() {
        Collections.sort(this.cartellinoList, new Comparator<CartellinoOrologio>() {
            @Override
            public int compare(CartellinoOrologio lhs, CartellinoOrologio rhs) {
                return (-1) * (lhs.getEndda().compareTo(rhs.getEndda()));
            }
        });
    }


    public void reloadList() {
        this.notifyDataSetChanged();
    }

    public void printList() {
        for (CartellinoOrologio c : this.cartellinoList) {
            Log.d("CartellinoOrologioAdapter", c.toString());
        }
    }

    public static class CartellinoViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.row_cartellino_data_inizio)
        TextView tvDataInizio;
        @Bind(R.id.row_cartellino_data_fine)
        TextView tvDataFine;
        @Bind(R.id.row_cartellino_orario_di_lavoro_teorico)
        TextView tvTeorico;
        @Bind(R.id.row_cartellino_orare_produttive_accumulate)
        TextView tvAccumulate;
        @Bind(R.id.cartellino_orologio_button_download_pdf)
        ImageView btDownloadPdf;


        public CartellinoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
