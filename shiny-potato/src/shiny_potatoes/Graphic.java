package shiny_potatoes;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL46.*;

public class Graphic extends Thread {
	private Logic resource;
	double h, w;
	double border = 0.1, width = 1;
	Texture[] potatoTextures = new Texture[3];
	Texture backgroundTexture;
	Texture menuButton1, menuButton2;
	
	void loadTextures() {
		try {
			potatoTextures[0] = new Texture("./res/potato1.png");
			potatoTextures[1] = new Texture("./res/potato2.png");
			potatoTextures[2] = new Texture("./res/potato3.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void initializeEverything() {
		// making this window current on this thread
		GLFW.glfwMakeContextCurrent(resource.window);
		// creating capabilities for the current thread
		GL.createCapabilities();
		//background is now grey
		glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		//loading textures
		loadTextures();
	}

	void resizeWindow() {	//reenumeration of cooridinates
		glViewport(0, 0, resource.width, resource.height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, w, h, 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
	}
	
	void drawMenu() {
		glBegin(GL_POLYGON);
		glColor3d(1, 1, 1);
		for(int i = 0; i < 4; i++)
			glVertex2i(resource.menuButton1CoordsX[i], resource.menuButton1CoordsY[i]);
		glEnd();
		
		glBegin(GL_POLYGON);
		glColor3d(1, 1, 1);
		for(int i = 0; i < 4; i++)
			glVertex2i(resource.menuButton2CoordsX[i], resource.menuButton2CoordsY[i]);
		glEnd();
	}
	void drawFlyingPotato() {
		potatoTextures[resource.currentFlying.get()].bind();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		
		glBegin(GL_POLYGON);
		glColor3d(1, 1, 1);
		glTexCoord2d(0d, 0d);
		glVertex2d(resource.flyingPotatoX.get()+border, resource.flyingPotatoY.get()+border);
		glTexCoord2d(1d, 0d);
		glVertex2d(resource.flyingPotatoX.get()+width, resource.flyingPotatoY.get()+border);
		glTexCoord2d(1d, 1d);
		glVertex2d(resource.flyingPotatoX.get()+width, resource.flyingPotatoY.get()+width);
		glTexCoord2d(0d, 1d);
		glVertex2d(resource.flyingPotatoX.get()+border, resource.flyingPotatoY.get()+width);
		glEnd();
		
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}
	
	void drawGame() {
		for(double y = 0; y < h; y++) {
			for(double x = 1; x < w-1; x++) {
				double mod = y % 2 == 1 ? 0.25 : -0.25;
				if(!resource.board.get((int)y).get((int)x).isPresent)
					continue;
				potatoTextures[resource.board.get((int)y).get((int)x).look].bind();
				
				glEnable(GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				
				glEnable(GL_TEXTURE_2D);
				glBegin(GL_POLYGON);
				glColor3d(1, 0, 1);
				glTexCoord2d(0d, 0d);
					glVertex2d(x+border+mod, y+border);
				glTexCoord2d(1d, 0d);
					glVertex2d(x+width+mod, y+border);
				glTexCoord2d(1d, 1d);
					glVertex2d(x+width+mod, y+width);
				glTexCoord2d(0d, 1d);
					glVertex2d(x+border+mod, y+width);
				glEnd();
				glDisable(GL_TEXTURE_2D);
				
				glDisable(GL_BLEND);
			}
		}
		drawFlyingPotato();
	}
	
	void drawPauseButton() {
		glBegin(GL_POLYGON);
		glColor3d(1, 1, 1);
		for(int i = 0; i < 4; i++)
			glVertex2i(resource.pauseButtonCoordsX[i], resource.pauseButtonCoordsY[i]);
		glEnd();
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
			glClear(GL_COLOR_BUFFER_BIT);
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
		//setDaemon(true);
	}
}
