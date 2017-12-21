package com.test.testinsta.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by qlooit5 on 6/10/15.
 */
public class TextViewCustom extends TextView {

    public TextViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewCustom(Context context) {
        super(context);
        init();
    }

    public void init() {
        if (!isInEditMode()) {
            Typeface fontLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Light.otf");
            setTypeface(fontLight);
        } else {
            // Log.d(TAG, "TextView");
        }
    }

}
