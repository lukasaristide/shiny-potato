package shiny_potatoes;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class Graphic extends Thread {
	private Logic resource;

	void initializeEverything() {
		// making this window current on this Thread
		GLFW.glfwMakeContextCurrent(resource.window);
		// creating capabilities for the current thread
		GL.createCapabilities();
	}

	@Override
	public void run() {
		initializeEverything();
		
	}

	Graphic(Logic Resource) {
		this.resource = Resource;
	}
}
