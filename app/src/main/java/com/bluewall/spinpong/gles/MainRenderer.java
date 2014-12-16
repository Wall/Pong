package com.bluewall.spinpong.gles;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.bluewall.spinpong.model.Vec2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by david on 12/13/14.
 */
public class MainRenderer implements GLSurfaceView.Renderer {

    public static int DIMENSIONS = 3;
    private int _rectangleProgram;
    private int _rectangleAPositionLocation;
    private int transformationMatrixLocation;
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private float[] baseVertices;
    private float[] vertices = new float[] {};
    private List<Shape> shapes = new ArrayList<>();

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        //initShapes();
        int _rectangleVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, _rectangleVertexShaderCode);
        int _rectangleFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, _rectangleFragmentShaderCode);
        _rectangleProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(_rectangleProgram, _rectangleVertexShader);
        GLES20.glAttachShader(_rectangleProgram, _rectangleFragmentShader);
        GLES20.glLinkProgram(_rectangleProgram);
        _rectangleAPositionLocation = GLES20.glGetAttribLocation(_rectangleProgram, "aPosition");
        transformationMatrixLocation = GLES20.glGetUniformLocation(_rectangleProgram, "transformationMatrix");
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(_rectangleProgram);
        GLES20.glVertexAttribPointer(_rectangleAPositionLocation, DIMENSIONS, GLES20.GL_FLOAT, false, 4*DIMENSIONS, vertexBuffer);
        GLES20.glEnableVertexAttribArray(_rectangleAPositionLocation);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        //System.arraycopy(baseVertices, 0, vertices, 0, baseVertices.length);
        //for (int i = 0; i < shapes.size(); ++i) {
            //applyTransform(shapes.get(i));
        //}
        vertexBuffer.position(0);
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        float[] mat = new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        //GLES20.glEnableVertexAttribArray(transformationMatrixLocation);


        for (int i = 0; i < shapes.size(); ++i) {
            Shape shape = shapes.get(i);
            GLES20.glUniformMatrix4fv(transformationMatrixLocation, 1, false, shape.genTransformationMatrix() ,0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, shape.getIndices().length,
                    GLES20.GL_UNSIGNED_SHORT, shape.getIndexBuffer());//sizeof(float)
        }

    }



    public void add(Shape shape) {
        shapes.add(shape);
        shape.setVertexOffset(vertices.length/DIMENSIONS);

        short[] sIndices = shape.getIndices();
        for (int i = 0; i < sIndices.length; ++i) {
            sIndices[i] += vertices.length/DIMENSIONS;
        }
        shape.genIndexBuffer();

        float[] sVertices = shape.getVertices();
        float[] newVertices = new float[vertices.length + sVertices.length];
        System.arraycopy(vertices, 0, newVertices, 0, vertices.length);
        System.arraycopy(sVertices, 0, newVertices, vertices.length, sVertices.length);
        vertices = newVertices;
    }

    private void initShapes()  {
        float rectangleVFA[] = {
                0,      0,
                0,      0.5f,
                0.375f,  0.5f,
                0.75f,  0.5f,
                0.75f,  0,
                0,      0
        };
        ByteBuffer rectangleVBB = ByteBuffer.allocateDirect(rectangleVFA.length * 4);
        rectangleVBB.order(ByteOrder.nativeOrder());
        vertexBuffer = rectangleVBB.asFloatBuffer();
        vertexBuffer.put(rectangleVFA);
        vertexBuffer.position(0);

        short[] indices = {
                0,1,2,3,4,5
        };
        ByteBuffer sByteBuffer = ByteBuffer.allocateDirect(indices.length*2);
        sByteBuffer.order(ByteOrder.nativeOrder());
        indexBuffer = sByteBuffer.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

    public void init() {
        ByteBuffer rectangleVBB = ByteBuffer.allocateDirect(vertices.length * 4);
        rectangleVBB.order(ByteOrder.nativeOrder());
        vertexBuffer = rectangleVBB.asFloatBuffer();

        baseVertices = new float[vertices.length];
        System.arraycopy(vertices, 0, baseVertices, 0, vertices.length);
//        vertexBuffer.put(vertices);
//        vertexBuffer.position(0);
    }

    private final String _rectangleVertexShaderCode =
                    "attribute vec4 aPosition;                                     \n" +
                    "uniform mat4 transformationMatrix;                            \n" +
                    "void main() {                                                 \n" +
                    "   gl_Position = transformationMatrix*aPosition;            \n" +
                    "}                                                             \n";

    private final String _rectangleFragmentShaderCode =
                    "void main() {                              \n"
                    +   " gl_FragColor = vec4(1,1,1,1);         \n"
                    +   "}                                      \n";

    private int loadShader(int type, String source)  {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private void applyTransform(Shape shape) {
        final float RESOLUTION_Y = 1440;
        final float RESOLUTION_X = 2560;
        float x = shape.getX();
        float y = shape.getY();
        //System.out.println("FLAGHx: " + x + ", " + y);
        int endIndex = shape.getVertexOffset()*DIMENSIONS + shape.getVertices().length;
        for (int i = shape.getVertexOffset()*DIMENSIONS; i < endIndex; ++i) {
            switch (i%DIMENSIONS) {
                case 0:
                    vertices[i] += x;
                    vertices[i] = vertices[i] * 2 /RESOLUTION_X;
                    break;
                case 1:
                    vertices[i] += y;
                    vertices[i] = -vertices[i] * 2 /RESOLUTION_Y;
                    break;
            }
        }
    }

    public void translate(Shape shape, float x, float y) {
        int endIndex = shape.getVertexOffset()*DIMENSIONS + shape.getVertices().length;
        for (int i = shape.getVertexOffset()*DIMENSIONS; i < endIndex; ++i) {
            vertices[i] += i%2 == 0 ? x : y;
        }
    }
}
