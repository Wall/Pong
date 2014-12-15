package com.bluewall.spinpong.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by david on 12/15/14.
 */
public class Field extends ImageView {

    public Field(Context context) {
        super(context);
        init();
    }

    public Field(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Field(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
    }
}
