#version 330

in vec3 vertexNormal;
in vec3 cameraSpacePosition;
in vec4 diffuseColor;

uniform vec3 cameraSpaceLightPos;
uniform vec4 lightIntensity;
uniform vec4 ambientIntensity;
uniform vec4 specularColor;

uniform float lightAttenuation;
uniform float shininessFactor;

float CalcAttenuation(in vec3 cameraSpacePosition, out vec3 lightDirection)
{
    vec3 lightDifference =  cameraSpaceLightPos - cameraSpacePosition;
    float lightDistanceSqr = dot(lightDifference, lightDifference);
    lightDirection = lightDifference * inversesqrt(lightDistanceSqr);
    return (1 / ( 1.0 + lightAttenuation * sqrt(lightDistanceSqr)));
}

void main() {
    vec3 lightDir = vec3(0.0);
    float atten = CalcAttenuation(cameraSpacePosition, lightDir);
    vec4 attenIntensity = atten * lightIntensity;

    vec3 surfaceNormal = normalize(vertexNormal);
    float cosAngIncidence = dot(surfaceNormal, lightDir);
    cosAngIncidence = clamp(cosAngIncidence, 0, 1);

    vec3 viewDirection = normalize(-cameraSpacePosition);
    vec3 reflectDir = reflect(-lightDir, surfaceNormal);
    float phongTerm = dot(viewDirection, reflectDir);
    phongTerm = clamp(phongTerm, 0, 1);
    phongTerm = cosAngIncidence != 0.0 ? phongTerm : 0.0;
    phongTerm = pow(phongTerm, shininessFactor);

    gl_FragColor = (diffuseColor * attenIntensity * cosAngIncidence) + (specularColor * attenIntensity * phongTerm) + (diffuseColor * ambientIntensity);
}