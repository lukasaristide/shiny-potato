package shiny_potatoes;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.glfw.GLFW;

public class Logic {
	Perspective currentPerspective = Perspective.menu;
	long window;
	int height = 480*2, width = 270*2;
	int rows = 13, columns = 9;
	AtomicInteger currentFlying = new AtomicInteger(0), currentScore = new AtomicInteger(0);
	AtomicInteger shots = new AtomicInteger(0), parity = new AtomicInteger(0);
	AtomicReference<Double> flyingPotatoX = new AtomicReference<Double>(4d), flyingPotatoY = new AtomicReference<Double>(12d);
	Vector<Vector<Potato>> board; // this will store potatoes
	int[] menuButton1CoordsX = new int[4], menuButton1CoordsY = new int[4],		//Button1 - start game
			menuButton2CoordsX = new int[4], menuButton2CoordsY = new int[4],
			pauseButtonCoordsX = new int[4], pauseButtonCoordsY = new int[4];	//Button2 - ranking
	
	void shootPotato(double xpos, double ypos) throws InterruptedException {
		//position of the cursor in potatoes
		double xpot = ((xpos*columns + width-1)/width)-1;
		double ypot = ((ypos*rows + height-1)/height)-1;
		//position of the shooter
		double xsho = columns/2;
		double ysho = rows-1;
		//don't allow shooting below the level of the shooter
		if (ypot >= ysho)
			return;
		//result position of new potato
		int xnew = -1, ynew = -1;
		//potato flies on a straight line, its distance from the shooter is increased by inc
		double distance = 0.5, inc = 0.5;
		//its coefficient
		double coeff = (ysho-ypot)/(xsho-xpot);
		if (coeff > 0 && coeff < 0.2) {
			coeff = 0.2;
		}
		else if (coeff < 0 && coeff > -0.2) {
			coeff = -0.2;
		}
		
		//find first free position - size of potatoes ignored
		//probably not the best practice - loop stops, when new position is found, or when y is out of bounds
		while (true) {
			double x = (columns/2)-((coeff > 0 ? 1 : -1)*(distance/Math.sqrt(coeff*coeff+1)));
			double y = rows-1-Math.abs(coeff*(((distance/Math.sqrt(coeff*coeff+1)))));
			if (y < 0)
				break;
			//fix when x is out of bounds
			if (x >= columns) {
				if ((((int)x)/(columns-1))%2 == 0) {
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
				if (div%2 != 0) {
					x = columns-1 - x;
				}
			}
			//necessary change when lines are offset
			if (x < 1)
				x = 1;
			else if (x > columns-2)
				x = columns-2;
			
			if (board.elementAt((int)y).elementAt((int)x).isPresent) {
				//this is game over
				if (y < rows-3)
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
					//fix for offset lines
					if (((((int)y+parity.get())%2 == 0 && x < xnew) || (((int)y+parity.get())%2 == 1 && x > xnew)) &&
							xnew != -1 && !board.elementAt((int)y).elementAt(xnew).isPresent)
						ynew = (int)y;
					break; //this means the angle was good
				}
				while (xnew+diff != x) {
					if (xnew != -1 && board.elementAt((int)y).elementAt(xnew).isPresent)
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
				if (!board.elementAt((int)y).elementAt(xnew).isPresent)
					ynew = (int)y;
				break;
			}
			
			//don't allow shooting through a line of potatoes
			boolean under = false, above = false;
			if (xnew != -1 && ynew != -1) {
				for (int i = Math.min((int)x, xnew); i <= Math.max((int)x, xnew); i++) {
					if (!under && board.elementAt(ynew).elementAt(i).isPresent){
						under = true;
					}
					if (!above && board.elementAt((int)y).elementAt(i).isPresent){
						above = true;
					}
					if (under && above) {
						break;
					}
				}
			}
			if (under && above) {
				break;
			}
			xnew = (int)x;
			ynew = (int)y;
			flyingPotatoX.set(x);
			flyingPotatoY.set(y);
			Thread.sleep(25);
			distance += inc;
		}
		flyingPotatoX.set(4d);
		flyingPotatoY.set(12d);
		if (xnew != -1 && ynew != -1) {
			board.elementAt(ynew).elementAt(xnew).isPresent = true;
			board.elementAt(ynew).elementAt(xnew).look = currentFlying.get();
			currentFlying.set((int)(Math.round(Math.random()*10))%3);
			makePotatoesDisappear(xnew, ynew);
			if (shots.incrementAndGet() == 3) {
				addPotatoes();
			}
		}
		else
			gameOver();
	}
	
	int dfs(int x, int y, int color, boolean pop, boolean[][] visited) {
		int howManyofThisColor = 1;
		if(visited[y][x])
			return 0;
		visited[y][x] = true;
		if(y > 0) {
			if(board.get(y-1).get(x).isPresent && board.get(y-1).get(x).look == color)
				howManyofThisColor += dfs(x,y-1,color,pop, visited);
			int mod = (y + parity.get()) % 2 == 0 ? -1 : 1;
			if(x+mod > 0 && x+mod < columns-2 && board.get(y-1).get(x+mod).isPresent && board.get(y-1).get(x+mod).look == color)
				howManyofThisColor += dfs(x+mod,y-1,color,pop, visited);
		}
		if(x > 1 && board.get(y).get(x-1).isPresent && board.get(y).get(x-1).look == color)
			howManyofThisColor += dfs(x-1, y, color, pop, visited);
		if(x < columns-2 && board.get(y).get(x+1).isPresent && board.get(y).get(x+1).look == color)
			howManyofThisColor += dfs(x+1, y, color, pop, visited);
		if(y < rows) {
			if(board.get(y+1).get(x).isPresent && board.get(y+1).get(x).look == color)
				howManyofThisColor += dfs(x,y+1,color,pop, visited);
			int mod = (y + parity.get()) % 2 == 0 ? -1 : 1;
			if(x+mod > 0 && x+mod < columns-2 && board.get(y+1).get(x+mod).isPresent && board.get(y+1).get(x+mod).look == color)
				howManyofThisColor += dfs(x+mod,y+1,color,pop, visited);
		}
		if(pop)
			board.get(y).get(x).isPresent = false;
		return howManyofThisColor;
	}
	
	void makePotatoesDisappear(int x, int y) {
		int color = board.get(y).get(x).look;
		boolean[][] visited = new boolean[rows][columns];
		for(boolean[] i : visited)
			for(int j = 0; j < columns; j++)
				i[j] = false;
		
		int found = dfs(x,y,color,false, visited);
		
		currentScore.addAndGet(found);
		
		if(found > 2) {
			for(boolean[] i : visited)
				for(int j = 0; j < columns; j++)
					i[j] = false;
			dfs(x,y,color,true, visited);
		}
	}
	
	void addPotatoes() {
		shots.set(0);
		parity.set(parity.get() == 0 ? 1 : 0);
		for (int i = rows-2; i >= 0; i--) {
			for (int j = 0; j < columns; j++) {
				if (board.elementAt(i).elementAt(j).isPresent) {
					board.elementAt(i+1).elementAt(j).look = board.elementAt(i).elementAt(j).look;
					board.elementAt(i+1).elementAt(j).isPresent = true;
					board.elementAt(i).elementAt(j).isPresent = false;
				}
			}
		}
		for (int i = 0; i < columns; i++) {
			board.elementAt(0).elementAt(i).look = (int)(Math.round(Math.random()*10))%3;
			board.elementAt(0).elementAt(i).isPresent = true;
		}
		for (int i = 1; i < columns-1; i++) {
			if (board.elementAt(rows-1).elementAt(i).isPresent) {
				gameOver();
			}
		}
	}
	
	void gameOver() {
		currentPerspective = Perspective.menu;
		setBoard();
	}
	
	void setBoard() {
		currentScore.set(0);
		shots.set(0);
		parity.set(0);
		//start with three rows and rest of the board empty
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < columns; j++) {
				board.elementAt(i).elementAt(j).isPresent = true;
				//set random color - out of three for now
				board.elementAt(i).elementAt(j).look = (int)(Math.round(Math.random()*10))%3;
			}
		}
		for (int i = 3; i < rows; i++) {
			for (int j = 0; j < columns; j++)
				board.elementAt(i).elementAt(j).isPresent = false;
		}
		//this is the color of the shot potato
		currentFlying.set((int)(Math.round(Math.random()*10))%3);
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
		
