#version 330

layout(std140) uniform;
layout(points) in;
layout(triangle_strip, max_vertices = 4) out;

uniform mat4 orientationMatrix;
uniform mat4 modelToCameraMatrix;

uniform GlobalMatrices {
    mat4 cameraToClipMatrix;
};

in VertexData {
    vec3 cameraSpherePos;
    float sphereRadius;
} vert[];

out FragData {
    smooth vec2 mapping;
};

mat4 translate(in vec3 v) {
    mat4 m = mat4(1.0);
    m[3][0] = v.x;
    m[3][1] = v.y;
    m[3][2] = v.z;
    return m;
}

void main() {
    vec4 cameraCornerPos;
    //Bottom-left
    mapping = vec2(0, 1);
    cameraCornerPos = vec4(-vert[0].sphereRadius, -vert[0].sphereRadius, 1.0, 1.0);
    gl_Position = cameraToClipMatrix * modelToCameraMatrix * translate(vert[0].cameraSpherePos) * orientationMatrix * cameraCornerPos;
    gl_PrimitiveID = gl_PrimitiveIDIn;
    EmitVertex();

    //Top-left
    mapping = vec2(0, 0);
    cameraCornerPos = vec4(-vert[0].sphereRadius, vert[0].sphereRadius, 1.0, 1.0);
    gl_Position = cameraToClipMatrix * modelToCameraMatrix * translate(vert[0].cameraSpherePos) * orientationMatrix * cameraCornerPos;
    gl_PrimitiveID = gl_PrimitiveIDIn;
    EmitVertex();

    //Bottom-right
    mapping = vec2(1, 1);
    cameraCornerPos = vec4(vert[0].sphereRadius, -vert[0].sphereRadius, 1.0, 1.0);
    gl_Position = cameraToClipMatrix * modelToCameraMatrix * translate(vert[0].cameraSpherePos) * orientationMatrix * cameraCornerPos;
    gl_PrimitiveID = gl_PrimitiveIDIn;
    EmitVertex();

    //Top-right
    mapping = vec2(1, 0);
    cameraCornerPos = vec4(vert[0].sphereRadius, vert[0].sphereRadius, 1.0, 1.0);
    gl_Position = cameraToClipMatrix * modelToCameraMatrix * translate(vert[0].cameraSpherePos) *  orientationMatrix * cameraCornerPos;
    gl_PrimitiveID = gl_PrimitiveIDIn;
    EmitVertex();
}
