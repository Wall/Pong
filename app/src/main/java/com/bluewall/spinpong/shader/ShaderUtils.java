package com.bluewall.spinpong.shader;

import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by david on 12/13/14.
 */
public class ShaderUtils {

    public static int loadShader(int type, String shaderCode){

        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static String loadVertexShaderSource() {
        //return loadShaderSource("/home/david/AndroidStudioProjects/spinpong/app/src/main/java/com/bluewall/spinpong/shader/VertexShader.c", "Vertex shader wasn't loaded properly");
        return "attribute vec4 vPosition;\n" +
                "void main() {\n" +
                "    gl_Position = vPosition;\n" +
                "}";
    }
    public static String loadFragmentShaderSource() {
        //return loadShaderSource("src/main/java/com/bluewall/spinpong/shader/FragmentShader.c", "Fragment shader wasn't loaded properly");
        return "precision mediump float;\n" +
                "uniform vec4 vColor;\n" +
                "void main() {\n" +
                "    gl_FragColor = vColor;\n" +
                "};";
    }

    private static String loadShaderSource(String path, String message) {
        BufferedReader reader = null;
        StringBuilder vertexShaderSource = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                vertexShaderSource.append(line).append('\n');
            }
        } catch (IOException e) {
            System.err.println(message);
            System.exit(1);
        }
        return vertexShaderSource.toString();
    }
}
