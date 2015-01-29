package com.bluewall.spinpong.View;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by david on 1/29/15.
 */
public class ReleaseImageButton extends ImageButton implements View.OnTouchListener {

    private int defaultResId, downResId;
    private OnReleaseListener releaseListener;

    public ReleaseImageButton(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public ReleaseImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public ReleaseImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    public void setImages(int defaultResId, int downResId) {
        this.defaultResId = defaultResId;
        this.downResId = downResId;
        setBackgroundResource(defaultResId);
    }

    public void setOnReleaseListener(OnReleaseListener releaseListener) {
        this.releaseListener = releaseListener;
    }

    private Rect rect;
    private boolean isDown = false;
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            setBackgroundResource(downResId);
            isDown = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE){
            if (isDown) {
                if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                    setBackgroundResource(defaultResId);
                    isDown = false;
                }
            } else {
                if (rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                    setBackgroundResource(downResId);
                    isDown = true;
                }
            }
        } else if (action == MotionEvent.ACTION_UP && isDown) {
            setBackgroundResource(defaultResId);
            isDown = false;
            if (releaseListener != null)
                releaseListener.onRelease();
        }
        return true;
    }
}
