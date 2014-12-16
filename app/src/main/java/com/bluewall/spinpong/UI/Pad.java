package com.bluewall.spinpong.UI;

import com.bluewall.spinpong.gles.Shape;

/**
 * Created by david on 12/15/14.
 */
public class Pad extends Shape {

    private static final float WIDTH = 32f;
    private static final float HEIGHT = 240f;
    private static final float DEPTH = 120f;

    public Pad() {
        super(new float[] {
                       -WIDTH/2,  HEIGHT/2, 0,      //0
                       -WIDTH/2, -HEIGHT/2, 0,      //1
                        WIDTH/2, -HEIGHT/2, 0,      //2
                        WIDTH/2,  HEIGHT/2, 0,      //3
                       -WIDTH/2,  HEIGHT/2, -DEPTH, //4
                       -WIDTH/2, -HEIGHT/2, -DEPTH, //5
                        WIDTH/2, -HEIGHT/2, -DEPTH, //6
                        WIDTH/2,  HEIGHT/2, -DEPTH  //7
                },
              new short[] {
                      0, 1, 2,
                      0, 3, 2,
                      4, 5, 6,
                      4, 7, 6,
                      0, 4, 5,
                      0, 1, 5,
                      3, 7, 6,
                      3, 2, 6,
                      0, 4, 1,
                      0, 3, 1,
                      1, 5, 6,
                      1, 2, 6
                });
    }

}
