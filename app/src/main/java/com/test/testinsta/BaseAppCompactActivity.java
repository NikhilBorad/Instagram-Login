package com.test.testinsta;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.test.testinsta.db.GalleryDBModel;
import com.test.testinsta.model.GalleryModel;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Nikhil-PC on 20/12/17.
 */

public class BaseAppCompactActivity extends AppCompatActivity implements CONSTANT {

    public SharedPreferences preferencesVal;
    public SharedPreferences.Editor editor;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesVal = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
    }

    public void nbUpdatePref(String key, String val) {
        editor = preferencesVal.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public String nbGetPref(String key, String defVal) {
        return preferencesVal.getString(key, defVal);
    }

    public boolean nbIsNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; ++i) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void nbToast(String val) {
        new Toast(this.getBaseContext());
        Toast customToast = Toast.makeText(this.getBaseContext(), val, Toast.LENGTH_SHORT);
        customToast.setGravity(17, 0, 0);
        customToast.show();
    }

    public static String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
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
    }


}

