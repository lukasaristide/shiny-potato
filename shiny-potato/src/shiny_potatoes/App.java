package shiny_potatoes;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

public class App {
	
	private static long window;
	static double height=1, width=1;

	public static void main(String[] args) throws InterruptedException {
		//TODO
		GLFW.glfwInit();
		
		//GLFW.glfwSwapInterval(100);
		
		window = GLFW.glfwCreateWindow(600, 600, "Tytul", 0, 0);
		
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
			GL11.glViewport(0, 0, width, height);
			System.out.println(width);
			System.out.println(height);
			System.out.println();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(-width, width, -height, height, -1, 1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			App.height = height;
			App.width = width;
		});
		
		GL11.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		while(!GLFW.glfwWindowShouldClose(window)) {
			GL11.glClear(GL33.GL_COLOR_BUFFER_BIT);
			

			GL11.glBegin(GL11.GL_POLYGON);
			double minimum = Math.min(width,height)/2;
			
			GL11.glColor3f(1.0f, 0.0f, 0.0f);
			GL11.glVertex2d(-minimum, minimum);
			GL11.glColor3f(0.0f, 1.0f, 0.0f);
			GL11.glVertex2d(minimum, minimum);
			GL11.glColor3f(0.0f, 0.0f, 1.0f);
			GL11.glVertex2d(minimum, -minimum);
			GL11.glColor3f(0.5f, 0.5f, 0.5f);
			GL11.glVertex2d(-minimum, -minimum);
			
			GL11.glEnd();
			
			GLFW.glfwSwapBuffers(window);
			
			GLFW.glfwPollEvents();
			Thread.sleep(15);
		}
		
		GLFW.glfwDestroyWindow(window);
		
		GLFW.glfwTerminate();
	}

}
