package ImageFilter;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.concurrent.CountDownLatch;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LwjglWindowThread extends Thread {

	public static final int WIDTH = 800;
    public static final int HEIGHT = 500;

    // The window handle
	private long window;
	private AbstractRenderer renderer;
	CountDownLatch quit;
    
	private boolean init = true;
    int index;

    private static final boolean DEBUG = true;

	public long getWindow() {
		return window;
	}

	static {
        if (DEBUG) {
            System.setProperty("org.lwjgl.util.Debug", "true");
            System.setProperty("org.lwjgl.util.NoChecks", "false");
            System.setProperty("org.lwjgl.util.DebugLoader", "true");
            System.setProperty("org.lwjgl.util.DebugAllocator", "true");
            System.setProperty("org.lwjgl.util.DebugStack", "true");
            Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
        }
    }

    public LwjglWindowThread(int index, CountDownLatch quit, AbstractRenderer renderer){
    	this.index = index;
        this.quit = quit;
        this.renderer = renderer;
		init();
	}
	
	private void init() {
		String text = new String(renderer.getClass().getName() );
		text = text.substring(0,text.lastIndexOf('.'));
		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Filtrace obrazu (Mean+Median) | Ivo Bauer CV2", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, renderer.getKeyCallback());
		glfwSetWindowSizeCallback(window,renderer.getWsCallback());
		glfwSetMouseButtonCallback(window,renderer.getMouseCallback());
		glfwSetCursorPosCallback(window,renderer.getCursorCallback());
		glfwSetScrollCallback(window,renderer.getScrollCallback());
		
		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2 + index * 30,
				(vidmode.height() - pHeight.get(0)) / 2 + index * 30
			);
		} // the stack frame is popped automatically

		// Make the window visible
		glfwShowWindow(window);
		
	}
	
	public void dispose() {
		renderer.dispose();
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
	}
	
	@Override
	public void run() {
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		// Enable v-sync
		glfwSwapInterval(1);

		if (init){ //only first time
			renderer.init();
			init = false;
		}
		
		while (quit.getCount() != 0) {
        	renderer.display();
			glfwSwapBuffers(window);
			glfwPollEvents();
        }
        GL.setCapabilities(null);
 	}
}