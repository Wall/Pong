package com.bluewall.spinpong.UI;

import com.bluewall.spinpong.gles.Shape;

/**
 * Created by david on 12/15/14.
 */
public class Pad extends Shape {

    public static final float WIDTH = 0.02f;
    public static final float HEIGHT = 0.25f;
    public static final float DEPTH = 0.2f;

    public Pad() {

        super(new float[] {
                        -WIDTH/2, HEIGHT/2, -DEPTH,
                        -WIDTH/2, -HEIGHT/2, -DEPTH,
                        WIDTH/2, -HEIGHT/2, -DEPTH,
                        WIDTH/2, HEIGHT/2, -DEPTH,

                        -WIDTH/2, HEIGHT/2, 0,
                        -WIDTH/2, -HEIGHT/2, 0,
                        WIDTH/2, -HEIGHT/2, 0,
                        WIDTH/2, HEIGHT/2, 0,

/*                        -WIDTH/2, HEIGHT/2,0,
                        WIDTH/2, HEIGHT/2,0,
                        WIDTH/2, -HEIGHT/2,0,
                        -WIDTH/2, -HEIGHT/2,0,
*/
                        -WIDTH/2, HEIGHT/2, -DEPTH,
                        -WIDTH/2, HEIGHT/2, 0,
                        -WIDTH/2, -HEIGHT/2, 0,
                        -WIDTH/2, -HEIGHT/2, -DEPTH,

                        -WIDTH/2, -HEIGHT/2, -DEPTH,
                        -WIDTH/2, -HEIGHT/2, 0,
                        WIDTH/2, -HEIGHT/2, 0,
                        WIDTH/2, -HEIGHT/2, -DEPTH,

                        WIDTH/2, -HEIGHT/2, -DEPTH,
                        WIDTH/2, -HEIGHT/2, 0,
                        WIDTH/2, HEIGHT/2, 0,
                        WIDTH/2, HEIGHT/2, -DEPTH,

                        -WIDTH/2, HEIGHT/2, 0,
                        -WIDTH/2, HEIGHT/2, -DEPTH,
                        WIDTH/2, HEIGHT/2, -DEPTH,
                        WIDTH/2, HEIGHT/2, 0
                },
              new short[] {
                      //0, 1, 2,
                      //0, 3, 2,
                      4, 5, 6,
                      4, 7, 6,
                      8, 9, 10,
                      8, 11, 10,
                      12, 13, 14,
                      12, 15, 14,
                      16, 17, 18,
                      16, 19, 18,
                      20, 21, 22,
                      20, 23, 22
              },
              new float[] {
                      0.0f, 0.0f, 1.0f,
                      0.0f, 0.0f, 1.0f,
                      0.0f, 0.0f, 1.0f,
                      0.0f, 0.0f, 1.0f,

                      0.0f, 0.0f, -1.0f,
                      0.0f, 0.0f, -1.0f,
                      0.0f, 0.0f, -1.0f,
                      0.0f, 0.0f, -1.0f,

                      1.0f, 0.0f, -0.0f,
                      1.0f, 0.0f, -0.0f,
                      1.0f, 0.0f, -0.0f,
                      1.0f, 0.0f, -0.0f,

                      -0.0f, 1.0f, 0.0f,
                      -0.0f, 1.0f, 0.0f,
                      -0.0f, 1.0f, 0.0f,
                      -0.0f, 1.0f, 0.0f,

                      -1.0f, -0.0f, 0.0f,
                      -1.0f, -0.0f, 0.0f,
                      -1.0f, -0.0f, 0.0f,
                      -1.0f, -0.0f, 0.0f,

                      0.0f, -1.0f, -0.0f,
                      0.0f, -1.0f, -0.0f,
                      0.0f, -1.0f, -0.0f,
                      0.0f, -1.0f, -0.0f
              }
        );
    }
}
