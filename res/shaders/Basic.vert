#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

out vec2 texCoords;

void main() {
    texCoords = inPosition;

    //Úprava pozice obrázku
    vec2 newPos = vec2(inPosition.x,inPosition.y-1.f);
    vec4 objectPosition = vec4(newPos, 0.5f, 1.f);

    gl_Position = objectPosition;
}

