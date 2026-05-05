package com.example.androidphotos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.androidphotos.util.ImageLoader;

/**
 * List adapter for search results across albums.
 */
public class SearchResultAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<SearchResult> results;

    public SearchResultAdapter(Context context, List<SearchResult> results) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.results = results;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_search_result, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SearchResult result = results.get(position);
        holder.photoName.setText(result.getPhoto().getDisplayName());
        holder.albumName.setText(context.getString(R.string.search_result_album, result.getAlbumName()));
        ImageLoader.loadThumbnail(context, holder.thumbnail, result.getPhoto().getUriString());
        return convertView;
    }

    private static class ViewHolder {
        final ImageView thumbnail;
        final TextView photoName;
        final TextView albumName;

        ViewHolder(View itemView) {
            thumbnail = itemView.findViewById(R.id.image_search_thumbnail);
            photoName = itemView.findViewById(R.id.text_search_photo_name);
            albumName = itemView.findViewById(R.id.text_search_album_name);
        }
    }
}
