package shiny_potatoes;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.glfw.GLFW;

public class Logic {
	Perspective currentPerspective = Perspective.menu;
	long window;
	int height = 480, width = 270;
	int rows = 13, columns = 7;
	AtomicReference<Double> flyingPotatoX = new AtomicReference<Double>(3d), flyingPotatoY = new AtomicReference<Double>(12d);
	Vector<Vector<Potato>> board; // this will store potatoes
	
	void shootPotato(double xpos, double ypos) throws InterruptedException {
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
			}
			if (board.elementAt(y).elementAt(x).isPresent) {
				//this is game over
				if (y == rows-2)
					break;
				//additional fix for too small shooting angles
				int diff = 0;
				if ((x-xnew) > 1) {
					diff = 1;
				}
				else if ((xnew-x) > 1) {
					diff = -1;
				}
				else {
					break; //this means the angle was good
				}
				while (xnew+diff != x) {
					if (board.elementAt(y).elementAt(xnew).isPresent)
						break;
					else if (ynew != -1 && board.elementAt(ynew).elementAt(xnew+diff).isPresent)
						break;
					xnew += diff;
					if (xnew < 0) {
						xnew = 0;
						break;
					}
					else if (xnew >= columns) {
						xnew = columns-1;
						break;
					}
				}
				if (!board.elementAt(y).elementAt(xnew).isPresent)
					ynew = y;
				break;
			}
			xnew = x;
			ynew = y;
			board.elementAt(ynew).elementAt(xnew).isPresent = true;
			Thread.sleep(100);
			board.elementAt(ynew).elementAt(xnew).isPresent = false;
		}
		if (xnew != -1 && ynew != -1)
			board.elementAt(ynew).elementAt(xnew).isPresent = true;
		else
			gameOver();
	}
	
	void gameOver() {
		currentPerspective = Perspective.menu;
		setBoard();
	}
	
	void setBoard() {
		//start with three rows and rest of the board empty
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < columns; j++)
				board.elementAt(i).elementAt(j).isPresent = true;
		}
		for (int i = 3; i < rows; i++) {
			for (int j = 0; j < columns; j++)
				board.elementAt(i).elementAt(j).isPresent = false;
		}
		//this is the shooter
		board.elementAt(rows-1).elementAt(columns/2).isPresent = true;
	}

	Logic() {
		board = new Vector<Vector<Potato>>(rows); // initialization 1
		for (int i = 0; i < rows; i++) {
			board.add(new Vector<Potato>(columns)); // initialization 2
			for (int j = 0; j < columns; j++)
				board.elementAt(i).add(new Potato()); // initialization 3
		}
		setBoard();
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
