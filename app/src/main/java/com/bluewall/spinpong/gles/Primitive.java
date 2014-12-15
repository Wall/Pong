package com.bluewall.spinpong.gles;

import android.opengl.GLES20;

import com.bluewall.spinpong.shader.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by david on 12/13/14.
 */
public class Primitive {

    private static final int DIMENSIONS = 3;
    private int program;
    private int positionHandle;
    private int colourHandle;
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    float colour[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Primitive() {};

    public Primitive(float[] points, short[] indices) {
        int vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, ShaderUtils.loadVertexShaderSource());
        int fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderUtils.loadFragmentShaderSource());

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(points.length*4); //float = 4 bytes
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(points);
        vertexBuffer.position(0);

        ByteBuffer sByteBuffer = ByteBuffer.allocateDirect(indices.length*2);
        byteBuffer.order(ByteOrder.nativeOrder());
        indexBuffer = byteBuffer.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

    public void draw() {
        GLES20.glUseProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(positionHandle, 4, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        colourHandle = GLES20.glGetUniformLocation(program, "vColour");

        GLES20.glUniform4fv(colourHandle, 1, colour, 0);

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(),
                GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
