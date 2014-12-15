package com.bluewall.spinpong.UI;

import com.bluewall.spinpong.gles.Shape;

/**
 * Created by david on 12/15/14.
 */
public class Pad extends Shape {

    private static final float WIDTH = 32f;
    private static final float HEIGHT = 240f;

    public Pad() {
        super(new float[] { -WIDTH/2, HEIGHT/2, -WIDTH/2, -HEIGHT/2, WIDTH/2, -HEIGHT/2, WIDTH/2, HEIGHT/2 },
              new short[] {0, 1, 2, 0, 3, 2});
    }

}
