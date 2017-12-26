package com.test.testinsta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.testinsta.R;
import com.test.testinsta.model.GalleryModel;

import java.util.ArrayList;

/**
 * Created by Nikhil-PC on 22/12/17.
 */

public class LikesListAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<GalleryModel> galleryModelArrayList;
    private LayoutInflater layoutInflater;


    public LikesListAdapter(Context context, ArrayList<GalleryModel> string) {
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
        galleryModelArrayList = string;

    }

    @Override
    public int getCount() {
        return galleryModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.like_list_view, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imgPic = (ImageView) view.findViewById(R.id.imgPic);
            viewHolder.tvLike = (TextView) view.findViewById(R.id.tvLike);
            viewHolder.tvComment = (TextView) view.findViewById(R.id.tvComment);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvLike.setText(galleryModelArrayList.get(position).getLikes());
        viewHolder.tvComment.setText(galleryModelArrayList.get(position).getComment_count());

        Glide.with(mContext)
                .load(galleryModelArrayList.get(position).getImgOri())
                .into(viewHolder.imgPic);

        return view;
    }


    static class ViewHolder {
        TextView tvLike, tvComment;
        ImageView imgPic;
    }
}
