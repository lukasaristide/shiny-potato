package shiny_potatoes;

import java.util.Vector;

import org.lwjgl.glfw.GLFW;

public class Logic {
	Perspective currentPerspective = Perspective.menu;
	long window;
	int height = 480, width = 270;
	int rows = 13, columns = 7;
	Vector<Vector<Potato>> board; // this will store potatoes
	
	void shootPotato(double xpos, double ypos) {}

	Logic() {
		board = new Vector<Vector<Potato>>(rows); // initialization 1
		for (int i = 0; i < rows; i++) {
			board.add(new Vector<Potato>(columns)); // initialization 2
			for (int j = 0; j < columns; j++)
				board.elementAt(i).add(new Potato(true)); // initialization 3
		}
		// those two calls have to be called from the main thread - so they are called
		// here, since main thread will construct Logic
		// lib initialization
		GLFW.glfwInit();
		// window creation
		window = GLFW.glfwCreateWindow(width, height, "Shiny Potatoes", 0, 0);
	}
}

enum Perspective{
	game, menu;
}

class Potato {
	boolean isPresent;

	Potato() {
		isPresent = false;
	}

	Potato(boolean isPresent) {
		this.isPresent = isPresent;
	}
}
