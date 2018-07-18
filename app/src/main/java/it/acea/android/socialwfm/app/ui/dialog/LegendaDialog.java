package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import it.acea.android.socialwfm.R;

/**
 * Created by n.fiorillo on 08/03/2016.
 */


/*        Timbrature:
        ASS (Assenza)
        FST (Festivo)
        ERR (Errore)
        COR (Correzione)
        LAV (Al lavoro)
        NLA (Giorno non lavorativo)

        Giustificativi:
        FST  (Festivo)
        INV (Inviato)
        ASS (Assente)
        CNC  (Canc. Richiesto)
        NLA (Giorno non lavorativo)
        INS (Più inserimenti)*/

public abstract class LegendaDialog {
    private MaterialDialog dialog;

    public LegendaDialog(Context context, @LayoutRes int legendLayout, @IdRes int closeView) {
        dialog = new MaterialDialog.Builder(context)
                .customView(legendLayout, false)
                .autoDismiss(true)
                .build();
        dialog.getCustomView().findViewById(closeView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void show() {
        dialog.show();
    }

    public static class LegendaGiustificativiDialog extends LegendaDialog {
        public LegendaGiustificativiDialog(Context context) {
            super(context, R.layout.dialog_legenda_giustificativi, R.id.close_dialog);
        }
    }

    public static class LegendaTimbratureDialog extends LegendaDialog {
        public LegendaTimbratureDialog(Context context) {
            super(context, R.layout.dialog_legenda_timbrature, R.id.close_dialog);
        }
    }
}
