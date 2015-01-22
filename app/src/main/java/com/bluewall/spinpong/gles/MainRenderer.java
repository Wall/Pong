package com.bluewall.spinpong.gles;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

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
    private int normalLocation;
    private int transformationMatrixLocation;
    private int mMVPMatrixHandle;
    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private ShortBuffer indexBuffer;
    private float[] baseVertices;
    private float[] vertices = new float[] {};
    private float[] normals = new float[] {};
    private List<Shape> shapes = new ArrayList<>();
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];


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
        normalLocation = GLES20.glGetAttribLocation(_rectangleProgram, "normal");
        transformationMatrixLocation = GLES20.glGetUniformLocation(_rectangleProgram, "transformationMatrix");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(_rectangleProgram, "uMVPMatrix");
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2.85f, 7);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(_rectangleProgram);
        GLES20.glVertexAttribPointer(_rectangleAPositionLocation, DIMENSIONS, GLES20.GL_FLOAT, false, 4*DIMENSIONS, vertexBuffer);
        GLES20.glEnableVertexAttribArray(_rectangleAPositionLocation);
        GLES20.glVertexAttribPointer(normalLocation, DIMENSIONS, GLES20.GL_FLOAT, false, 4*DIMENSIONS, normalBuffer);
        GLES20.glEnableVertexAttribArray(normalLocation);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //GLES20.glVertexAttribPointer(normalLocation, DIMENSIONS, GLES20.GL_FLOAT, false, 4*DIMENSIONS, normalBuffer);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        //System.arraycopy(baseVertices, 0, vertices, 0, baseVertices.length);
        //for (int i = 0; i < shapes.size(); ++i) {
            //applyTransform(shapes.get(i));
        //}
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

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

        float[] sNormals = shape.getNormals();
        float[] newNormals = new float[normals.length + sNormals.length];
        System.arraycopy(normals, 0, newNormals, 0, normals.length);
        System.arraycopy(sNormals, 0, newNormals, normals.length, sNormals.length);
        normals = newNormals;
    }

    public void init() {
        ByteBuffer rectangleVBB = ByteBuffer.allocateDirect(vertices.length * 4);
        rectangleVBB.order(ByteOrder.nativeOrder());
        vertexBuffer = rectangleVBB.asFloatBuffer();

        vertexBuffer.position(0);
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer normalVBB = ByteBuffer.allocateDirect(vertices.length * 4);
        normalVBB.order(ByteOrder.nativeOrder());
        normalBuffer = normalVBB.asFloatBuffer();

        normalBuffer.position(0);
        normalBuffer.put(normals);
        normalBuffer.position(0);



        baseVertices = new float[vertices.length];
        System.arraycopy(vertices, 0, baseVertices, 0, vertices.length);
//        vertexBuffer.put(vertices);
//        vertexBuffer.position(0);
    }

    private final String _rectangleVertexShaderCode =
                    "attribute vec4 aPosition;                                    " +
                    "attribute vec3 normal;                                    " +
                    "varying vec3 lightDir;                                    " +
                    "varying vec3 N;                                            " +
                    "varying vec4 tPosition;" +
                    "uniform mat4 transformationMatrix;                           " +
                    "uniform mat4 uMVPMatrix;" +
                    "const vec3 lightPos = vec3(0.5f, 0.0f, 0.0);                                             " +
                    "const mat4 perspectiveMatrix = mat4(" +
                            "1.1f, 0, 0, 0," +
                            "0, 1.2f, 0, 0," +
                            "0, 0, -1, -1," +
                            "0, 0, -1f, 1" +
                            ");" +
                    "void main() {                                                " +
                    "   N = normalize(mat3(transformationMatrix)*normal);             " +//normalize(mat3(transformationMatrix)*normal)
                    "   tPosition = transformationMatrix*aPosition;" +
                    "   lightDir = normalize(lightPos - tPosition);                               " +
                    //"   normal = normalize(gl_NormalMatrix * gl_Normal);             " +
                    //"   gl_Position = perspectiveMatrix*tPosition;           " +
                    "   gl_Position = uMVPMatrix*tPosition;           " +
                    "}                                                            ";

    private final String _rectangleFragmentShaderCode =
                    "varying vec3 lightDir;                                    " +
                    "varying vec3 N;" +
                    "varying vec4 tPosition;" +
                    "const vec3 lightPos = vec3(0.5f, 0.0f, 0.0);" +
                    "float diffuseSimple(vec3 L, vec3 N){" +
                    "   return clamp(dot(L,N),0.0,1.0);" +
                    "}                                    " +
                    "void main() {                             " +
                    "    float dist = length(lightDir);             " + //dot(lightDir,normalize(N)) (1.0f)/(1.0f + 0.5f*dist)
                    //"    float intensity = 1.0f/(1.0f + 0.5f*dist);  " +
                    "    float intensity = diffuseSimple(lightDir, N)/(1.0f + 0.3f*dist);" +
                    //"    vec3 L = normalize(lightPos.xyz - tPosition.xyz);" +
                    //"    vec4 Idiff = vec4(1.0, 1.0, 1.0, 1.0)*max(dot(N,L), 0.0);" +
                            //"   Idiff = clamp(Idiff, 0.0, 1.0);" +
                            //"   gl_FragColor = Idiff;" +
                    //"    float intensity = dot(lightDir,N);    " +
                    //"    intensity = floor(intensity*5.0f)/5.0f; " +
                    "    vec4 col = vec4(intensity + 0.15f ,intensity + 0.2f , intensity + 0.45f, 1);             " +
                    "    gl_FragColor = col;        " +
                    //"    gl_FragColor = vec4(1, 1, 1, 1);        " +
                    "}                                     ";

    private int loadShader(int type, String source)  {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void translate(Shape shape, float x, float y) {
        int endIndex = shape.getVertexOffset()*DIMENSIONS + shape.getVertices().length;
        for (int i = shape.getVertexOffset()*DIMENSIONS; i < endIndex; ++i) {
            vertices[i] += i%2 == 0 ? x : y;
        }
    }
}
