package com.test.testinsta.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.test.testinsta.BaseAppCompactActivity;
import com.test.testinsta.R;
import com.test.testinsta.adapter.MyGridListAdapter;
import com.test.testinsta.custom.SyncAllPost;
import com.test.testinsta.db.GalleryDBModel;
import com.test.testinsta.helper.InstagramApp;
import com.test.testinsta.helper.InstagramSession;
import com.test.testinsta.jsonHelper.JSONParser;
import com.test.testinsta.model.GalleryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nikhil-PC on 20/12/17.
 */

public class PhotosActivity extends BaseAppCompactActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 600;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private GridView gvAllImages;
    private HashMap<String, String> userInfo;
    private Context context;
    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private ProgressDialog pd;
    private List<GalleryModel> imageModelList = new ArrayList<>();
    private GoogleApiClient googleApiClient;
    private Location lastLocation = null;

    private Handler handler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (pd != null && pd.isShowing())
                pd.dismiss();
            if (msg.what == WHAT_FINALIZE) {
                new SyncAllPost().execute(imageModelList);
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
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
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
        ((ImageView) findViewById(R.id.imgFilter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFilterClick();
            }
        });

    }

    private void setImageGridAdapter() {
        gvAllImages.setAdapter(new MyGridListAdapter(context, imageModelList));
    }

    private void getAllMediaImages() {
        pd = ProgressDialog.show(context, "", "Loading images...");
        if (nbIsNetworkAvailable(getApplicationContext())) {
//            GalleryDBModel.deleteAll(GalleryDBModel.class);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    int what = WHAT_FINALIZE;
                    try {
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
                            GalleryModel galleryModel = new GalleryModel();
                            galleryModel.setImage_id(data_obj.getString("id"));
                            galleryModel.setImgThumb(thumb_url);
                            galleryModel.setImgOri(original_url);
                            galleryModel.setComment_count(data_obj.getJSONObject("comments").getString("count"));
                            galleryModel.setCaption(data_obj.getString("caption"));
                            galleryModel.setLikes(data_obj.getJSONObject("likes").getString("count"));
                            galleryModel.setTags(getTagText(data_obj.getJSONArray("tags")));
                            try {
                                galleryModel.setLocation_lat(data_obj.getJSONObject("location").getString("latitude"));
                                galleryModel.setLocation_long(data_obj.getJSONObject("location").getString("longitude"));
                            } catch (Exception e) {
                                galleryModel.setLocation_lat("0.0");
                                galleryModel.setLocation_long("0.0");
                            }
                            imageModelList.add(galleryModel);
                        }

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
                galleryModel.setImage_id(listData.get(i).getImage_id());
                galleryModel.setImgThumb(listData.get(i).getImgThumb());
                galleryModel.setImgOri(listData.get(i).getImgOri());
                galleryModel.setComment_count(listData.get(i).getComment_count());
                galleryModel.setCaption(listData.get(i).getCaption());
                galleryModel.setLikes(listData.get(i).getLikes());
                galleryModel.setTags(listData.get(i).getTags());
                galleryModel.setLocation_lat(listData.get(i).getLocation_lat());
                galleryModel.setLocation_long(listData.get(i).getLocation_long());
                imageModelList.add(galleryModel);
            }
            handler.sendEmptyMessage(WHAT_FINALIZE);
        }

    }

    private String getTagText(JSONArray tags) {

        String tag_text = "";
        for (int i = 0; i < tags.length(); i++) {
            try {
                if (i == tags.length() - 1) {
                    tag_text = tag_text + "#" + tags.get(i);
                } else {
                    tag_text = tag_text + "#" + tags.get(i)+", ";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tag_text;

    }

    private void onFilterClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            applyFilter();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    private void applyFilter() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            nbToast("Permission denied");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation == null) {
            nbToast("Permission denied");
        } else {
            imageModelList = sortLocations(imageModelList, lastLocation.getLatitude(), lastLocation.getLongitude());
            nbToast("Filter applied");
            setImageGridAdapter();
        }

    }

    public static List<GalleryModel> sortLocations(List<GalleryModel> locations, final double myLatitude, final double myLongitude) {
        Comparator comp = new Comparator<GalleryModel>() {
            @Override
            public int compare(GalleryModel o, GalleryModel o2) {
                float[] result1 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, Double.parseDouble(o.getLocation_lat()), Double.parseDouble(o.getLocation_long()), result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, Double.parseDouble(o2.getLocation_lat()), Double.parseDouble(o2.getLocation_long()), result2);
                Float distance2 = result2[0];

                return distance1.compareTo(distance2);
            }
        };

        Collections.sort(locations, comp);
        return locations;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onFilterClick();
                } else {
                    nbToast("Need your location to filter pins");
                }

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
