package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.http.response.ResponseManager;

/**
 * Created by n.fiorillo on 10/03/2016.
 */
public class ErrorDialog {
    private MaterialDialog dialog;

    public ErrorDialog(Context context, List<ResponseManager.ErrorDetail> errors) {
        if (context == null) return;
        if (errors == null) return;
        ArrayAdapter<ResponseManager.ErrorDetail> adapter = new ArrayAdapter<>(context, R.layout.row_error_detail, errors);
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.attenzione)
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                    }
                })
                .positiveText(R.string.chiudi)
                .autoDismiss(true)
                .build();
    }

    public void show() {
        dialog.show();
    }
}
