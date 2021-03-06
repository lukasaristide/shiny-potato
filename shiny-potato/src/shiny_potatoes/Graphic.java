package shiny_potatoes;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL46.*;

public class Graphic extends Thread {
	private Logic resource;
	double h, w;
	double border = 0.0, width = 1;
	Texture[] potatoTextures = new Texture[6], shooter = new Texture[2];
	Texture backgroundTexture;
	Texture menu1Text, menu2Text, bgHorizontal, GameoverText, PauseText;
	Texture[] digit = new Texture[10];

	void loadTextures() {
		try {
			potatoTextures[0] = new Texture("./resources/potato1.png");
			potatoTextures[1] = new Texture("./resources/potato2.png");
			potatoTextures[2] = new Texture("./resources/potato3.png");
			potatoTextures[3] = new Texture("./resources/blurred_potato1.png");
			potatoTextures[4] = new Texture("./resources/blurred_potato2.png");
			potatoTextures[5] = new Texture("./resources/blurred_potato3.png");
			backgroundTexture = new Texture("./resources/field.png");
			menu1Text = new Texture("./resources/menu1_text.png");
			menu2Text = new Texture("./resources/menu2_text.png");
			bgHorizontal = new Texture("./resources/bg_horiz.png");
			GameoverText = new Texture("./resources/endgame_text.png");
			PauseText = new Texture("./resources/pause_text.png");
			shooter[0] = new Texture("./resources/shooter1.png");
			shooter[1] = new Texture("./resources/shooter2.png");
			for (int i = 0; i < 10; i++)
				digit[i] = new Texture("./resources/" + i + ".png");
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
			glColor3d(1, 1, 0);
			break;
		}
	}

	void drawMenuButton(double[] coordsX, double[] coordsY, Texture tex, double r, double g, double b) {
		tex.bind();
		glBegin(GL_POLYGON);
		glColor3d(r, g, b);
		glTexCoord2d(0d, 0d);
		glVertex2d(coordsX[0], coordsY[0]);
		glTexCoord2d(1d, 0d);
		glVertex2d(coordsX[1], coordsY[1]);
		glTexCoord2d(1d, 1d);
		glVertex2d(coordsX[2], coordsY[2]);
		glTexCoord2d(0d, 1d);
		glVertex2d(coordsX[3], coordsY[3]);
		glEnd();
	}

