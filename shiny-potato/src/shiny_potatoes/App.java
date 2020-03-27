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
		//inicjalizacja biblioteki
		GLFW.glfwInit();
		
		//stworzenie okna
		window = GLFW.glfwCreateWindow(600, 600, "Tytul", 0, 0);
		
		//podpięcie okna pod OpenGL
		GLFW.glfwMakeContextCurrent(window);
		//podpięcie OpenGL po aktualne okno
		GL.createCapabilities();
		
		//callback, gdy zmieniamy rozmiary okna
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
		
		//kolor tła
		GL11.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		while(!GLFW.glfwWindowShouldClose(window)) {
			GL11.glClear(GL33.GL_COLOR_BUFFER_BIT);
			
			//zacznij rysować wielokąt
			GL11.glBegin(GL11.GL_POLYGON);
			double minimum = Math.min(width,height)/2;
			//kolejne wierzchołki
			GL11.glColor3f(1.0f, 0.0f, 0.0f);
			GL11.glVertex2d(-minimum, minimum);
			GL11.glColor3f(0.0f, 1.0f, 0.0f);
			GL11.glVertex2d(minimum, minimum);
			GL11.glColor3f(0.0f, 0.0f, 1.0f);
			GL11.glVertex2d(minimum, -minimum);
			GL11.glColor3f(0.5f, 0.5f, 0.5f);
			GL11.glVertex2d(-minimum, -minimum);
			
			//zakończ rysować wielokąt
			GL11.glEnd();
			
			//wyrzuć narysowany w buforze wielokąt na ekran
			GLFW.glfwSwapBuffers(window);
			
			//przeprocesuj wszelkie eventy, jeśli się takowe zdarzyły
			GLFW.glfwPollEvents();
			//śpij 15 milisekund
			Thread.sleep(15);
		}
		//zmiszcz okno	
		GLFW.glfwDestroyWindow(window);
		
		//zakończ działanie biblioteki
		GLFW.glfwTerminate();
	}

}
