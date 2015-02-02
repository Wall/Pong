attribute vec4 aPosition;
attribute vec3 normal;
attribute vec2 a_TexCoordinate;
varying vec3 lightDir;
varying vec3 N;
varying vec4 tPosition;
varying vec2 v_TexCoordinate;
uniform mat4 transformationMatrix;
uniform mat4 uMVPMatrix;
const vec3 lightPos = vec3(0.5f, 0.0f, 0.0);
const mat4 perspectiveMatrix =
mat4(
    1.1f, 0, 0, 0,
    0, 1.2f, 0, 0,
    0, 0, -1, -1,
    0, 0, -1f, 1
);

void main() {
    N = normalize(mat3(transformationMatrix)*normal);
    tPosition = transformationMatrix*aPosition;
    lightDir = normalize(lightPos - tPosition);
    v_TexCoordinate = a_TexCoordinate;
    //normal = normalize(gl_NormalMatrix * gl_Normal);
    //gl_Position = perspectiveMatrix*tPosition;
    gl_Position = uMVPMatrix*tPosition;
}