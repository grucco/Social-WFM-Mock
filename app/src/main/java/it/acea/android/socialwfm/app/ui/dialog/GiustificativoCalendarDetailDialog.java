package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.ess.GiustificativiCalendar;

/**
 * Created by n.fiorillo on 09/03/2016.
 */
public class GiustificativoCalendarDetailDialog {
    private MaterialDialog dialog;
    private TextView tipo;
    private TextView data;
    private TextView note;
    private ImageView close;

    public GiustificativoCalendarDetailDialog(Context context, GiustificativiCalendar giustificativo) {
        dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_giustificativo_calendar_detail, false)
                .build();
        buildView();
        bindData(giustificativo);
    }

    private void buildView() {
        tipo = (TextView) dialog.getCustomView().findViewById(R.id.giustificativo_calendar_detail_tipo_tv);
        data = (TextView) dialog.getCustomView().findViewById(R.id.giustificativo_calendar_detail_data_tv);
        note = (TextView) dialog.getCustomView().findViewById(R.id.giustificativo_calendar_detail_note_tv);
        close = (ImageView) dialog.getCustomView().findViewById(R.id.close_dialog);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void bindData(GiustificativiCalendar giustificativo) {
        tipo.setText(giustificativo.getActkeytext());
        String giornoNome = giustificativo.getDaytext();
        String giorno = giustificativo.getDay();
        String mese = giustificativo.getMonthtext();
        String anno = giustificativo.getYear();
        String d = String.format("%s, %s %s %s", giornoNome, giorno, mese, anno);
        data.setText(d);
        note.setText(giustificativo.getActinfo().replaceAll(",[\\s]*", "\n"));
    }

    public void show() {
        dialog.show();
    }
}
