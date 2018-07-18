package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import it.acea.android.socialwfm.app.model.ess.MonteOreTipo;

/**
 * Created by n.fiorillo on 02/03/2016.
 */
public class MonteOreTipoAdapter extends ArrayAdapter<MonteOreTipo> {
    public MonteOreTipoAdapter(Context context, List<MonteOreTipo> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
    }
}
