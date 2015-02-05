package com.bluewall.spinpong.UI;

import android.util.Log;

/**
 * Created by dlee on 5/02/2015.
 */
public enum Path {

    INSTANCE;

    public static final String TAG = "Path";

    private float x;
    private float y;
    private float speedX;
    private float speedY;
    private float spin;

    private float[] position = new float[] {0, 0};

    private Path() {
    }

    public void setInitialParameters(float x, float y, float speedX, float speedY, float spin) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.spin = spin;
    }

    public float[] getPosition(float t) {
        return getPosition(t, x, y, speedX, speedY, spin);
    }

    private float[] getPosition(float t, float x, float y, float speedX, float speedY, float spin) {
        //TODO: implement this properly
        //Currently ignores spin


        float rightWallHit = (ScreenInfo.RATIO - x)/speedX;
        if (t > rightWallHit) {
            Log.i(TAG, "RightWall");
            return getPosition( t - rightWallHit, ScreenInfo.RATIO, y + rightWallHit*speedX, -speedX, speedY, spin);
        }

        float leftWallHit = -(ScreenInfo.RATIO + x)/speedX;
        if (t > leftWallHit) {
            Log.i(TAG, "LeftWall");
            return getPosition( t - leftWallHit, -ScreenInfo.RATIO, y + leftWallHit*speedX, -speedX, speedY, spin);
        }

        float bottomWallHit = -(1 + y)/speedY;
        if (t > bottomWallHit && bottomWallHit > 0) {
            Log.i(TAG, "BottomWall");
            return getPosition( t - bottomWallHit, x + bottomWallHit*speedX, -1, speedX, -speedY, spin);
        }

        float topWallHit = (1 - y)/speedY;
        if (t > topWallHit && topWallHit > 0) {
            Log.i(TAG, "TopWall");
            return getPosition( t - topWallHit, x + topWallHit*speedX, 1, speedX, -speedY, spin);
        }


        position[0] = x + t*speedX;
        position[1] = y + t*speedY;

        return position;
    }
}
