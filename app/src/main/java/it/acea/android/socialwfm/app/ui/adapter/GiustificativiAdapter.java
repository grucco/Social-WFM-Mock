package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Giustificativi;
import it.acea.android.socialwfm.app.ui.dialog.GiustificativoDetailDialog;

/**
 * Created by raphaelbussa on 21/03/16.
 */
public class GiustificativiAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Giustificativi> giustificativiList;
    private Context context;
    private FragmentManager fragmentManager;

    public GiustificativiAdapter(Context context, FragmentManager fragmentManager) {
        this.giustificativiList = new ArrayList<>();
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setGiustificativiList(List<Giustificativi> giustificativiList) {
        this.giustificativiList.clear();
        this.giustificativiList.addAll(giustificativiList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return giustificativiList.size();
    }

    @Override
    public Object getItem(int position) {
        return giustificativiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_giustificativi, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.start_date = (TextView) convertView.findViewById(R.id.start_date);
            holder.end_date = (TextView) convertView.findViewById(R.id.end_date);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.ore = (TextView) convertView.findViewById(R.id.ore);
            holder.goduto = (TextView) convertView.findViewById(R.id.goduto);
            holder.action = (ImageView) convertView.findViewById(R.id.action);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Giustificativi giustificativi = giustificativiList.get(position);
        holder.title.setText(giustificativi.getSubtypeDescription());
        try {
            holder.start_date.setText(Utils.formattaDataAnno(giustificativi.getBegda()) + " - " + Utils.formatTimePTHMS(giustificativi.getBeginTime()));
            holder.end_date.setText(Utils.formattaDataAnno(giustificativi.getEndda()) + " - " + Utils.formatTimePTHMS(giustificativi.getEndTime()));
        } catch (Exception ignored) {
        }
        holder.name.setText(giustificativi.getNamenxp());
        holder.status.setText(giustificativi.getStatusText());
        holder.ore.setText(giustificativi.getAttabsHours());
        holder.action.setTag(giustificativi);
        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Giustificativi value = (Giustificativi) v.getTag();
                GiustificativoDetailDialog dialog = new GiustificativoDetailDialog(context, fragmentManager, value);
                if (onDismissListener != null) dialog.setOnDismissListener(onDismissListener);
                dialog.show();
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView start_date;
        TextView end_date;
        TextView name;
        TextView status;
        TextView ore;
        TextView goduto;
        ImageView action;
    }

    private MaterialDialog.OnDismissListener onDismissListener;

    public void setOnDismissListener(MaterialDialog.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
