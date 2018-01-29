#version 330

layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;
layout(location = 5) in vec2 texCoord;

uniform mat4 modelToCameraMatrix;
uniform mat3 normalModelToCameraMatrix;

out vec2 colorCoord;

layout (std140) uniform GlobalMatrices {
    mat4 cameraToClipMatrix;
};

void main() {
    vec4 tempCamPosition = (modelToCameraMatrix * vec4(position, 1.0));
    gl_Position = cameraToClipMatrix * tempCamPosition;
    colorCoord = texCoord;
}