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
		
		//aby nie lapac kursora wielokrotnie pod rzad
		boolean buttonPressed = false;
		
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
			
			//mialo byc zgrane z polozeniem wielokata,
			//ale z jakichs powodow width i height w tym miejscu wynosza 1
			if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS && !buttonPressed){
				buttonPressed = true;
				int size = 1;
				double xpos[] = new double[size], ypos[] = new double[size];
				GLFW.glfwGetCursorPos(window, xpos, ypos);
				if (xpos[0] > 450) {
					System.out.println("to the left");
				}
				else if (xpos[0] < 150) {
					System.out.println("to the right");
				}
				else if (ypos[0] > 450) {
					System.out.println("higher");
				}
				else if (ypos[0] < 150) {
					System.out.println("lower");
				}
				else {
					System.out.println("perfect!");
				}
			}
			if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS) {
				buttonPressed = false;
			}
			
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
