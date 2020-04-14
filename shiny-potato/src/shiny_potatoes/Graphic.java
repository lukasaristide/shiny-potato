package shiny_potatoes;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Graphic extends Thread {
	private Logic resource;
	double h, w;

	void initializeEverything() {
		// making this window current on this thread
		GLFW.glfwMakeContextCurrent(resource.window);
		// creating capabilities for the current thread
		GL.createCapabilities();
	}

	void resizeWindow() {	//reenumeration of cooridinates
		GL11.glViewport(0, 0, resource.width, resource.height);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, w, 0, h, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	void drawMenu() {
		
	}
	void drawGame() {
		
	}
	@Override
	public void run() {
		initializeEverything();
		resizeWindow();
		while (!GLFW.glfwWindowShouldClose(resource.window)) {
			switch(resource.currentPerspective) {
			case menu:
				drawMenu();
				break;
			case game:
				drawGame();
				break;
			default:
				break;	
			}
			try {
				sleep(20);
			} catch (InterruptedException e) { e.printStackTrace(); return;}
		}
	}

	Graphic(Logic resource) {
		this.resource = resource;
		h = resource.rows;
		w = resource.columns;
	}
}
