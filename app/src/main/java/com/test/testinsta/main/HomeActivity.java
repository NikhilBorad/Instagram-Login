package com.test.testinsta.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.test.testinsta.BaseAppCompactActivity;
import com.test.testinsta.R;

/**
 * Created by Nikhil-PC on 20/12/17.
 */

public class HomeActivity extends BaseAppCompactActivity{

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.home_activity);
        initialControl();


    }

    private void initialControl() {

        toolbar= (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
    }


}
