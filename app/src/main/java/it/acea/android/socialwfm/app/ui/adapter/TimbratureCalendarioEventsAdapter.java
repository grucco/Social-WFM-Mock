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
import it.acea.android.socialwfm.app.model.ess.Timbratura;
import it.acea.android.socialwfm.app.ui.dialog.TimbratureDettaglioItemDialog;

/**
 * Created by n.fiorillo on 25/03/2016.
 */
public class TimbratureCalendarioEventsAdapter extends
        RecyclerView.Adapter<TimbratureCalendarioEventsAdapter.TimbratureCalendarioEventsViewHolder> {

    private List<Timbratura.TimbraturaOrarioWeekEvent> events;
    private Context context;

    public TimbratureCalendarioEventsAdapter(Context context, List<Timbratura.TimbraturaOrarioWeekEvent> events) {
        this.events = events;
        this.context = context;
    }

    @Override
    public TimbratureCalendarioEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_timbrature_calendario_events,
                parent, false);
        return new TimbratureCalendarioEventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimbratureCalendarioEventsViewHolder holder, int position) {
        final Timbratura.TimbraturaOrarioWeekEvent tow = this.events.get(position);
        holder.descrizione.setText(tow.getTimbraturaOrario().getDescrizione());
        holder.color.setBackgroundColor(tow.getColor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timbratura.TimbraturaDettaglio td = tow.getTimbraturaOrario().getDettaglio();
                if (td != null)
                    new TimbratureDettaglioItemDialog(TimbratureCalendarioEventsAdapter.this.context,
                            tow.getTimbraturaOrario().getDettaglio()).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (this.events != null) ? this.events.size() : 0;
    }

    public static class TimbratureCalendarioEventsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.row_timbrature_calendario_events_descrizione_tv)
        TextView descrizione;

        @Bind(R.id.row_timbrature_calendario_events_color)
        TextView color;

        public TimbratureCalendarioEventsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