		//menu coords
		menuButton1CoordsX[0] = menuButton1CoordsX[3] = 1;
		menuButton1CoordsX[1] = menuButton1CoordsX[2] = columns - 1;
		menuButton1CoordsY[0] = menuButton1CoordsY[1] = 1;
		menuButton1CoordsY[2] = menuButton1CoordsY[3] = 4;
		
		menuButton2CoordsX[0] = menuButton2CoordsX[3] = 1;
		menuButton2CoordsX[1] = menuButton2CoordsX[2] = columns - 1;
		menuButton2CoordsY[0] = menuButton2CoordsY[1] = 5;
		menuButton2CoordsY[2] = menuButton2CoordsY[3] = 7;
		
		pauseButtonCoordsX[0] = pauseButtonCoordsX[3] = 1;
		pauseButtonCoordsX[1] = pauseButtonCoordsX[2] = columns - 1;
		pauseButtonCoordsY[0] = pauseButtonCoordsY[1] = 3;
		pauseButtonCoordsY[2] = pauseButtonCoordsY[3] = 7;
	}
}

enum Perspective{
	game, menu, pause, ranking;
}

class Potato {
	boolean isPresent;
	int look;
	Potato() {
		isPresent = false;
	}

	Potato(boolean isPresent) {
		this.isPresent = isPresent;
	}
}
