package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mikepenz.materialdrawer.view.BezelImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.events.GroupSelectedFromSearch;
import it.acea.android.socialwfm.app.events.MemberSelectedFromSearch;
import it.acea.android.socialwfm.app.model.BeanAutocomplete;
import it.acea.android.socialwfm.app.ui.dialog.SearchGrouppiContattiDialog;
import it.acea.android.socialwfm.factory.ImageFactory;



public class AutocompleteGruppiContattiAdapter extends RecyclerView.Adapter<AutocompleteGruppiContattiAdapter.ViewHolder> {


    private List<BeanAutocomplete> list;
    private Context context;
    private ImageFactory.TYPE searchMode;

    public AutocompleteGruppiContattiAdapter(Context context,List<BeanAutocomplete> list,SearchGrouppiContattiDialog.SearchMode searchMode){
        this.context = context;
        this.list = list;
        setMode(searchMode);
    }

    private void setMode(SearchGrouppiContattiDialog.SearchMode mode){
        switch (mode){
            case SEARCHMODE_CONTATTI:{
                this.searchMode = ImageFactory.TYPE.MEMBER;
                break;
            }
            case SEARCHMODE_GRUPPI:{
                this.searchMode = ImageFactory.TYPE.GROUP;
                break;}
        }

    }

    public void update(List<BeanAutocomplete> suggestions) {
        this.list.clear();
        this.list.addAll(suggestions);
        this.notifyDataSetChanged();
    }

    public void clean() {
        this.list.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public AutocompleteGruppiContattiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AutocompleteGruppiContattiAdapter.ViewHolder holder, int position) {
        final BeanAutocomplete item = list.get(position);
        new ImageFactory(context, item.getId(), searchMode).into(holder.itemImage);
        holder.itemDescription.setText(item.getFullName());
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMode == ImageFactory.TYPE.GROUP) {
                    EventBus.getDefault().post(new GroupSelectedFromSearch(item.getId()));
                }
                if (searchMode == ImageFactory.TYPE.MEMBER) {
                    EventBus.getDefault().post(new MemberSelectedFromSearch(item.getId()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(this.list != null){
            return this.list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private BezelImageView itemImage;
        private TextView itemDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = (BezelImageView) itemView.findViewById(R.id.member_type);
            itemDescription = (TextView) itemView.findViewById(R.id.member_name);
        }
    }
}
