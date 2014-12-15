package com.bluewall.spinpong.gles;

/**
 * Created by david on 12/13/14.
 */
public class Triangle extends Primitive {

    public Triangle() {
        super(new float[] {
                1.0f, 1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f
        },
              new short[] {
                0, 1, 2
              });
    }
}
