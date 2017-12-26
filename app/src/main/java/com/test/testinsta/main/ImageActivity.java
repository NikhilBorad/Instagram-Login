package com.test.testinsta.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.test.testinsta.BaseAppCompactActivity;
import com.test.testinsta.R;
import com.test.testinsta.custom.ImageDownload;
import com.test.testinsta.model.GalleryModel;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Nikhil-PC on 20/12/17.
 */
public class ImageActivity extends BaseAppCompactActivity {


    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 330;
    SubsamplingScaleImageView imageView;
    private Bundle bundle;
    private Bitmap loaded_bitmap;
    private Toolbar toolbar;
    private GalleryModel gallerModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_activity);

        bundle = getIntent().getExtras();
        gallerModel = (GalleryModel) bundle.getSerializable("DATAMODEL");
        intialControl();
    }

    private void intialControl() {

        setUpActionBar();
        imageView = (SubsamplingScaleImageView) findViewById(R.id.imageView);

        if (gallerModel.getLikes().equals("") || gallerModel.getLikes().equals("0"))
            ((TextView) findViewById(R.id.txtLike)).setText("0 Likes");
        else
            ((TextView) findViewById(R.id.txtLike)).setText(gallerModel.getLikes() + " Likes");
        if (gallerModel.getComment_count().equals("") || gallerModel.getComment_count().equals("0"))
            ((TextView) findViewById(R.id.txtComment)).setText("0 Comments");
        else
            ((TextView) findViewById(R.id.txtComment)).setText(gallerModel.getComment_count() + " Comments");

        ((TextView) findViewById(R.id.txtTag)).setText(gallerModel.getTags());
        try {
            getHighResImage info = new getHighResImage();
            info.execute("");
        } catch (Exception e) {
            e.printStackTrace();
            nbToast("Try again");
            finish();
        }

    }

    private void setUpActionBar() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        ((TextView) findViewById(R.id.txtTitle)).setText("Image");
        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.imgFilter).setVisibility(View.GONE);
        findViewById(R.id.imgSave).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.imgSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareForDownload();
            }
        });
    }

    private void prepareForDownload() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            startDownload();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }

    }

    private void startDownload() {

        ImageDownload imageDownload = new ImageDownload();
        imageDownload.DownloadFromUrl(getApplicationContext(), gallerModel.getImgOri(), gallerModel.getImage_id() + "_testInsta");

    }

    public class getHighResImage extends AsyncTask<String, String, String> {


        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ImageActivity.this);
            pDialog.setMessage("Fetching image...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL(gallerModel.getImgOri());
                InputStream is = new BufferedInputStream(url.openStream());
                loaded_bitmap = BitmapFactory.decodeStream(is);

            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing() && pDialog != null) {
                pDialog.dismiss();
            }
            imageView.setImage(ImageSource.bitmap(loaded_bitmap));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareForDownload();
                } else {
                    nbToast("Need your file access to save image");
                }

                break;
        }
    }

}
