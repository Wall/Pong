package com.bluewall.spinpong;

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
 * Created by david on 12/13/14.
 */
public class MainActivity extends Activity {

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

        ActivityManager am  = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info  = am.getDeviceConfigurationInfo();

        boolean supportsES2 = (info.reqGlEsVersion >= 0x20000);
        if (supportsES2) {
            MainRenderer mainRenderer = new MainRenderer();

            MainSurfaceView mainSurfaceView = new MainSurfaceView(this);
            mainSurfaceView.setEGLContextClientVersion(2);
            mainSurfaceView.setRenderer(mainRenderer);
            this.setContentView(mainSurfaceView);

            Shape.setMainRenderer(mainRenderer);

            final Shape shape = new Shape(new float[] {
                    -50, 0, 0,
                    0, 0, 0,
                    0, -50, 0},
                    new short[] {
                    0, 1, 2
                    }, new float[] {});
            /*final Shape shape2 = new Shape(new float[] {
                    0.23f,      0.85f,
                    0.11f,      1.13f,
                    0.375f,  1.2f},
                    new short[] {
                            0, 1, 2
                    });*/
            final Pad pad = new Pad();
            final Ball ball = new Ball();

            //mainRenderer.add(shape2);
            mainRenderer.add(shape);
            mainRenderer.add(pad);
            mainRenderer.add(ball);
            mainRenderer.init();
            //pad.translate(0, 0.7f);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < 100; ++i) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //shape.translate(-0.006f, -0.01f);
                        //shape.set(-0.006f*i, -0.01f*i);
                        //pad.set(7*i, 7*i);
                    }
                }
            }).start();
            mainSurfaceView.setOnGlobalTouchListener(new OnGlobalTouchListener() {
                @Override
                public boolean onTouch(MotionEvent event) {
                    //if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        //System.out.println("DOWN: " + event.getRawX() + ", " + event.getRawY());
                        pad.set(-event.getRawX() + ScreenInfo.RES_X/2 + 250, event.getRawY() - ScreenInfo.RES_Y/2);
                    //}
                    return true;
                }
            });

        } else {
            Log.e("OpenGLES 2", "Device does not support ES2.");
        }
    }
}
