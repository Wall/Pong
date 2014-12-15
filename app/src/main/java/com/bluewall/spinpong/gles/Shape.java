package com.bluewall.spinpong.gles;

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

    public Shape(float[] vertices, short[] indices) {
        this.vertices = vertices;
        this.indices = indices;
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

}
