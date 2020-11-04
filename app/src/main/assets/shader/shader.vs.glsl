attribute vec4 vPosition;
attribute vec2 vTexcoord;
attribute vec3 vNormal;

uniform mat4 m;
uniform mat4 mvp;
uniform vec3 cameraPos;
uniform vec3 lightPos;

varying vec3 toLightVector;
varying vec3 toCameraVector;
varying vec2 passTexcoord;
varying vec3 passNormal;

void main() {

    passTexcoord = vTexcoord;
    passNormal = (m * vec4(vNormal, 0.0)).xyz;

    vec3 worldPos = (m * vPosition).xyz;
    toLightVector = lightPos - worldPos;
    toCameraVector = cameraPos - worldPos;

    gl_Position = mvp * vPosition;
}
