varying vec3 lightDir;
varying vec3 N;
varying vec4 tPosition;
uniform sampler2D u_Texture;
const vec3 lightPos = vec3(0.5f, 0.0f, 0.0);

float diffuseSimple(vec3 L, vec3 N){
   return clamp(dot(L,N),0.0,1.0);
}

void main() {
    float dist = length(lightDir); //dot(lightDir,normalize(N)) (1.0f)/(1.0f + 0.5f*dist)
//  float intensity = 1.0f/(1.0f + 0.5f*dist);
    float intensity = diffuseSimple(lightDir, N)/(1.0f + 0.3f*dist);
//  vec3 L = normalize(lightPos.xyz - tPosition.xyz);
//  vec4 Idiff = vec4(1.0, 1.0, 1.0, 1.0)*max(dot(N,L), 0.0);
//  Idiff = clamp(Idiff, 0.0, 1.0);
//  gl_FragColor = Idiff;
//  float intensity = dot(lightDir,N);
//  intensity = floor(intensity*5.0f)/5.0f;
    vec4 col = vec4(intensity + 0.8f ,intensity + 0.22f , intensity + 0.47f, 1);
    gl_FragColor = col;
//  gl_FragColor = col* texture2D(u_Texture, v_TexCoordinate);
//  gl_FragColor = vec4(1, 1, 1, 1);
}