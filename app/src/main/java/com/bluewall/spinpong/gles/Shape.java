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
    private float[] normals;
    private int vertexOffset;
    private ShortBuffer indexBuffer;
    private static MainRenderer mainRenderer;

    protected float x;
    protected float y;
    protected float rotX;
    protected float rotY;
    protected float rotZ;

    protected float[] transformationMatrix = new float[16];
    protected float[] bufferMatrix = new float[16];
    protected float[] resultMatrix = new float[16];

    protected static final float[] IDENTITY_MATRIX = new float [] {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    };

    static {

    }
    protected final float RESOLUTION_Y = 1440;
    protected final float RESOLUTION_X = 1440;
    protected final float RESOLUTION_Z = 1440;

    public Shape(float[] vertices, short[] indices, float[] normals) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;

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
    public void add(float x, float y) {
        this.x += x;
        this.y += y;
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
    public float[] getNormals() {
        return normals;
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
        System.arraycopy(IDENTITY_MATRIX, 0, bufferMatrix, 0, 16);

        /*bufferMatrix[0] = (float) Math.cos(rotZ);
        bufferMatrix[5] = bufferMatrix[0];
        bufferMatrix[4] = (float) -Math.sin(rotZ);
        bufferMatrix[1] = -bufferMatrix[4];*/


        /*bufferMatrix[0] = (float) Math.cos(rotY);
        bufferMatrix[10] = bufferMatrix[0];
        bufferMatrix[2] = (float) Math.sin(rotY);
        bufferMatrix[8] = -bufferMatrix[2];*/

        bufferMatrix[5] = (float) Math.cos(rotX);
        bufferMatrix[10] = bufferMatrix[5];
        bufferMatrix[9] = (float) Math.sin(rotX);
        bufferMatrix[6] = -bufferMatrix[9];

        Matrix.multiplyMM(transformationMatrix, 0, bufferMatrix, 0, resultMatrix, 0);

        System.arraycopy(IDENTITY_MATRIX, 0, bufferMatrix, 0, 16);

        bufferMatrix[0] = (float) Math.cos(rotY);
        bufferMatrix[10] = bufferMatrix[0];
        bufferMatrix[2] = (float) Math.sin(rotY);
        bufferMatrix[8] = -bufferMatrix[2];

        Matrix.multiplyMM(resultMatrix, 0, bufferMatrix, 0, transformationMatrix, 0);
        //System.out.println(resultMatrix);\
        /*System.out.println("MAT");
        for (int i = 0; i < 16; ++i) {
            System.out.print(resultMatrix[i] + " ");
            if (i%4 == 3) {
                System.out.println();
            }
        }*/
        return resultMatrix;
    }


}
