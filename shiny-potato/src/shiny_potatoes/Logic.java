package shiny_potatoes;

import java.util.Vector;

import org.lwjgl.glfw.GLFW;

public class Logic {
	Perspective currentPerspective = Perspective.menu;
	long window;
	int height = 480, width = 270;
	int rows = 13, columns = 7;
	Vector<Vector<Potato>> board; // this will store potatoes
	
	void shootPotato(double xpos, double ypos) {
		//position of the cursor in potatoes
		int xpot = (int)((xpos*columns + width-1)/width)-1;
		int ypot = (int)((ypos*rows + height-1)/height)-1;
		//position of the shooter
		int xsho = columns/2;
		int ysho = rows-1;
		//don't allow shooting at the level of the shooter
		//it causes division by zero
		if (ypot == ysho)
			return;
		//result position of new potato
		int xnew = -1, ynew = -1;
		//find first free position - size of potatoes ignored
		for (int y = rows-2; y >= 0; y--) {
			int x = (((xsho-xpot)*(y-ypot))/(ysho-ypot))+xpot;
			//fix when x is out of bounds
			if (x >= columns) {
				if ((x/(columns-1))%2 == 0) {
					x = x%(columns-1);
				}
				else {
					x = columns-1 - (x%(columns-1));
				}
			} //modulo for negative numbers is tricky
			else if (x < 0) {
				int div = 0;
				while (x < 0) {
					div++;
					x += columns-1;
				}
				if (div%2 == 0) {
					x = columns-1 - x;
				}
			} //the fix is still problematic for too small shooting angles
			if (board.elementAt(y).elementAt(x).isPresent)
				break;
			xnew = x;
			ynew = y;
		}
		if (xnew != -1 && ynew != -1)
			board.elementAt(ynew).elementAt(xnew).isPresent = true;
		//else - game over
	}

	Logic() {
		//start with three rows and rest of the board empty
		board = new Vector<Vector<Potato>>(rows); // initialization 1
		for (int i = 0; i < 3; i++) {
			board.add(new Vector<Potato>(columns)); // initialization 2
			for (int j = 0; j < columns; j++)
				board.elementAt(i).add(new Potato(true)); // initialization 3
		}
		for (int i = 3; i < rows; i++) {
			board.add(new Vector<Potato>(columns)); // initialization 2 cont'd
			for (int j = 0; j < columns; j++)
				board.elementAt(i).add(new Potato(false)); // initialization 3 cont'd
		}
		//this is the shooter
		board.elementAt(rows-1).elementAt(columns/2).isPresent = true;
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
