#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

out vec2 texCoords;

void main() {
    vec2 newpos = vec2(inPosition.x,inPosition.y-0.5f);
    texCoords = inPosition;
//    vec4 objectPosition = vec4(inPosition, 0.f, 1.f);
    vec4 objectPosition = vec4(newpos, 0.5f, 1.f);
    gl_Position = objectPosition;
}

