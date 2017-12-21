package com.test.testinsta.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.testinsta.BaseAppCompactActivity;
import com.test.testinsta.R;
import com.test.testinsta.adapter.MyGridListAdapter;
import com.test.testinsta.db.GalleryDBModel;
import com.test.testinsta.helper.InstagramApp;
import com.test.testinsta.helper.InstagramSession;
import com.test.testinsta.jsonHelper.JSONParser;
import com.test.testinsta.model.GalleryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nikhil-PC on 20/12/17.
 */

public class PhotosActivity extends BaseAppCompactActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private GridView gvAllImages;
    private HashMap<String, String> userInfo;
    private ArrayList<String> imageThumbList = new ArrayList<String>();
    private Context context;
    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private ProgressDialog pd;
    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_STD_RESOL = "standard_resolution";
    public static final String TAG_URL = "url";
    private ArrayList<GalleryModel> imageModelList = new ArrayList<>();

    private Handler handler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (pd != null && pd.isShowing())
                pd.dismiss();
            if (msg.what == WHAT_FINALIZE) {
                setImageGridAdapter();
            } else {
                Toast.makeText(context, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity);

        intialControl();
    }

    private void intialControl() {

        context = PhotosActivity.this;
        setUpActionBar();
        userInfo = (HashMap<String, String>) getIntent().getSerializableExtra(
                "userInfo");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        gvAllImages = (GridView) findViewById(R.id.gvAllImages);
        gvAllImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                intent.putExtra("DATAMODEL", imageModelList.get(i));
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageModelList.size() == 0) {
                    nbToast("Oops! No data available");
                } else {
                    startActivity(new Intent(PhotosActivity.this, MapActivity.class));
                }
            }
        });

        getAllMediaImages();

    }

    private void setUpActionBar() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        ((TextView) findViewById(R.id.txtTitle)).setText("Photos");
        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void setImageGridAdapter() {
        gvAllImages.setAdapter(new MyGridListAdapter(context, imageThumbList));
    }

    private void getAllMediaImages() {
        pd = ProgressDialog.show(context, "", "Loading images...");
        GalleryDBModel.deleteAll(GalleryDBModel.class);
        if (nbIsNetworkAvailable(getApplicationContext())) {
//            GalleryDBModel.
            new Thread(new Runnable() {

                @Override
                public void run() {
                    int what = WHAT_FINALIZE;
                    try {
                        // URL url = new URL(mTokenUrl + "&code=" + code);
                        JSONParser jsonParser = new JSONParser();
//					JSONObject jsonObject = jsonParser
//							.getJSONFromUrlByGet("https://api.instagram.com/v1/users/"
//									+ userInfo.get(InstagramApp.TAG_ID)
//									+ "/media/recent/?client_id="
//									+ ApplicationData.CLIENT_ID
//									+ "&count="
//									+ userInfo.get(InstagramApp.TAG_COUNTS));

                        InstagramSession mSession = new InstagramSession(context);
//					mAccessToken = mSession.getAccessToken();

                        JSONObject jsonObject = jsonParser
                                .getJSONFromUrlByGet("https://api.instagram.com/v1/users/self/media/recent/?access_token="
                                        + mSession.getAccessToken()
                                        + "&count="
                                        + userInfo.get(InstagramApp.TAG_COUNTS));

                        JSONArray data = jsonObject.getJSONArray(TAG_DATA);
                        for (int data_i = 0; data_i < data.length(); data_i++) {
                            JSONObject data_obj = data.getJSONObject(data_i);

                            JSONObject images_obj = data_obj
                                    .getJSONObject(TAG_IMAGES);

                            JSONObject thumbnail_obj = images_obj
                                    .getJSONObject(TAG_THUMBNAIL);

                            // String str_height =
                            // thumbnail_obj.getString(TAG_HEIGHT);
                            //
                            // String str_width =
                            // thumbnail_obj.getString(TAG_WIDTH);

                            String thumb_url = thumbnail_obj.getString(TAG_URL);
                            String original_url = images_obj.getJSONObject(TAG_STD_RESOL).getString(TAG_URL);
                            imageThumbList.add(thumb_url);
                            GalleryModel galleryModel = new GalleryModel();
                            galleryModel.setId(data_obj.getString("id"));
                            galleryModel.setImgThumb(thumb_url);
                            galleryModel.setImgOri(original_url);
                            galleryModel.setComment_count(data_obj.getJSONObject("comments").getString("count"));
                            galleryModel.setCaption(data_obj.getString("caption"));
                            galleryModel.setLikes(data_obj.getJSONObject("likes").getString("count"));
                            try {
                                galleryModel.setLocation_lat(data_obj.getJSONObject("location").getString("latitude"));
                                galleryModel.setLocation_long(data_obj.getJSONObject("location").getString("longitude"));
                            } catch (Exception e) {
                                galleryModel.setLocation_lat("");
                                galleryModel.setLocation_long("");
                            }
                            addToDb(galleryModel);
                            imageModelList.add(galleryModel);
                        }

                        System.out.println("jsonObject::" + jsonObject);

                    } catch (Exception exception) {
                        exception.printStackTrace();
                        what = WHAT_ERROR;
                    }
                    // pd.dismiss();
                    handler.sendEmptyMessage(what);
                }
            }).start();
        } else {
            List<GalleryDBModel> listData = GalleryDBModel.listAll(GalleryDBModel.class);
            for (int i = 0; i < listData.size(); i++) {
                GalleryModel galleryModel = new GalleryModel();
                galleryModel.setId(listData.get(i).getImage_id());
                galleryModel.setImgThumb(listData.get(i).getImgThumb());
                galleryModel.setImgOri(listData.get(i).getImgOri());
                galleryModel.setComment_count(listData.get(i).getComment_count());
                galleryModel.setCaption(listData.get(i).getCaption());
                galleryModel.setLikes(listData.get(i).getLikes());
                galleryModel.setLocation_lat(listData.get(i).getLocation_lat());
                galleryModel.setLocation_long(listData.get(i).getLocation_long());
                imageModelList.add(galleryModel);
            }
            handler.sendEmptyMessage(WHAT_FINALIZE);
        }

    }

}
