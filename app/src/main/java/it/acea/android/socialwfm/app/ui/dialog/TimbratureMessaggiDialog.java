package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.ess.TimbraturaMessaggio;
import it.acea.android.socialwfm.app.ui.adapter.TimbratureMessaggiAdapter;
import it.acea.android.socialwfm.app.ui.fragment.TimbratureFragment;

/**
 * Created by n.fiorillo on 11/03/2016.
 */
public class TimbratureMessaggiDialog implements TimbratureMessaggiAdapter.OnItemClickListener {
    private MaterialDialog dialog;
    private Context context;

    public TimbratureMessaggiDialog(Context context, List<TimbraturaMessaggio> timbraturaMessaggioList) {
        this.context = context;
        this.dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_timbrature_messaggi, false)
                .build();
        bindData(timbraturaMessaggioList);
    }

    public void show() {
        dialog.show();
    }

    private void bindData(List<TimbraturaMessaggio> timbraturaMessaggioList) {
        TimbratureMessaggiAdapter adapter = new TimbratureMessaggiAdapter(context, timbraturaMessaggioList, this);
        View view = dialog.getCustomView();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.timbrature_messaggi_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(context)
                        .color(ContextCompat.getColor(context, R.color.ess_recycler_vier_divider))
                        .sizeResId(R.dimen.divider)
                        .build());

        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(TimbraturaMessaggio timbraturaMessaggio) {
        Calendar c = Calendar.getInstance();
        c.setTime(timbraturaMessaggio.getData());
        EventBus.getDefault().post(new TimbratureFragment.MessageRequestDataByDate(
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));
        dialog.dismiss();
    }
}
