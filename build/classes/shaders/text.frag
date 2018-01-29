#version 330

in vec2 colorCoord;

uniform sampler2D sampler;

void main() {
    vec4 tex = texture2D(sampler, colorCoord);
    gl_FragColor = vec4(1.0f, 1.0f, 0.0f, tex.a);
}