package com.bluewall.spinpong.gles;

import android.opengl.Matrix;

import com.bluewall.spinpong.model.Vec2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Created by david on 12/13/14.
 */
public class Shape {

    private float[] vertices;
    private short[] indices;
    private int vertexOffset;
    private ShortBuffer indexBuffer;
    private static MainRenderer mainRenderer;
    private float x;
    private float y;

    private float[] transformationMatrix = new float[16];
    private float[] bufferMatrix = new float[16];
    private float[] resultMatrix = new float[16];

    private static final float[] IDENTITY_MATRIX = new float [] {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    };

    public static final float[] PERSPECTIVE_MATRIX = new float[] {
            1.1f, 0, 0, 0,
            0, 1.2f, 0, 0,
            0, 0, -1, -1,
            0, 0, -1f, 1
    };

    static {

    }
    final float RESOLUTION_Y = 1440;
    final float RESOLUTION_X = 2560;
    final float RESOLUTION_Z = 2560;

    public Shape(float[] vertices, short[] indices) {
        this.vertices = vertices;
        this.indices = indices;

        for (int i = 0; i < vertices.length; ++i) {
            switch (i%MainRenderer.DIMENSIONS) {
                case 0: vertices[i] = vertices[i] * 2 / RESOLUTION_X; break;
                case 1: vertices[i] = vertices[i] * 2 / RESOLUTION_Y; break;
                case 2: vertices[i] = vertices[i] * 2 / RESOLUTION_Z; break;
            }
        }
    }

    public static void setMainRenderer(MainRenderer renderer) {
        mainRenderer = renderer;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    /*public void translate(float x, float y) {
        mainRenderer.translate(this, x, y);
    }*/

    public float[] getVertices() {
        return vertices;
    }
    public short[] getIndices() {
        return indices;
    }
    public void setVertexOffset(int vertexOffset) {
        this.vertexOffset = vertexOffset;
    }
    public int getVertexOffset() {
        return vertexOffset;
    }
    public ShortBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public void genIndexBuffer() {
        ByteBuffer sByteBuffer = ByteBuffer.allocateDirect(indices.length*2);
        sByteBuffer.order(ByteOrder.nativeOrder());
        indexBuffer = sByteBuffer.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }
    public float[] genTransformationMatrix() {
        System.arraycopy(IDENTITY_MATRIX, 0, transformationMatrix, 0, 16);
        System.arraycopy(IDENTITY_MATRIX, 0, bufferMatrix, 0, 16);
        bufferMatrix[12] += x * 2 / RESOLUTION_X;
        bufferMatrix[13] -= y * 2 / RESOLUTION_Y;
        //mult(transformationMatrix, bufferMatrix);
        Matrix.multiplyMM(resultMatrix, 0, bufferMatrix, 0, transformationMatrix, 0);
        Matrix.multiplyMM(transformationMatrix, 0, PERSPECTIVE_MATRIX, 0, resultMatrix, 0);
        //System.out.println(resultMatrix);\
        /*System.out.println("MAT");
        for (int i = 0; i < 16; ++i) {
            System.out.print(resultMatrix[i] + " ");
            if (i%4 == 3) {
                System.out.println();
            }
        }*/
        return transformationMatrix;
    }


}
