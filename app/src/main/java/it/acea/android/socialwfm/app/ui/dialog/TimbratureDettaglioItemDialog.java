package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Timbratura;
import it.acea.android.socialwfm.app.ui.fragment.TimbratureFragment;

/**
 * Created by a.simeoni on 18/03/2016.
 */
public class TimbratureDettaglioItemDialog /*implements MaterialDialog.SingleButtonCallback*/ {

    private Timbratura.TimbraturaDettaglio timbraturaDettaglio;
    private MaterialDialog dialog;
    private View dialogView;
    private TextView tvTipoText;
    private TextView tvData;
    private TextView tvApprovatore;
    private TextView tvOra;
    private TextView tvSocieta;
    private TextView tvCdc;
    private TextView tvMotivo;
    private TextView tvAppunto;

    private Context context;

    public TimbratureDettaglioItemDialog(Context context, Timbratura.TimbraturaDettaglio timbraturaDettaglio) {
        this.timbraturaDettaglio = timbraturaDettaglio;
        this.dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_timbrature_detail, false)
                //.negativeText(R.string.cancella)
                //.onNegative(this)
                .build();
        bindViews();
        hideUnavailableFields();
        injectContent();

        if (!timbraturaDettaglio.isExternal()) setActionButtonModifica();
    }

    public void show() {
        this.dialog.show();
    }

    private void bindViews() {
        dialogView = dialog.getCustomView();
        tvTipoText = (TextView) dialogView.findViewById(R.id.timbrature_detail_tipo_tv);
        tvData = (TextView) dialogView.findViewById(R.id.timbrature_detail_data_tv);
        tvApprovatore = (TextView) dialogView.findViewById(R.id.timbrature_detail_approvatore_tv);
        tvOra = (TextView) dialogView.findViewById(R.id.timbrature_detail_ora_tv);
        tvSocieta = (TextView) dialogView.findViewById(R.id.timbrature_detail_societa_et);
        tvCdc = (TextView) dialogView.findViewById(R.id.timbrature_detail_cdc_tv);
        tvMotivo = (TextView) dialogView.findViewById(R.id.timbrature_detail_motivo_sp);
        tvAppunto = (TextView) dialogView.findViewById(R.id.timbrature_detail_appunto_tv);
        dialogView.findViewById(R.id.close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setActionButtonModifica() {
        dialog.setActionButton(DialogAction.POSITIVE, R.string.modifica);
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new TimbratureFragment.MessageModificaTimbratura(timbraturaDettaglio));
                dialog.dismiss();
            }
        });
    }

    private void hideUnavailableFields() {

        if (!this.timbraturaDettaglio.isCentroDiCostoVisible()) {
            this.dialogView.findViewById(R.id.timbrature_detail_cdc_container).setVisibility(View.GONE);
        }
        if (!this.timbraturaDettaglio.isMotivoVisible()) {
            this.dialogView.findViewById(R.id.timbrature_detail_motivo_container).setVisibility(View.GONE);
        }
        if (!this.timbraturaDettaglio.isSocietaVisible()) {
            this.dialogView.findViewById(R.id.timbrature_detail_societa_container).setVisibility(View.GONE);
        }
    }

    private void injectContent() {
        tvTipoText.setText(this.timbraturaDettaglio.getTipoDescrizione());
        tvData.setText(this.timbraturaDettaglio.getGiornoAsString());
        tvApprovatore.setText(Utils.cleanNullableValue(this.timbraturaDettaglio.getApprovatore()));
        tvOra.setText(this.timbraturaDettaglio.getOraString());
        tvAppunto.setText(this.timbraturaDettaglio.getNota());
        if (this.timbraturaDettaglio.isCentroDiCostoVisible()) {
            tvCdc.setText(this.timbraturaDettaglio.getIdCentroDiCosto());
        }
        if (this.timbraturaDettaglio.isMotivoVisible()) {
            tvMotivo.setText(this.timbraturaDettaglio.getMotivoDescrizione());
        }
        if (this.timbraturaDettaglio.isSocietaVisible()) {
            tvSocieta.setText(this.timbraturaDettaglio.getIdSocieta());
        }
    }

   /* @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        switch (which) {
            case NEGATIVE:
                EventBus.getDefault().post(new TimbratureFragment.MessageCancellaTimbratura(timbraturaDettaglio));
                break;
        }
        dialog.dismiss();

    }*/
}
