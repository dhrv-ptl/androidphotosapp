package com.example.androidphotos;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.androidphotos.model.Photo;

/**
 * Simple ListView adapter for album photo rows.
 */
public class PhotoListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<Photo> photos;

    public PhotoListAdapter(Context context, List<Photo> photos) {
        this.inflater = LayoutInflater.from(context);
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_photo, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Photo photo = photos.get(position);
        holder.photoName.setText(photo.getDisplayName());
        bindThumbnail(holder.photoThumbnail, photo);
        return convertView;
    }

    private void bindThumbnail(ImageView imageView, Photo photo) {
        imageView.setImageDrawable(null);
        imageView.setImageURI(Uri.parse(photo.getUriString()));
        if (imageView.getDrawable() == null) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private static class ViewHolder {
        final ImageView photoThumbnail;
        final TextView photoName;

        ViewHolder(View itemView) {
            photoThumbnail = itemView.findViewById(R.id.image_photo_thumbnail);
            photoName = itemView.findViewById(R.id.text_photo_name);
        }
    }
}
