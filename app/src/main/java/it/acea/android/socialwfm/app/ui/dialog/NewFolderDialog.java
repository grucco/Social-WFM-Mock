package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;

import it.acea.android.socialwfm.R;

/**
 * Created by n.fiorillo on 25/02/2016.
 */
public class NewFolderDialog {

    private MaterialDialog dialog;

    public NewFolderDialog(Context context, MaterialDialog.InputCallback inputCallback) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.nuova_cartella)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRange(1, -1)
                .input(R.string.nome, 0, inputCallback)
                .positiveText(R.string.crea)
                .negativeText(R.string.annulla)
                .build();
    }

    public void show() {
        dialog.show();
    }


}
