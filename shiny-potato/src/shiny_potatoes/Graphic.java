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
		GL46.glColor3i(1, 1, 1);
		GL46.glVertex2i(1, 1);
		GL46.glVertex2i(resource.columns-1, 1);
		GL46.glVertex2i(resource.columns-1, 5);
		GL46.glVertex2i(1, 5);
		GL46.glEnd();
		GLFW.glfwSwapBuffers(resource.window);
	}
	void drawGame() {
		for(double x = 0; x < w; x++) {
			for(double y = 0; y < h; y++) {
				if(!resource.board.get((int)y).get((int)x).isPresent)
					continue;
				GL46.glEnable(GL46.GL_TEXTURE_2D);
				GL46.glBegin(GL46.GL_POLYGON);
				GL46.glColor3i(1, 1, 1);
				GL46.glVertex2d(x+0.1, y+0.1);
				GL46.glVertex2d(x+0.8, y+0.1);
				GL46.glVertex2d(x+0.8, y+0.8);
				GL46.glVertex2d(x+0.1, y+0.8);
				
				GL46.glEnd();
			}
		}
		GLFW.glfwSwapBuffers(resource.window);
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
