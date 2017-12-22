package com.test.testinsta.custom;

import android.os.AsyncTask;
import android.util.Log;

import com.test.testinsta.db.GalleryDBModel;
import com.test.testinsta.model.GalleryModel;

import java.util.List;

/**
 * Created by Nikhil-PC on 22/12/17.
 */

public class SyncAllPost extends AsyncTask<List<GalleryModel>, String, String> {

    @Override
    protected String doInBackground(List<GalleryModel>[] lists) {

        List<GalleryDBModel> oldData = GalleryDBModel.listAll(GalleryDBModel.class);

        if (oldData.size() == 0) {
            for (int i = 0; i < lists[0].size(); i++) {
                addToDb(lists[0].get(i));
            }
        } else {
            for (int i = 0; i < lists[0].size(); i++) {
                if (oldData.get(i).getImage_id().equals(lists[0].get(i).getId())) {
//                    Log.e("DB", "CONTAIN");
                } else {
                    addToDb(lists[0].get(i));
                }
            }
        }
        return "DONE";
    }

    @Override
    protected void onPostExecute(String s) {
//        Log.e("DB", "DONE Save");
        super.onPostExecute(s);
    }

    public void addToDb(GalleryModel galleryModel) {
        GalleryDBModel galleryDBModel = new GalleryDBModel();
        galleryDBModel.setImage_id(galleryModel.getId());
        galleryDBModel.setImgOri(galleryModel.getImgOri());
        galleryDBModel.setImgThumb(galleryModel.getImgThumb());
        galleryDBModel.setCaption(galleryModel.getCaption());
        galleryDBModel.setComment_count(galleryModel.getComment_count());
        galleryDBModel.setLikes(galleryModel.getLikes());
        galleryDBModel.setLocation_lat(galleryModel.getLocation_lat());
        galleryDBModel.setLocation_long(galleryModel.getLocation_long());
        galleryDBModel.save();
//        Log.e("DB", "ADD");
    }
}
