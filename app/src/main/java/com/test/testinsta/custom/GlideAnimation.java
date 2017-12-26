package com.test.testinsta.custom;

import android.graphics.drawable.Drawable;
import android.view.View;
/**
 * Created by qlooit-9 on 22/12/17.
 */

public interface GlideAnimation<R> {

    interface ViewAdapter {
        /**
         * Returns the wrapped {@link android.view.View}.
         */
        View getView();

        /**
         * Returns the current drawable being displayed in the view, or null if no such drawable exists (or one cannot
         * be retrieved).
         */
        Drawable getCurrentDrawable();

        /**
         * Sets the current drawable (usually an animated drawable) to display in the wrapped view.
         *
         * @param drawable The drawable to display in the wrapped view.
         */
        void setDrawable(Drawable drawable);
    }


    boolean animate(R current, ViewAdapter adapter);
}


