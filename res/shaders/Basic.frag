#version 330
in vec2 texCoords;

uniform int u_FilterMode;
uniform int u_FilterSize;
uniform sampler2D textureBase;
//uniform sampler2D textureNormal;

out vec4 outColor;

float arrayRedChannel[365];
float arrayGreenChannel[365];
float arrayBlueChannel[365];

void bubbleSort(int n)
{
    for (int n = (u_FilterSize*u_FilterSize); n > 0; n--){
        if (n == 1)//pokud má pole jen 1 element, není co třídit
        {
            return;
        }

        //projetí všech neporovnaných prvků pole
        for (int i = 0; i < n-1; i++)
        {
            if (arrayRedChannel[i] > arrayRedChannel[i+1])//má prvek správné pořadí? Pokud ne, prohoď aktuální a následující prvek
            {
                float tmpValue = arrayRedChannel[i];
                arrayRedChannel[i] = arrayRedChannel[i+1];
                arrayRedChannel[i+1] = tmpValue;
            }
        }

        for (int i = 0; i < n-1; i++)
        {
            if (arrayGreenChannel[i] > arrayGreenChannel[i+1])
            {
                float tmpValue = arrayGreenChannel[i];
                arrayGreenChannel[i] = arrayGreenChannel[i+1];
                arrayGreenChannel[i+1] = tmpValue;
            }
        }

        for (int i = 0; i < n-1; i++)
        {
            if (arrayBlueChannel[i] > arrayBlueChannel[i+1])
            {
                float tmpValue = arrayBlueChannel[i];
                arrayBlueChannel[i] = arrayBlueChannel[i+1];
                arrayBlueChannel[i+1] = tmpValue;
            }
        }
    }
}

vec4 getColorFromMedian(int pixelIndex){
    //median
    int numberOfMissingPixels = (u_FilterSize*u_FilterSize) - pixelIndex;
    for (int i = pixelIndex; i < numberOfMissingPixels; i++){
        arrayRedChannel[pixelIndex] = 0;
        arrayGreenChannel[pixelIndex] = 0;
        arrayBlueChannel[pixelIndex] = 0;
    }

    int testValue = u_FilterSize * u_FilterSize;

    bubbleSort(testValue);
    highp int filterValue = int(round((u_FilterSize*u_FilterSize)/2.f));
    vec4 colorFromMedian = vec4(arrayRedChannel[filterValue],arrayGreenChannel[filterValue],arrayBlueChannel[filterValue],1.f);
    return colorFromMedian;
}

vec4 getColorFromMean(int pixelIndex){
    float red = 0;
    float green = 0;
    float blue = 0;

    for (int i = 0; i < pixelIndex; i++)
    {
        red += arrayRedChannel[i];
        green += arrayGreenChannel[i];
        blue += arrayBlueChannel[i];
    }

    vec4 colorFromMean = vec4(red/pixelIndex,green/pixelIndex,blue/pixelIndex,1.f);
    return colorFromMean;
}

vec4 obarviPixel(){
    int imageWidth = 716; //TODO
    int imageHeight = 630;
    float filterIndex = (u_FilterSize-1)/2;

    float stepX = (1.0f/716.0f);
    float stepY = (1.0f/630.0f);

    float startX = texCoords.x - filterIndex*(1.f/imageWidth);
    float endX = texCoords.x + filterIndex*(1.f/imageWidth);
    float startY = texCoords.y - filterIndex*(1.f/imageHeight);
    float endY = texCoords.y + filterIndex*(1.f/imageHeight);

    //převod souřadnic z <0;1> na <0;imageWidth> a <0;imageHeight>
    highp int istartX = int(round(startX*imageWidth));
    highp int iendX = int(round(endX*imageWidth));
    highp int istartY = int(round(startY*imageHeight));
    highp int iendY = int(round(endY*imageHeight));

    //upravení koncových bodů
    if (istartX < 0){
        istartX = 0;
    }
    if (iendX > imageWidth){
        iendX = imageWidth;
    }
    if(istartY < 0){
        istartY = 0;
    }
    if(iendY > imageHeight){
        iendY = imageHeight;
    }

    int pixelIndex = 0;
    for (int j = istartY; j <= iendY; j++){
        for (int i = istartX; i <= iendX; i++){
            float pixelPosX = i/ float(imageWidth);
            float pixelPosY = j/ float(imageHeight);
            arrayRedChannel[pixelIndex] = texture(textureBase, vec2(pixelPosX,pixelPosY)).r;
            arrayGreenChannel[pixelIndex] = texture(textureBase, vec2(pixelPosX,pixelPosY)).g;
            arrayBlueChannel[pixelIndex] = texture(textureBase, vec2(pixelPosX,pixelPosY)).b;
            pixelIndex++;
        }
    }


    vec4 calculatedColor = vec4(0.f, 1.f, 0.f,1.f);

    //median
    if (u_FilterMode == 1){
        calculatedColor = getColorFromMedian(pixelIndex);
    } else {
        calculatedColor = getColorFromMean(pixelIndex);
    }

    return calculatedColor;
}

void main() {
    vec4 vyslednaBarva;
    if (u_FilterSize == 0){
        vyslednaBarva = texture(textureBase, texCoords).rgba;
    } else {
            vyslednaBarva = obarviPixel();
    }

    outColor = vyslednaBarva;
}


//float array[3];
//    vec4 baseColor = texture(textureBase, texCoords).rgba;
//https://www.khronos.org/opengl/wiki/Data_Type_(GLSL) array/lists