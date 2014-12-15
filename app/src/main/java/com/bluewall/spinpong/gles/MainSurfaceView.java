package com.bluewall.spinpong.gles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by david on 12/13/14.
 */
public class MainSurfaceView extends GLSurfaceView {

    private OnGlobalTouchListener onGlobalTouchListener;

    public MainSurfaceView(Context context) {
        super(context);
    }

    public MainSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnGlobalTouchListener(OnGlobalTouchListener onGlobalTouchListener) {
        this.onGlobalTouchListener = onGlobalTouchListener;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event != null) {
            //if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //   System.out.println("DOWN");
            //}
            if (onGlobalTouchListener != null) {
                return onGlobalTouchListener.onTouch(event);
            }
        }

        return super.onTouchEvent(event);
    }

}
