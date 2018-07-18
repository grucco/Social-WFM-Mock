package it.acea.android.socialwfm.app.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.delegate.SWFMRecyclerViewClickDelegate;
import it.acea.android.socialwfm.app.model.ess.CudCedolino;

/**
 * Created by a.simeoni on 16/05/2016.
 */
public class CudCedolinoAdapter extends RecyclerView.Adapter<CudCedolinoAdapter.CudCedolinoViewHolder> implements View.OnClickListener {

    private List<CudCedolino> list;

    private SWFMRecyclerViewClickDelegate delegate;

    public CudCedolinoAdapter(List<CudCedolino> l){
        list = l;
    }

    public void setDelegate(SWFMRecyclerViewClickDelegate del) {
        this.delegate = del;
    }

    @Override
    public CudCedolinoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cud_cedolino, parent, false);
        return new CudCedolinoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CudCedolinoViewHolder holder, int position) {
        CudCedolino item = list.get(position);
        holder.tvNomeFile.setText(item.getFilename());
        holder.tvMese.setText(item.getMonthText());
        holder.btDownloadPdf.setOnClickListener(this);
        holder.btDownloadPdf.setTag(item);
    }

    @Override
    public int getItemCount() {
        if(list != null)
            return list.size();
        else
            return 0;
    }

    @Override
    public void onClick(View v) {
        this.delegate.onItemSelected(v.getTag());
    }

    public static class CudCedolinoViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.row_cud_cedolino_nome_file)
        TextView tvNomeFile;
        @Bind(R.id.row_cud_cedolino_mese)
        TextView tvMese;
        @Bind(R.id.cud_cedolino_button_download_pdf)
        ImageView btDownloadPdf;

        public CudCedolinoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
