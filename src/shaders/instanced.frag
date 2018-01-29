#version 330

uniform sampler2D sampler;

in vec2 colorCoord;

out vec4 outputColor;

void main() {
    outputColor = texture2D(sampler, colorCoord);
}