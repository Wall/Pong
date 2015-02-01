package com.bluewall.spinpong.gles;

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

    protected float x = 0;
    protected float y = 0;

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

    public Shape(float[] vertices, short[] indices, float[] normals) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;

        for (int i = 0; i < vertices.length; ++i) {
            switch (i%MainRenderer.DIMENSIONS) {
                case 0: vertices[i] = vertices[i] * 2; break;
                case 1: vertices[i] = vertices[i] * 2; break;
                case 2: vertices[i] = vertices[i] * 2; break;
            }
        }
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
        System.arraycopy(IDENTITY_MATRIX, 0, resultMatrix, 0, 16);
        resultMatrix[12] -= x;
        resultMatrix[13] += y;

        return resultMatrix;
    }


}
