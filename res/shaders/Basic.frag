#version 330
in vec2 texCoords;

uniform int u_FilterMode;//0 = mean, 1 = median filter
uniform int u_FilterSize;//mean filter nabývá hodnot: 0,3,5,7,9,11,13,15 a median filter: 0,3,5,7
uniform int u_ImageWidth;
uniform int u_ImageHeight;
uniform sampler2D textureBase;

out vec4 outColor;

//Pokoušel jsem se nastavit přes uniform, ale dočetl jsem se, že to není možné. Takže jsem vypočetl pro nejvyšší filtr (15*15=225).
float arrayRedChannel[225];
float arrayGreenChannel[225];
float arrayBlueChannel[225];

//Seřadí jednotlivé barevné kanály (R,G,B) od nejmenší po největší hodnotu. Použití v Median filtru.
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

//Vrací barvu vypočtenou pomocí mediánu barevných kanálů.
vec4 getColorFromMedian(int pixelIndex){

    //V případě bodů umístěných na kraji je nutné doplnit pixely
    int numberOfMissingPixels = (u_FilterSize*u_FilterSize) - pixelIndex;
    for (int i = pixelIndex; i < numberOfMissingPixels; i++){
        arrayRedChannel[pixelIndex] = 0;
        arrayGreenChannel[pixelIndex] = 0;
        arrayBlueChannel[pixelIndex] = 0;
    }

    int count = u_FilterSize * u_FilterSize;

    //Barevné kanály se setřídí od nejmenšího po největší
    bubbleSort(count);

    //Najde se pořadí mediánu
    highp int filterValue = int(round((u_FilterSize*u_FilterSize)/2.f));

    vec4 colorFromMedian = vec4(arrayRedChannel[filterValue], arrayGreenChannel[filterValue], arrayBlueChannel[filterValue], 1.f);
    return colorFromMedian;
}

//Vrací barvu vypočtenou pomocí průměru barevných kanálů.
vec4 getColorFromMean(int pixelIndex){
    float red = 0;
    float green = 0;
    float blue = 0;

    //Sečte jednotlivé barevné kanály
    for (int i = 0; i < pixelIndex; i++)
    {
        red += arrayRedChannel[i];
        green += arrayGreenChannel[i];
        blue += arrayBlueChannel[i];
    }

    //Součet barevných kanálů se jednotlivě vydělí počtem a získá se tak průměrná hodnota každého barevného kanálu.
    vec4 colorFromMean = vec4(red/pixelIndex, green/pixelIndex, blue/pixelIndex, 1.f);
    return colorFromMean;
}

//Určí se začátek a konec filtru a do pole se uloží jednotlivé barevné kanály. Poté se zavolá přislušná funkce (mean,median) a vypočte se výsledná barva.
vec4 getPixelColor(){
    int imageWidth = u_ImageWidth;
    int imageHeight = u_ImageHeight;
    float filterIndex = (u_FilterSize-1)/2;

    float startX = texCoords.x - filterIndex*(1.f/imageWidth);
    float endX = texCoords.x + filterIndex*(1.f/imageWidth);
    float startY = texCoords.y - filterIndex*(1.f/imageHeight);
    float endY = texCoords.y + filterIndex*(1.f/imageHeight);

    //Převod souřadnic z <0;1> na <0;imageWidth> a <0;imageHeight>
    highp int istartX = int(round(startX*imageWidth));
    highp int iendX = int(round(endX*imageWidth));
    highp int istartY = int(round(startY*imageHeight));
    highp int iendY = int(round(endY*imageHeight));

    //Upravení koncových bodů
    if (istartX < 0){
        istartX = 0;
    }
    if (iendX > imageWidth){
        iendX = imageWidth;
    }
    if (istartY < 0){
        istartY = 0;
    }
    if (iendY > imageHeight){
        iendY = imageHeight;
    }

    //Načtení pixelů polí podle barevných kanálů
    int pixelIndex = 0;
    for (int j = istartY; j <= iendY; j++){
        for (int i = istartX; i <= iendX; i++){
            float pixelPosX = i/ float(imageWidth);
            float pixelPosY = j/ float(imageHeight);
            arrayRedChannel[pixelIndex] = texture(textureBase, vec2(pixelPosX, pixelPosY)).r; //RED
            arrayGreenChannel[pixelIndex] = texture(textureBase, vec2(pixelPosX, pixelPosY)).g; //GREEN
            arrayBlueChannel[pixelIndex] = texture(textureBase, vec2(pixelPosX, pixelPosY)).b; //BLUE
            pixelIndex++;
        }
    }

    //Dle nastaveného módu se zvolí patřičná funkce pro výpočet barvy
    vec4 calculatedColor;
    if (u_FilterMode == 1){
        calculatedColor = getColorFromMedian(pixelIndex);
    } else {
        calculatedColor = getColorFromMean(pixelIndex);
    }

    return calculatedColor;
}

void main() {
    if (u_FilterSize == 0){
        outColor = texture(textureBase, texCoords).rgba;
    } else {
        outColor = getPixelColor();
    }
}