package shiny_potatoes;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

public class App {
	
	private static long window;
	static double height=1, width=1;
	static boolean changed = false;

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
			changed = true;
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
			
			//sprawdza, czy klikniecie bylo wewnatrz wielokata - na razie z wyjsciem do System.out
			if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS && !buttonPressed){
				buttonPressed = true;
				double xpos[] = new double[1], ypos[] = new double[1];
				int currWidth[] = new int[1], currHeight[] = new int[1];
				GLFW.glfwGetWindowSize(window, currWidth, currHeight);
				GLFW.glfwGetCursorPos(window, xpos, ypos);
				if (xpos[0] > 3*currWidth[0]/4) {
					System.out.println("to the left");
				}
				else if (xpos[0] < currWidth[0]/4) {
					System.out.println("to the right");
				}
				else if (ypos[0] > 3*currHeight[0]/4) {
					System.out.println("higher");
				}
				else if (ypos[0] < currHeight[0]/4) {
					System.out.println("lower");
				}
				else {
					System.out.println("perfect!");
				}
			}
			if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS) {
				buttonPressed = false;
			}
			
			double[] xpos = new double[1],
					ypos = new double[1];
			GLFW.glfwGetCursorPos(window, xpos, ypos);
			double x = -width/2 + xpos[0],
					y = height/2 - ypos[0];
			if(!changed) {
				x/=600;
				y/=600;
				x-=width/2;
				y+=height/2;
			}
			GL11.glBegin(GL11.GL_POLYGON);
			double diffW = width/20;
			double diffH = height/20;
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glVertex2d(2*x-diffW,2*y);
			GL11.glVertex2d(2*x,2*y+diffH);
			GL11.glVertex2d(2*x+diffW,2*y);
			GL11.glVertex2d(2*x,2*y-diffH);
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
