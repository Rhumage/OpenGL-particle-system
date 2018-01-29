#version 330

layout(location = 0) in vec3 position;
layout(location = 5) in vec2 texCoord;

uniform vec2 posOffset;

out vec2 colorCoord;

void main() {
    gl_Position = vec4(position.x + posOffset.x / 100, position.y + posOffset.y / 100, position.z, 1.0);
    colorCoord = texCoord;
}