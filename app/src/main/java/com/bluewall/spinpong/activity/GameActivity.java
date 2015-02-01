package com.bluewall.spinpong.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.bluewall.spinpong.UI.Ball;
import com.bluewall.spinpong.UI.Pad;
import com.bluewall.spinpong.UI.ScreenInfo;
import com.bluewall.spinpong.gles.MainRenderer;
import com.bluewall.spinpong.gles.MainSurfaceView;
import com.bluewall.spinpong.gles.OnGlobalTouchListener;
import com.bluewall.spinpong.gles.Shape;

/**
 * Created by david on 1/29/15.
 */
public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        //final int screenWidth = displayMetrics.widthPixels;
        //final int screenHeight = displayMetrics.heightPixels;

        ScreenInfo.RES_X = displayMetrics.widthPixels;
        ScreenInfo.RES_Y = displayMetrics.heightPixels;
        ScreenInfo.RATIO = ((float) ScreenInfo.RES_X)/ ((float) ScreenInfo.RES_Y);

        ActivityManager am  = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info  = am.getDeviceConfigurationInfo();

        boolean supportsES2 = (info.reqGlEsVersion >= 0x20000);
        if (supportsES2) {
            MainRenderer mainRenderer = new MainRenderer(getApplicationContext());

            MainSurfaceView mainSurfaceView = new MainSurfaceView(this);
            mainSurfaceView.setEGLContextClientVersion(2);
            mainSurfaceView.setRenderer(mainRenderer);
            this.setContentView(mainSurfaceView);

            //mainRenderer.loadTexture(getApplicationContext(), R.drawable.basic_texture);

            final Shape shape = new Shape(new float[] {
                    -50, 0, 0,
                    0, 0, 0,
                    0, -50, 0},
                    new short[] {
                    0, 1, 2
                    }, new float[] {});
            final Pad pad = new Pad();
            final Ball ball = new Ball(0, 0);
            ball.setPad(pad);
            //final Ball ball2 = new Ball(1f, 1f);
            //ball2.setPad(pad);
            //mainRenderer.add(shape2);
            //mainRenderer.add(shape);
            mainRenderer.add(pad);
            mainRenderer.add(ball);
            //mainRenderer.add(ball2);
            mainRenderer.init();
            //pad.translate(0, 0.7f);
            mainSurfaceView.setOnGlobalTouchListener(new OnGlobalTouchListener() {
                @Override
                public boolean onTouch(MotionEvent event) {
                    //if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        //System.out.println("DOWN: " + event.getRawX() + ", " + event.getRawY());
                        pad.set((2*event.getRawX() - ScreenInfo.RES_X)/ScreenInfo.RES_Y, (-2*event.getRawY() + ScreenInfo.RES_Y)/ScreenInfo.RES_Y);
                    //}
                    return true;
                }
            });

        } else {
            Log.e("OpenGLES 2", "Device does not support ES2.");
        }
    }
}
