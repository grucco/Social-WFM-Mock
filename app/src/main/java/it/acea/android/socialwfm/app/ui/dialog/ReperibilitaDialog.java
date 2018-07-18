package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.ess.Reperibilita;

/**
 * Created by n.fiorillo on 13/04/2016.
 */
public class ReperibilitaDialog {
    private MaterialDialog dialog;

    public ReperibilitaDialog(Context context, Reperibilita reperibilita) {
        this.dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_reperibilita, false)
                .positiveText(R.string.chiudi)
                .autoDismiss(true)
                .build();
        bindData(reperibilita);
    }

    public void show() {
        this.dialog.show();
    }

    private void bindData(Reperibilita reperibilita) {
        View v = dialog.getCustomView();
        ((TextView) v.findViewById(R.id.dettaglio_reperibilita_descrizione_tv)).setText(reperibilita.getDescrizioneTipo());
        ((TextView) v.findViewById(R.id.dettaglio_reperibilita_data_inizio_tv)).setText(reperibilita.getDataInizioString());
        ((TextView) v.findViewById(R.id.dettaglio_reperibilita_ora_inizio_tv)).setText(reperibilita.getOraInizioString());
        ((TextView) v.findViewById(R.id.dettaglio_reperibilita_ora_fine_tv)).setText(reperibilita.getOraFineString());
    }
}
