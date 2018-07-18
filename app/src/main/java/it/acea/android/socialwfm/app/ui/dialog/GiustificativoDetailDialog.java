package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Giustificativi;
import it.acea.android.socialwfm.app.model.ess.GiustificativiType;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.ResponseManager;

/**
 * Created by n.fiorillo on 09/03/2016.
 */
public class GiustificativoDetailDialog implements MaterialDialog.SingleButtonCallback {

    private FragmentManager fragmentManager;
    private MaterialDialog dialogDetail;
    private TextView tipo;
    private TextView nome;
    private TextView inizio;
    private TextView fine;
    private TextView oreDiAssenza;
    private TextView oreDiAssenzaLabel;
    private TextView goduto;
    private TextView note;
    private TextView stato;
    private ImageView close;

    private Giustificativi giustificativo;
    private Context context;


    private MaterialDialog.OnDismissListener onDismissListener;

    public void setOnDismissListener(MaterialDialog.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public GiustificativoDetailDialog(Context context, FragmentManager fragmentManager, Giustificativi giustificativo) {
        this.fragmentManager = fragmentManager;
        this.giustificativo = giustificativo;
        this.context = context;
        dialogDetail = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_giustificativo_detail, false)
                .positiveText(R.string.modifica)
                .negativeText(R.string.cancella)
                .onAny(this)
                .autoDismiss(false)
                .build();
        if (onDismissListener != null) dialogDetail.setOnDismissListener(onDismissListener);
        buildView();
        bindData(giustificativo);
    }

    private void buildView() {
        stato = (TextView) dialogDetail.getCustomView().findViewById(R.id.giustificativo_detail_stato_tv);
        tipo = (TextView) dialogDetail.getCustomView().findViewById(R.id.giustificativo_detail_tipo_tv);
        nome = (TextView) dialogDetail.getCustomView().findViewById(R.id.giustificativo_detail_nome_tv);
        inizio = (TextView) dialogDetail.getCustomView().findViewById(R.id.giustificativo_detail_inizio_tv);
        fine = (TextView) dialogDetail.getCustomView().findViewById(R.id.giustificativo_detail_fine_tv);
        oreDiAssenza = (TextView) dialogDetail.getCustomView().findViewById(R.id.giustificativo_detail_ore_assenza_tv);
        oreDiAssenzaLabel = (TextView) dialogDetail.getCustomView().findViewById(R.id.giustificativo_detail_ore_assenza_label);
        goduto = (TextView) dialogDetail.getCustomView().findViewById(R.id.giustificativo_detail_goduto_tv);
        note = (TextView) dialogDetail.getCustomView().findViewById(R.id.giustificativo_detail_note_tv);
        close = (ImageView) dialogDetail.getCustomView().findViewById(R.id.close_dialog);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetail.dismiss();
            }
        });
    }

    private void bindData(Giustificativi giustificativo) {
        stato.setText(giustificativo.getStatusText());
        tipo.setText(giustificativo.getSubtypeDescription());
        nome.setText(giustificativo.getNamenxp());
        inizio.setText(Giustificativi.getDataEOraInizio(giustificativo));
        fine.setText(Giustificativi.getDataEOraFine(giustificativo));
        oreDiAssenza.setText(giustificativo.getAttabsHours());
        goduto.setText(giustificativo.getDeduction());
        note.setText(giustificativo.getCurrNotice());
        Realm realm = Realm.getDefaultInstance();
        GiustificativiType gt = realm.where(GiustificativiType.class).equalTo("Subty", giustificativo.getSubty()).findFirst();

        if (gt != null && Utils.cleanBooleanField(gt.getNoPartialDay())) {
            oreDiAssenzaLabel.setVisibility(View.INVISIBLE);
            oreDiAssenza.setVisibility(View.INVISIBLE);
            inizio.setText(Giustificativi.getDataInizio(giustificativo));
            fine.setText(Giustificativi.getDataFine(giustificativo));
        }

    }

    public void show() {
        dialogDetail.show();
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        switch (which) {
            case POSITIVE:
                final Realm realm = Realm.getDefaultInstance();
                GiustificativiType giustificativiType = realm.where(GiustificativiType.class).equalTo("Subty", giustificativo.getSubty()).findFirst();
                dialogDetail.dismiss();
                GiustificativoEditDialog dialog1 = new GiustificativoEditDialog(context, fragmentManager, giustificativo);
                dialog1.setOnDismissListener(onDismissListener);
                dialog1.show();
                break;
            case NEGATIVE:
                //delete this giustificativo
                new MaterialDialog.Builder(context)
                        .content("Vuoi cancellare il giustificativo corrente?")
                        .positiveText("OK")
                        .negativeText("Annulla")
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                switch (which) {
                                    case POSITIVE:
                                        deteleGiustificativo(dialog);
                                        break;
                                }
                            }
                        })
                        .show();
                break;
        }
    }

    private void deteleGiustificativo(final MaterialDialog dialog) {
        final MaterialDialog progressDialogToken = showProgress();
        EssClient.fetchXCSRFToken(context, EssClient.EssEntity.GIUSTIFICATIVI, new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    String csrfToken = ResponseManager.getXCSRFToken(e, result);
                    final MaterialDialog progressDialog = showProgress();
                    EssClient.deleteGiustificativo(context, csrfToken, giustificativo.getRequestId(), new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                new ResponseManager(e, result);
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                giustificativo.removeFromRealm();
                                realm.commitTransaction();
                            } catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                                new ErrorDialog(context, dataErrorExpection.getErrorDetails()).show();
                                dataErrorExpection.printStackTrace();
                            } catch (Exception e1) {
                                showGenericErrorMessage();
                                e1.printStackTrace();
                            } finally {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                if (onDismissListener != null)
                                    onDismissListener.onDismiss(null);
                                dialogDetail.dismiss();
                            }
                        }
                    });

                } catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                    new ErrorDialog(context, dataErrorExpection.getErrorDetails()).show();
                    dataErrorExpection.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    showGenericErrorMessage();
                } finally {
                    progressDialogToken.dismiss();
                }
            }
        });
    }

    public void showGenericErrorMessage() {
        new MaterialDialog.Builder(context)
                .title(R.string.attenzione)
                .content(R.string.si_e_verificato_un_errore)
                .positiveText(R.string.chiudi)
                .autoDismiss(true)
                .show();
    }

    private MaterialDialog showProgress(){
        MaterialDialog  progress = new MaterialDialog.Builder(context)
                .content(R.string.loading)
                .cancelable(false)
                .progress(true, 0)
                .build();
        progress.show();
        return progress;
    }

    private void dismissProgress(MaterialDialog progress){
        progress.dismiss();
    }
}
