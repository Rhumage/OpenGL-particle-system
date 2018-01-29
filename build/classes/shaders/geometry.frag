#version 330

in FragData {
    smooth vec2 mapping;
};

uniform sampler2D sampler;

out vec4 outputColor;

void main() {
    outputColor = texture2D(sampler, mapping);
}
