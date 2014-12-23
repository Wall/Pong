package com.bluewall.spinpong.UI;

import com.bluewall.spinpong.gles.Shape;

/**
 * Created by david on 12/15/14.
 */
public class Pad extends Shape {

    private static final float WIDTH = 32f;
    private static final float HEIGHT = 240f;
    private static final float DEPTH = 120f;

    private static final float F = 0.759835686f;

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
    /*
        new float[] {
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
                }, new float[] {
                        -F,F,F,
                        -F,-F,F,
                        F,-F,F,
                        F,F,F,
                        -F,F,-F,
                        -F,-F,-F,
                        F,-F,-F,
                        F,F,-F
                }
     */

}
