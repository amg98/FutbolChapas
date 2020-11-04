precision mediump float;

uniform sampler2D tex0;

varying vec3 toLightVector;
varying vec3 toCameraVector;
varying vec2 passTexcoord;
varying vec3 passNormal;

uniform vec3 ambient;
uniform vec3 diffuse;
uniform vec3 specular;
uniform vec3 emissive;
uniform float alpha;
uniform float shininess;

uniform vec3 lightColor;

void main() {
    vec4 texColor = texture2D(tex0, passTexcoord);

    vec3 unitNormal = normalize(passNormal);
    vec3 unitLightVector = normalize(toLightVector);
    float brightness = max(dot(unitNormal, unitLightVector), 0.0);

    vec3 unitCameraVector = normalize(toCameraVector);
    vec3 reflectedLightDirection = reflect(-unitLightVector, unitNormal);
    float specularCoef = pow(max(dot(reflectedLightDirection, unitCameraVector), 0.0), shininess);

    vec3 ambientFactor = 0.1 * ambient;
    vec3 diffuseFactor = (1.0 - texColor.a) * diffuse + texColor.a * texColor.rgb;
    vec3 specularFactor = specular * specularCoef;
    diffuseFactor = diffuseFactor * brightness * lightColor;

    gl_FragColor = vec4(ambientFactor + diffuseFactor + specularFactor, alpha);
}
