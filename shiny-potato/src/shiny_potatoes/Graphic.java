package shiny_potatoes;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL46.*;

public class Graphic extends Thread {
	private Logic resource;
	double h, w;
	double border = 0.0, width = 1;
	Texture[] potatoTextures = new Texture[3];
	Texture backgroundTexture;
	Texture menuButton1, menuButton2;

	void loadTextures() {
		try {
			potatoTextures[0] = new Texture("./res/potato1.png");
			potatoTextures[1] = new Texture("./res/potato2.png");
			potatoTextures[2] = new Texture("./res/potato3.png");
			backgroundTexture = new Texture("./res/field.png");
			menuButton1 = new Texture("./res/menu1.png");
			menuButton2 = new Texture("./res/menu2.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void initializeEverything() {
		// making this window current on this thread
		GLFW.glfwMakeContextCurrent(resource.window);
		// creating capabilities for the current thread
		GL.createCapabilities();
		// background is now grey
		glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		// loading textures
		loadTextures();
	}

	void resizeWindow() { // reenumeration of cooridinates
		glViewport(0, 0, resource.width, resource.height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, w, h, 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
	}

	void setColorByPotatoNumber(int nr) {
		switch (nr) {
		case 0:
			glColor3d(1, 0, 0);
			break;
		case 1:
			glColor3d(0, 1, 0);
			break;
		case 2:
			glColor3d(0, 0, 1);
			break;
		default:
			glColor3d(1, 1, 1);
			break;
		}
	}
	
	void drawMenu() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		
		menuButton1.bind();
		glBegin(GL_POLYGON);
		glColor3d(0.5, 0.5, 0.8);
			glTexCoord2d(0d, 0d);
		glVertex2i(resource.menuButton1CoordsX[0], resource.menuButton1CoordsY[0]);
			glTexCoord2d(0d, 1d);
		glVertex2i(resource.menuButton1CoordsX[1], resource.menuButton1CoordsY[1]);
			glTexCoord2d(1d, 1d);
		glVertex2i(resource.menuButton1CoordsX[2], resource.menuButton1CoordsY[2]);
			glTexCoord2d(1d, 0d);
		glVertex2i(resource.menuButton1CoordsX[3], resource.menuButton1CoordsY[3]);
		glEnd();
		
		menuButton2.bind();
		glBegin(GL_POLYGON);
		glColor3d(0.5, 0.5, 0.8);
			glTexCoord2d(0, 0);
		glVertex2i(resource.menuButton2CoordsX[0], resource.menuButton2CoordsY[0]);
			glTexCoord2d(0, 1);
		glVertex2i(resource.menuButton2CoordsX[1], resource.menuButton2CoordsY[1]);
			glTexCoord2d(1, 1);
		glVertex2i(resource.menuButton2CoordsX[2], resource.menuButton2CoordsY[2]);
			glTexCoord2d(1, 0);
		glVertex2i(resource.menuButton2CoordsX[3], resource.menuButton2CoordsY[3]);
		glEnd();
		
		int curPotato = (int) (Math.random()%3);
		potatoTextures[curPotato].bind();
		glBegin(GL_POLYGON);
		setColorByPotatoNumber(curPotato);
			glTexCoord2d(0, 0);
		glVertex2i(resource.menuButton2CoordsX[3], resource.menuButton2CoordsY[3]+1);
			glTexCoord2d(1, 0);
		glVertex2i(resource.menuButton2CoordsX[2], resource.menuButton2CoordsY[2]+1);
			glTexCoord2d(1, 1);
		glVertex2d(w-1,h-0.5);
			glTexCoord2d(0, 1);
		glVertex2d(1,h-0.5);
		glEnd();
		
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		
	}

	void drawFlyingPotato() {
		potatoTextures[resource.currentFlying.get()].bind();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);

		glBegin(GL_POLYGON);

		setColorByPotatoNumber(resource.currentFlying.get());

		glTexCoord2d(0d, 0d);
		glVertex2d(resource.flyingPotatoX.get() + border, resource.flyingPotatoY.get() + border);
		glTexCoord2d(1d, 0d);
		glVertex2d(resource.flyingPotatoX.get() + width, resource.flyingPotatoY.get() + border);
		glTexCoord2d(1d, 1d);
		glVertex2d(resource.flyingPotatoX.get() + width, resource.flyingPotatoY.get() + width);
		glTexCoord2d(0d, 1d);
		glVertex2d(resource.flyingPotatoX.get() + border, resource.flyingPotatoY.get() + width);
		glEnd();

		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}

	void drawGame() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		for (double y = 0; y < h; y++) {
			for (double x = 1; x < w - 1; x++) {
				double mod = y % 2 == 1 ? 0.25 : -0.25;
				if (!resource.board.get((int) y).get((int) x).isPresent)
					continue;
				potatoTextures[resource.board.get((int) y).get((int) x).look].bind();
				
				glBegin(GL_POLYGON);

				switch (resource.board.get((int) y).get((int) x).look) {
				case 0:
					glColor3d(1, 0, 0);
					break;
				case 1:
					glColor3d(0, 1, 0);
					break;
				default:
					glColor3d(0, 0, 1);
					break;
				}

				glTexCoord2d(0d, 0d);
				glVertex2d(x + border + mod, y + border);
				glTexCoord2d(1d, 0d);
				glVertex2d(x + width + mod, y + border);
				glTexCoord2d(1d, 1d);
				glVertex2d(x + width + mod, y + width);
				glTexCoord2d(0d, 1d);
				glVertex2d(x + border + mod, y + width);
				glEnd();
				
			}
		}
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		drawFlyingPotato();
	}

	void drawPauseButton() {
		glBegin(GL_POLYGON);
		glColor3d(1, 1, 1);
		for (int i = 0; i < 4; i++)
			glVertex2i(resource.pauseButtonCoordsX[i], resource.pauseButtonCoordsY[i]);
		glEnd();
	}

	void drawPause() {
		drawGame();
		drawPauseButton();
	}

	void drawRanking() {

	}

	void drawBackground(double brightness, double downBrightnessMod) {
		backgroundTexture.bind();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		glBegin(GL_POLYGON);
		glColor3d(brightness, brightness, brightness);
		glTexCoord2d(0.1d, 0.1d);
		glVertex2d(0, 0);
		glTexCoord2d(0.9d, 0.1d);
		glVertex2d(w, 0);
		glColor3d(brightness-downBrightnessMod, brightness-downBrightnessMod, brightness-downBrightnessMod);
		glTexCoord2d(0.9d, 0.9d);
		glVertex2d(w, h);
		glTexCoord2d(0.1d, 0.9d);
		glVertex2d(0, h);
		glEnd();
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);

	}

	@Override
	public void run() {
		initializeEverything();
		resizeWindow();
		while (!GLFW.glfwWindowShouldClose(resource.window)) {
			glClear(GL_COLOR_BUFFER_BIT);
			switch (resource.currentPerspective) {
			case menu:
				drawBackground(1, 0.7);
				drawMenu();
				break;
			case game:
				drawBackground(0.5, 0);
				drawGame();
				break;
			case pause:
				drawBackground(0.5, 0);
				drawPause();
				break;
			case ranking:
				drawBackground(1, 0);
				drawRanking();
				break;
			default:
				break;
			}
			GLFW.glfwSwapBuffers(resource.window);
			try {
				sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	Graphic(Logic resource) {
		this.resource = resource;
		h = resource.rows;
		w = resource.columns;
		// setDaemon(true);
	}
}
