package com.test.testinsta.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.intrusoft.library.FrissonView;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.test.testinsta.BaseAppCompactActivity;
import com.test.testinsta.R;
import com.test.testinsta.custom.BuilderManager;
import com.test.testinsta.helper.InstagramApp;

import java.util.HashMap;

/**
 * Created by Nikhil-PC on 20/12/17.
 */

public class MainActivity extends BaseAppCompactActivity implements OnClickListener {

    private InstagramApp mApp;
    private Button btnConnect;
    private LinearLayout loginBtnLayout, llAfterLoginView;
    private FrissonView wave_head;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private ImageView imgUserProfile;
    private TextView txtUserName, txtUserFollowers, txtUserFollowing, txtUserPost;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mApp = new InstagramApp(this, IG_CLIENT,
                IG_CLIENT_SECRET, CALLBACKURL);

        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                loginBtnLayout.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                llAfterLoginView.setVisibility(View.VISIBLE);
                // userInfoHashmap = mApp.
                mApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });
        intialControl();
        bindEventHandlers();

        if (mApp.hasAccessToken()) {
            loginBtnLayout.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            llAfterLoginView.setVisibility(View.VISIBLE);
            mApp.fetchUserName(handler);

        }

    }

    private void intialControl() {
        loginBtnLayout = (LinearLayout) findViewById(R.id.loginBtnLayout);
        llAfterLoginView = (LinearLayout) findViewById(R.id.llAfterLoginView);
        wave_head = (FrissonView) findViewById(R.id.wave_head);
        imgUserProfile = (ImageView) findViewById(R.id.imgUserProfile);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserFollowers = (TextView) findViewById(R.id.txtUserFollowers);
        txtUserFollowing = (TextView) findViewById(R.id.txtUserFollowing);
        txtUserPost = (TextView) findViewById(R.id.txtUserPost);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        BoomMenuButton bmb = (BoomMenuButton) findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.addBuilder(BuilderManager.getMenu1());
        bmb.addBuilder(BuilderManager.getMenu2());
        bmb.addBuilder(BuilderManager.getMenu3());

        bmb.setOnBoomListener(new OnBoomListener() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
                if (index == 0) {
                    if (nbIsNetworkAvailable(getApplicationContext())) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class)
                                .putExtra("userInfo", userInfoHashmap));
                    } else {
                        nbToast(getString(R.string.no_internet));
                    }
                } else if (index == 1) {
                    if (nbIsNetworkAvailable(getApplicationContext())) {
                        startActivity(new Intent(MainActivity.this, PhotosActivity.class)
                                .putExtra("userInfo", userInfoHashmap));
                    } else {
                        nbToast(getString(R.string.no_internet));
                    }
                } else if (index == 2) {
                    connectOrDisconnectUser();
                }
            }

            @Override
            public void onBackgroundClick() {

            }

            @Override
            public void onBoomWillHide() {

            }

            @Override
            public void onBoomDidHide() {

            }

            @Override
            public void onBoomWillShow() {

            }

            @Override
            public void onBoomDidShow() {

            }
        });

    }

    private void bindEventHandlers() {
        btnConnect.setOnClickListener(this);
    }

    private Handler handler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
                displayInfoDialogView();
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(MainActivity.this, "Check your network.Try again",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    public void onClick(View v) {
        if (v == btnConnect) {
            connectOrDisconnectUser();
        }
    }

    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
                                    loginBtnLayout.setVisibility(View.VISIBLE);
                                    toolbar.setVisibility(View.GONE);
                                    llAfterLoginView.setVisibility(View.GONE);
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }

    private void displayInfoDialogView() {

        Glide.with(getApplicationContext())
                .load(userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE))
                .into(imgUserProfile);
        txtUserName.setText(userInfoHashmap.get(InstagramApp.TAG_FULL_NAME));
        txtUserPost.setText("Total Posts: " + userInfoHashmap.get(InstagramApp.TAG_MEDIA));
        txtUserFollowing.setText("Following: " + userInfoHashmap.get(InstagramApp.TAG_FOLLOWS));
        txtUserFollowers.setText("Followers: " + userInfoHashmap.get(InstagramApp.TAG_FOLLOWED_BY));
//        Glide.with(this)
//                .asBitmap()
//                .load(userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE))
//                .into(new SimpleTarget<Bitmap>(SimpleTarget.SIZE_ORIGINAL, SimpleTarget.SIZE_ORIGINAL) {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
//                        wave_head.setBitmap(bitmap);
//                    }
//                });

    }
}