	void drawMenu() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);

		drawMenuButton(resource.menuButton1CoordsX, resource.menuButton1CoordsY, bgHorizontal, 0.5, 0.5, 0.8);
		drawMenuButton(resource.menuButton1CoordsX, resource.menuButton1CoordsY, menu1Text, 1, 1, 1);

		drawMenuButton(resource.menuButton2CoordsX, resource.menuButton2CoordsY, bgHorizontal, 0.5, 0.5, 0.8);
		drawMenuButton(resource.menuButton2CoordsX, resource.menuButton2CoordsY, menu2Text, 1, 1, 1);
		
		int curPotato = 0;
		potatoTextures[curPotato].bind();
		glBegin(GL_POLYGON);
		setColorByPotatoNumber(curPotato);
		glTexCoord2d(0, 0);
		glVertex2d(resource.menuButton2CoordsX[3], resource.menuButton2CoordsY[3] + 1);
		glTexCoord2d(1, 0);
		glVertex2d(resource.menuButton2CoordsX[2], resource.menuButton2CoordsY[2] + 1);
		glTexCoord2d(1, 1);
		glVertex2d(w - 1, h - 0.5);
		glTexCoord2d(0, 1);
		glVertex2d(1, h - 0.5);
		glEnd();

		int speed = resource.speed.get() % 10;
		drawMenuButton(resource.menuSpeedDigitCoordsX, resource.menuSpeedDigitCoordsY, digit[speed], 0 + ((double)(speed-1) / 2),
				1 - ((double)(speed-1) / 2), 0);

		glDisable(GL_TEXTURE_2D);

		glBegin(GL_POLYGON);
		for (int i = 0; i < 3; i++) {
			glColor3d(i % 3 == 1 ? 1 : 0, i % 3 != 1 ? 0.5 : 0, i % 3 != 1 ? 0.5 : 0);
			glVertex2d(resource.menuIncreaseArrowX[i], resource.menuIncreaseArrowY[i]);
		}
		glEnd();

		glBegin(GL_POLYGON);
		for (int i = 0; i < 3; i++) {
			glColor3d(i % 3 != 1 ? 0.3 : 0, i % 3 == 1 ? 1 : 0.3, i % 3 != 1 ? 0.5 : 0);
			glVertex2d(resource.menuDecreaseArrowX[i], resource.menuDecreaseArrowY[i]);
		}
		glEnd();

		glDisable(GL_BLEND);

	}

	void drawShooterAndFlyingPotato() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);

		double coordX = resource.flyingPotatoX.get(), coordY = resource.flyingPotatoY.get(), curWidth = width,
				curBorder = border;
		if (coordX == 4 && coordY == 12) {
			curWidth = 0.5;
			curBorder = 0.3;
		}

		potatoTextures[resource.currentFlying.get()].bind();
		glBegin(GL_POLYGON);

		setColorByPotatoNumber(resource.currentFlying.get());

		glTexCoord2d(0d, 0d);
		glVertex2d(coordX + curBorder, coordY);
		glTexCoord2d(1d, 0d);
		glVertex2d(coordX + curBorder + curWidth, coordY);
		glTexCoord2d(1d, 1d);
		glVertex2d(coordX + curBorder + curWidth, coordY + curWidth);
		glTexCoord2d(0d, 1d);
		glVertex2d(coordX + curBorder, coordY + curWidth);
		glEnd();

		shooter[coordX == 4 && coordY == 12 ? 1 : 0].bind();
		glBegin(GL_POLYGON);
		glColor3d(1, 1, 1);
		glTexCoord2d(0d, 0d);
		glVertex2d(4, 12);
		glTexCoord2d(1d, 0d);
		glVertex2d(4 + width, 12);
		glTexCoord2d(1d, 1d);
		glVertex2d(4 + width, 12 + width);
		glTexCoord2d(0d, 1d);
		glVertex2d(4, 12 + width);
		glEnd();
		
		
		int current = resource.currentScore.get();
		double percent = Math.min((double)current / (double)Math.max(1, resource.highScores.get(0)),1),
				blue = (1 - Math.max(1-percent, percent))*2;
		coordY = h-0.5;
		for (double i = w - 0.25; i > w - 3; i-= 0.5, current /= 10) {
			int toWrite = current % 10;
			digit[toWrite].bind();
			glBegin(GL_POLYGON);
			glColor3d(percent, 1 - percent, blue);
			glTexCoord2d(0d, 0d);
			glVertex2d(i - 0.25, coordY);
			glTexCoord2d(1d, 0d);
			glVertex2d(i + 0.25, coordY);
			glTexCoord2d(1d, 1d);
			glVertex2d(i + 0.25, coordY + 0.5);
			glTexCoord2d(0d, 1d);
			glVertex2d(i - 0.25, coordY + 0.5);
			glEnd();
		}
		
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}

	void drawGame() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		for (double y = 0; y < h; y++) {
			for (double x = 1; x < w - 1; x++) {
				double mod = (y + resource.parity.get()) % 2 == 1 ? 0.25 : -0.25;
				if (!resource.board.get((int) y).get((int) x).isPresent)
					continue;
				potatoTextures[resource.board.get((int) y).get((int) x).look].bind();

				glBegin(GL_POLYGON);

				setColorByPotatoNumber(resource.board.get((int) y).get((int) x).look);

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
		drawShooterAndFlyingPotato();
	}

	void drawPauseOrGameoverButton(boolean over) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		bgHorizontal.bind();
		glBegin(GL_POLYGON);
		glColor3d(1, 1, 1);
		glTexCoord2d(0d, 0d);
		glVertex2d(resource.pauseButtonCoordsX[0], resource.pauseButtonCoordsY[0]);
		glTexCoord2d(1d, 0d);
		glVertex2d(resource.pauseButtonCoordsX[1], resource.pauseButtonCoordsY[1]);
		glColor3d(0.5, 0.5, 0.5);
		glTexCoord2d(1d, 1d);
		glVertex2d(resource.pauseButtonCoordsX[2], resource.pauseButtonCoordsY[2]);
		glTexCoord2d(0d, 1d);
		glVertex2d(resource.pauseButtonCoordsX[3], resource.pauseButtonCoordsY[3]);
		glEnd();

		int current = resource.currentScore.get();
		double percent = Math.min((double)current / (double)Math.max(1, resource.highScores.get(0)),1),
				blue = (1 - Math.max(1-percent, percent))*2;
		double coordY = ((double) resource.pauseButtonCoordsY[0] + resource.pauseButtonCoordsY[2]) / 2 + 0.3;
		for (int i = (int)resource.pauseButtonCoordsX[1] - 1; i >= resource.pauseButtonCoordsX[0] + 1; i--, current /= 10) {
			int toWrite = current % 10;
			digit[toWrite].bind();
			glBegin(GL_POLYGON);
			glColor3d(percent, 1 - percent, blue);
			glTexCoord2d(0d, 0d);
			glVertex2d(i - 0.5, coordY);
			glTexCoord2d(1d, 0d);
			glVertex2d(i + 0.5, coordY);
			glTexCoord2d(1d, 1d);
			glVertex2d(i + 0.5, coordY + 1);
			glTexCoord2d(0d, 1d);
			glVertex2d(i - 0.5, coordY + 1);
			glEnd();
		}

		if(over)
			GameoverText.bind();
		else
			PauseText.bind();
		glBegin(GL_POLYGON);
		glColor3d(1, 1, 1);
		glTexCoord2d(0d, 0d);
		glVertex2d(resource.pauseButtonCoordsX[0]+1, resource.pauseButtonCoordsY[0]);
		glTexCoord2d(1d, 0d);
		glVertex2d(resource.pauseButtonCoordsX[1]-1, resource.pauseButtonCoordsY[0]);
		glTexCoord2d(1d, 1d);
		glVertex2d(resource.pauseButtonCoordsX[1]-1, resource.pauseButtonCoordsY[0]+2.3);
		glTexCoord2d(0d, 1d);
		glVertex2d(resource.pauseButtonCoordsX[0]+1, resource.pauseButtonCoordsY[0]+2.3);
		glEnd();

		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}

	void drawPause() {
		drawGame();
		drawPauseOrGameoverButton(false);
	}

	void drawRanking() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		for (int i = 0; i < 6; i++) {
			if (resource.highScores.get(i) == 0)
				break;
			double coordY = 1 + 2 * i;

			glBegin(GL_POLYGON);
			glColor3d(0.36, 0.63, 0.777);
			glVertex2d(1, coordY);
			glColor3d(0.63, 0.777, 0.36);
			glVertex2d(w - 1, coordY);
			glColor3d(0.777, 0.36, 0.63);
			glVertex2d(w - 1, 1 + coordY);
			glColor3d(0.777, 0.63, 0.36);
			glVertex2d(1, 1 + coordY);
			glEnd();
		}

		glEnable(GL_TEXTURE_2D);
		for (int i = 0; i < 6; i++) {
			int current = resource.highScores.get(i);
			if (current == 0)
				break;
			double coordY = 1 + 2 * i;

			for (int j = (int) w - 2; j >= 2; j--, current /= 10) {
				int toWrite = current % 10;
				digit[toWrite].bind();
				glBegin(GL_POLYGON);
				glColor3d(1, 1, 1);
				glTexCoord2d(0d, 0d);
				glVertex2d(j - 0.5, coordY);
				glTexCoord2d(1d, 0d);
				glVertex2d(j + 0.5, coordY);
				glTexCoord2d(1d, 1d);
				glVertex2d(j + 0.5, coordY + 1);
				glTexCoord2d(0d, 1d);
				glVertex2d(j - 0.5, coordY + 1);
				glEnd();
			}
		}
		glDisable(GL_TEXTURE_2D);

		glDisable(GL_BLEND);
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
		glColor3d(brightness - downBrightnessMod, brightness - downBrightnessMod, brightness - downBrightnessMod);
		glTexCoord2d(0.9d, 0.9d);
		glVertex2d(w, h);
		glTexCoord2d(0.1d, 0.9d);
		glVertex2d(0, h);
		glEnd();
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);

	}

	void drawGameover() {
		drawGame();
		drawPauseOrGameoverButton(true);
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
			case gameover:
				drawBackground(0.5, 0);
				drawGameover();
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
