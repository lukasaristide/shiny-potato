package shiny_potatoes;

import java.util.Vector;

import org.lwjgl.glfw.GLFW;

public class Logic {
	long window;
	double height = 1, width = 1;
	int rows = 7, columns = 13;
	Vector<Vector<Potato>> board; // this will store potatoes

	Logic() {
		board = new Vector<Vector<Potato>>(rows); // initialization 1
		for (int i = 0; i < rows; i++) {
			board.add(new Vector<Potato>(columns)); // initialization 2
			for (int j = 0; j < columns; j++)
				board.elementAt(j).add(new Potato()); // initialization 3
		}
		// those fwo calls have to be called from the main thread - so they are called
		// here, since main thread will construct Logic
		// lib initialisation
		GLFW.glfwInit();
		// window creation
		window = GLFW.glfwCreateWindow(480, 270, "Shiny Potatoes", 0, 0);
	}
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
