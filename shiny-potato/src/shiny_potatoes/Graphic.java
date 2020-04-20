package shiny_potatoes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

public class Graphic extends Thread {
	private Logic resource;
	double h, w;
	double border = 0.1, width = 1;

	void initializeEverything() {
		// making this window current on this thread
		GLFW.glfwMakeContextCurrent(resource.window);
		// creating capabilities for the current thread
		GL.createCapabilities();
		//background is now grey
		GL46.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
	}

	void resizeWindow() {	//reenumeration of cooridinates
		GL46.glViewport(0, 0, resource.width, resource.height);
		GL46.glMatrixMode(GL46.GL_PROJECTION);
		GL46.glLoadIdentity();
		GL46.glOrtho(0, w, h, 0, -1, 1);
		GL46.glMatrixMode(GL46.GL_MODELVIEW);
	}
	
	void drawMenu() {
		GL46.glBegin(GL46.GL_POLYGON);
		GL46.glColor3d(1, 1, 1);
		for(int i = 0; i < 4; i++)
			GL46.glVertex2i(resource.menuButton1CoordsX[i], resource.menuButton1CoordsY[i]);
		GL46.glEnd();
		
		GL46.glBegin(GL46.GL_POLYGON);
		GL46.glColor3d(1, 1, 1);
		for(int i = 0; i < 4; i++)
			GL46.glVertex2i(resource.menuButton2CoordsX[i], resource.menuButton2CoordsY[i]);
		GL46.glEnd();
	}
	void drawFlyingPotato() {
		GL46.glEnable(GL46.GL_TEXTURE_2D);
		GL46.glBegin(GL46.GL_POLYGON);
		GL46.glColor3d(1, 1, 1);
		GL46.glVertex2d(resource.flyingPotatoX.get()+border, resource.flyingPotatoY.get()+border);
		GL46.glVertex2d(resource.flyingPotatoX.get()+width, resource.flyingPotatoY.get()+border);
		GL46.glVertex2d(resource.flyingPotatoX.get()+width, resource.flyingPotatoY.get()+width);
		GL46.glVertex2d(resource.flyingPotatoX.get()+border, resource.flyingPotatoY.get()+width);
		GL46.glEnd();
		GL46.glDisable(GL46.GL_TEXTURE_2D);
	}
	
	void drawGame() {
		for(double y = 0; y < h; y++) {
			for(double x = 1; x < w-1; x++) {
				double mod = y % 2 == 1 ? 0.25 : -0.25;
				if(!resource.board.get((int)y).get((int)x).isPresent)
					continue;
				GL46.glEnable(GL46.GL_TEXTURE_2D);
				GL46.glBegin(GL46.GL_POLYGON);
				GL46.glColor3d(1, 1, 1);
				GL46.glVertex2d(x+border+mod, y+border);
				GL46.glVertex2d(x+width+mod, y+border);
				GL46.glVertex2d(x+width+mod, y+width);
				GL46.glVertex2d(x+border+mod, y+width);
				GL46.glEnd();
				GL46.glDisable(GL46.GL_TEXTURE_2D);
			}
		}
		drawFlyingPotato();
	}
	
	void drawPauseButton() {
		GL46.glBegin(GL46.GL_POLYGON);
		GL46.glColor3d(1, 1, 1);
		for(int i = 0; i < 4; i++)
			GL46.glVertex2i(resource.pauseButtonCoordsX[i], resource.pauseButtonCoordsY[i]);
		GL46.glEnd();
	}
	
	void drawPause() {
		drawGame();
		drawPauseButton();
		GLFW.glfwSwapBuffers(resource.window);
	}
	
	void drawRanking() {
		
	}
	
	@Override
	public void run() {
		initializeEverything();
		resizeWindow();
		while (!GLFW.glfwWindowShouldClose(resource.window)) {
			GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
			switch(resource.currentPerspective) {
			case menu:
				drawMenu();
				break;
			case game:
				drawGame();
				break;
			case pause:
				drawPause();
				break;
			case ranking:
				drawRanking();
				break;
			default:
				break;	
			}
			GLFW.glfwSwapBuffers(resource.window);
			try {
				sleep(10);
			} catch (InterruptedException e) { e.printStackTrace(); return;}
		}
	}

	Graphic(Logic resource) {
		this.resource = resource;
		h = resource.rows;
		w = resource.columns;
		setDaemon(true);
	}
}

class Texture{
	int id;
	int width, height;
	void bind() {
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, id);
	}
	Texture(String filename) throws IOException{
		BufferedImage x = ImageIO.read(new File(filename));
		id = GL46.glGenTextures();
		bind();
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
		width = x.getWidth();
		height = x.getHeight();
		GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGBA, width, height, 0, GL46.GL_RGBA_INTEGER, GL46.GL_INT,
				x.getRGB(0, 0, width, height, null, 0, width));
	}
}
