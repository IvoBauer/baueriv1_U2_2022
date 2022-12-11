package p03swing;


import lwjglutils.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{
    private int shaderProgram;
    private Grid grid;

    private Camera camera;
    private Mat4 projection;
    private OGLTexture2D textureBase, textureOriginal;
    private OGLTexture2D textureNormal;
    private boolean mouseButton1;
    private double ox, oy;
    private int loc_uFilterSize;

    // PostProcessing
    private OGLRenderTarget renderTarget;

    // Viewer
    private OGLTexture2D.Viewer viewer;

    // Obj
    private OGLModelOBJ objModel;
    static int filterSize = 3;
    static String textureName = "mosaic.jpg";


    @Override
    public void init() {
//        OGLUtils.printOGLparameters();
//        OGLUtils.printLWJLparameters();
//        OGLUtils.printJAVAparameters();
//        OGLUtils.shaderCheck();
//        // Set the clear color
//        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        textRenderer = new OGLTextRenderer(width, height);
//        glEnable(GL_DEPTH_TEST);

        shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        grid = new Grid(8, 8);

        try {
            textureBase = new OGLTexture2D("./textures/mosaic.jpg");
            textureOriginal = new OGLTexture2D("./textures/mosaic.jpg");
            textureNormal = new OGLTexture2D("./textures/bricksn.png");
            System.out.println("W: " + textureBase.getWidth() + " H: " + textureBase.getHeight());
            renderTarget = new OGLRenderTarget(textureBase.getWidth(), textureBase.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loc_uFilterSize = glGetUniformLocation(shaderProgram, "u_FilterSize"); //Zvolený objekt k vykreslení
        textureOriginal.flipY(new OGLTexImageFloat.Format(4));
        textureBase.flipY(new OGLTexImageFloat.Format(4));
        viewer = new OGLTexture2D.Viewer();
    }

    @Override
    public void display(){
//        glViewport(0, 0, width, height);
//        String text = new String(this.getClass().getName() + ": look at console and try keys, mouse, wheel and window interaction " );
//
//        pass++;
//        // Set the clear color
////        glClearColor((float)(Math.sin(pass/100.)/2+0.5),
////                (float)(Math.cos(pass/200.)/2+0.5),
////                (float)(Math.sin(pass/300.)/2+0.5), 0.0f);
//        // clear the framebuffer
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//        //create and draw text
//        textRenderer.clear();
//        textRenderer.addStr2D(3, 20, text);
//        textRenderer.addStr2D(3, 50, "pass " + pass);
//        textRenderer.addStr2D(width-90, height-3, " (c) PGRF UHK");
//        textRenderer.draw();
        getTexture();

        // Vykresluj do textury
        renderTarget.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUseProgram(shaderProgram);
        textureBase.bind(shaderProgram, "textureBase", 0);

        // Zase vykresluj na obrazovku
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glUniform1i(loc_uFilterSize, filterSize);
        // Render quadu přes obrazovku
        grid.getBuffers().draw(GL_TRIANGLES, shaderProgram);
        viewer.view(textureOriginal, -1, -0.5, 1);
    }

    private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
		}
	};

    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
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
        @Override public void invoke (long window, double dx, double dy) {
        }
    };

    public static void sayMeow(int meow){
        System.out.println("RENDERER:" + meow);
        filterSize = meow;
    };

    private void getTexture(){
        try {
            textureBase = new OGLTexture2D(textureName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void selectImage(){
        System.out.println("Select image:");
        JFileChooser fileChooser= new JFileChooser();
//                        File workingDirectory = new File(System.getProperty("user.dir"));
        File workingDir = new File
                (System.getProperty("user.dir") +
                        System.getProperty("file.separator")+ "res" +
                        System.getProperty("file.separator")+ "textures" );

        fileChooser.setCurrentDirectory(workingDir);

        int choice = fileChooser.showOpenDialog(null);
        if (choice != JFileChooser.APPROVE_OPTION) return;

        File chosenFile = fileChooser.getSelectedFile();
        if(chosenFile.exists()){
//            loadTexture("./textures/"+chosenFile.getName());
            System.out.println(chosenFile);
            String FileName = "./textures/" + chosenFile.getName();
            textureName = FileName;
        }
    }
/*
	@Override
	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}

	@Override
	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}

	@Override
	public GLFWMouseButtonCallback getMouseCallback() {
		return mbCallback;
	}

	@Override
	public GLFWCursorPosCallback getCursorCallback() {
		return cpCallbacknew;
	}

	@Override
	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}


	@Override
	public void init() {
	}

	@Override
	public void display() {
	}*/
}