package ImageFilter;


import lwjglutils.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;


/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {
    private int shaderProgram;
    private Grid grid;
    private OGLTexture2D textureBase, textureOriginal;
    private int loc_uFilterSize, loc_uFilterMode, loc_uImageHeight, loc_uImageWidth;
    private OGLRenderTarget renderTarget;
    private OGLTexture2D.Viewer viewer;
    static int filterSize = 0;
    static int filterMode = 0;
    static String textureName = "textures/NoiseGirl.jpg";
    static boolean textureNameChanged = true;


    @Override
    public void init() {
        shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        grid = new Grid(8, 8);

        try {
            textureBase = new OGLTexture2D("textures/NoiseGirl.jpg");
            textureOriginal = new OGLTexture2D("textures/NoiseGirl.jpg");
            System.out.println("W: " + textureBase.getWidth() + " H: " + textureBase.getHeight());
            renderTarget = new OGLRenderTarget(textureBase.getWidth(), textureBase.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loc_uFilterSize = glGetUniformLocation(shaderProgram, "u_FilterSize");
        loc_uFilterMode = glGetUniformLocation(shaderProgram, "u_FilterMode");
        loc_uImageWidth = glGetUniformLocation(shaderProgram, "u_ImageWidth");
        loc_uImageHeight = glGetUniformLocation(shaderProgram, "u_ImageHeight");
        textureOriginal.flipY(new OGLTexImageFloat.Format(4));
        textureBase.flipY(new OGLTexImageFloat.Format(4));
        viewer = new OGLTexture2D.Viewer();
    }

    @Override
    public void display() {
        //Pokud uživatel vybral a potvrdil obrázek, zde se načte
        if (textureNameChanged) {
            getTexture();
            textureOriginal.flipY(new OGLTexImageFloat.Format(4));
            textureBase.flipY(new OGLTexImageFloat.Format(4));
            textureNameChanged = false;

        }

        // Vykresluj do textury
        renderTarget.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUseProgram(shaderProgram);
        textureBase.bind(shaderProgram, "textureBase", 0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glUniform1i(loc_uFilterSize, filterSize);
        glUniform1i(loc_uFilterMode, filterMode);
        glUniform1i(loc_uImageWidth, textureBase.getWidth());
        glUniform1i(loc_uImageHeight, textureBase.getHeight());

        //Render quadu přes obrazovku
        grid.getBuffers().draw(GL_TRIANGLES, shaderProgram);

        //Zobrazení původního obrázku
        viewer.view(textureOriginal, -1, -1, 1);
    }

    //Změna velikosti filtru
    public static void changeFilterValue(int size) {
        filterSize = size;
    }

    //Načte nové obrázky do textury
    private void getTexture() {
        try {
            textureOriginal = new OGLTexture2D(textureName);
            textureBase = new OGLTexture2D(textureName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Dialog pro načtení obrázku
    public static void selectImage() {
        System.out.println("Select image:");
        JFileChooser fileChooser = new JFileChooser();
        File workingDir = new File
                (System.getProperty("user.dir") +
                        System.getProperty("file.separator") + "res" +
                        System.getProperty("file.separator") + "textures");

        fileChooser.setCurrentDirectory(workingDir);

        int choice = fileChooser.showOpenDialog(null);
        if (choice != JFileChooser.APPROVE_OPTION) return;

        File chosenFile = fileChooser.getSelectedFile();
        if (chosenFile.exists()) {
            String FileName = "./textures/" + chosenFile.getName();
            textureName = FileName;
            textureNameChanged = true;
            System.out.println(FileName);
        }
    }

    //Funkce pro změnu filtru (0 = mean, 1= median), filterValue je velikost filtru
    public static void changeMode(int mode, int filterValue) {
        filterMode = mode;
        filterSize = filterValue;
    }

    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
        }
    };

    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
        }

    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
        }
    };
}