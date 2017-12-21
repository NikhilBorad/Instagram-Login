package com.test.testinsta.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.testinsta.BaseAppCompactActivity;
import com.test.testinsta.R;
import com.test.testinsta.helper.InstagramApp;

import java.util.HashMap;

/**
 * Created by qlooit-9 on 20/12/17.
 */

public class ProfileActivity extends BaseAppCompactActivity {

    private HashMap<String, String> userInfo;
    private Toolbar toolbar;
    private TextView txtUserName, txtUserFullName, txtUserBio, txtUserWebsite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);
        userInfo = (HashMap<String, String>) getIntent().getSerializableExtra(
                "userInfo");


        intialControl();

    }

    private void intialControl() {

        setUpActionBar();
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserFullName = (TextView) findViewById(R.id.txtUserFullName);
        txtUserBio = (TextView) findViewById(R.id.txtUserBio);
        txtUserWebsite = (TextView) findViewById(R.id.txtUserWebsite);

        setDataToView();

    }

    private void setUpActionBar() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        ((TextView) findViewById(R.id.txtTitle)).setText("Profile");
        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void setDataToView() {

        txtUserName.setText(userInfo.get(InstagramApp.TAG_USERNAME));
        txtUserFullName.setText(userInfo.get(InstagramApp.TAG_FULL_NAME));
        txtUserBio.setText(userInfo.get(InstagramApp.TAG_BIO));
        txtUserWebsite.setText(userInfo.get(InstagramApp.TAG_WEBSITE));

    }
}
