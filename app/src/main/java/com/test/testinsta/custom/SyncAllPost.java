package com.test.testinsta.custom;

import android.os.AsyncTask;
import android.util.Log;

import com.test.testinsta.db.GalleryDBModel;
import com.test.testinsta.model.GalleryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikhil-PC on 22/12/17.
 */

public class SyncAllPost extends AsyncTask<List<GalleryModel>, String, String> {

    @Override
    protected String doInBackground(List<GalleryModel>[] listsAllData) {

        List<GalleryDBModel> oldData = GalleryDBModel.listAll(GalleryDBModel.class);
        if (oldData.size() == 0) {
            for (int i = 0; i < listsAllData[0].size(); i++) {
                addToDb(listsAllData[0].get(i));
            }
        } else {
            ArrayList<String> old_id = getOldImageId(oldData);
            ArrayList<String> new_id = getNewImageId(listsAllData[0]);

            for (int i = 0; i < new_id.size(); i++) {
                if (old_id.contains(new_id.get(i))) {
//                    Log.e("DB","YES");
                } else {
//                    Log.e("DB","NO");
                    addToDb(listsAllData[0].get(i));
                }
            }
        }
        return "DONE";
    }

    private ArrayList<String> getOldImageId(List<GalleryDBModel> oldData) {

        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < oldData.size(); i++) {
            temp.add(oldData.get(i).getImage_id());
        }
        return temp;

    }

    private ArrayList<String> getNewImageId(List<GalleryModel> oldData) {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < oldData.size(); i++) {
            temp.add(oldData.get(i).getImage_id());
        }
        return temp;

    }

    @Override
    protected void onPostExecute(String s) {
//        Log.e("DB", "DONE Save");
        super.onPostExecute(s);
    }

    public void addToDb(GalleryModel galleryModel) {
        GalleryDBModel galleryDBModel = new GalleryDBModel();
        galleryDBModel.setImage_id(galleryModel.getImage_id());
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
