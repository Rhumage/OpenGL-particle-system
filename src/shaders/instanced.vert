#version 330

layout(location = 0) in vec3 position;
layout(location = 5) in vec2 texCoord;
layout(location = 7) in vec3 location;

uniform mat4 orientationMatrix;
uniform mat4 modelToCameraMatrix;

layout (std140) uniform GlobalMatrices {
    mat4 cameraToClipMatrix;
};

out vec2 colorCoord;

mat4 translate(in vec3 v) {
    mat4 m = mat4(1.0);
    m[3][0] = v.x;
    m[3][1] = v.y;
    m[3][2] = v.z;
    return m;
}

void main() {
    gl_Position = cameraToClipMatrix * modelToCameraMatrix * translate(location) * orientationMatrix * vec4(position, 1.0);
    colorCoord = texCoord;
}