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

            final Pad pad = new Pad();
            final Ball ball = new Ball(0, 0);
            ball.setPad(pad);
            mainRenderer.add(pad);
            mainRenderer.add(ball);
            mainRenderer.init();
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
