package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.http.response.content.ContentItem;

/**
 * Created by Raphael on 19/11/2015.
 */
public class MediaAdapter extends BaseAdapter {

    private Context context;
    private List<ContentItem> contentItems;
    private LayoutInflater layoutInflater;
    private CallBack callBack;
    private View emptyView;

    public MediaAdapter(Context context) {
        this.context = context;
        this.contentItems = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public MediaAdapter(Context context, View emptyView) {
        this(context);
        this.emptyView = emptyView;
    }

    public void update(List<ContentItem> contentItems) {
        this.contentItems.clear();
        this.contentItems.addAll(contentItems);
        this.notifyDataSetChanged();
        if (emptyView != null) this.emptyView.setVisibility(contentItems.size()>0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getCount() {
        return contentItems.size();
    }

    @Override
    public Object getItem(int position) {
        return contentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_content_media, null);
            holder = new ViewHolder();
            holder.item = (LinearLayout) convertView.findViewById(R.id.item);
            holder.nome = (TextView) convertView.findViewById(R.id.nome);
            holder.numero = (TextView) convertView.findViewById(R.id.numero);
            holder.logo = (TextView) convertView.findViewById(R.id.logo);
            holder.delete =  convertView.findViewById(R.id.icon_item_content_media_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ContentItem item = contentItems.get(position);
        if (item.getFolder() != null) {
            holder.logo.setBackgroundResource(R.drawable.ic_folder_media);
            holder.logo.setText(R.string.empty);
        } else {
            holder.logo.setBackgroundResource(R.drawable.ic_file_media);
            String[] parseExr = item.getName().split("\\.");
            holder.logo.setText(parseExr[parseExr.length - 1]);
        }
        holder.nome.setText(item.getName());
        holder.numero.setText(item.getLastModifiedAt(context));
        holder.item.setTag(item);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentItem item = (ContentItem) v.getTag();
                if (item.getFolder() != null) {
                    if (callBack != null) callBack.onFolderClick(item.getId(), item.getName());
                } else {
                    if (callBack != null)
                        callBack.onFileClick(item.getId(), item.getContentListItemType(), item.getName());
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) callBack.deleteContentItem(item);
            }
        });
        return convertView;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onFileClick(String documentId, String documentType, String documentName);

        void onFolderClick(String folderId, String folderName);

        void deleteContentItem(ContentItem contentItem);
    }

    static class ViewHolder {
        LinearLayout item;
        TextView nome;
        TextView numero;
        TextView logo;
        View delete;
    }

}
