package shiny_potatoes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.glfw.GLFW;

public class Logic {
	Perspective currentPerspective = Perspective.menu;
	
	long window;
	int height = 480*2, width = 270*2;
	int rows = 13, columns = 9;
	String fileName = "./data.txt";
	
	AtomicInteger
			currentFlying = new AtomicInteger(0), 
			currentScore = new AtomicInteger(0),
			shots = new AtomicInteger(0), 
			parity = new AtomicInteger(0),
			speed = new AtomicInteger(2);
	
	AtomicReference<Double> 
			flyingPotatoX = new AtomicReference<Double>(4d), 
			flyingPotatoY = new AtomicReference<Double>(12d);
	
	Vector<Vector<Potato>> board; // this will store potatoes
	
	AtomicIntegerArray	highScores = new AtomicIntegerArray(6);
	
	int[] 	menuButton1CoordsX = new int[4], menuButton1CoordsY = new int[4],		//Button1 - start game
			menuButton2CoordsX = new int[4], menuButton2CoordsY = new int[4],		//Button2 - ranking
			menuSpeedDigitCoordsX = new int[4], menuSpeedDigitCoordsY = new int[4],		//Button3 - speed setting
			pauseButtonCoordsX = new int[4], pauseButtonCoordsY = new int[4];		//Pause + Game Over
	
	double[]
			menuDecreaseArrowX = new double[3], menuDecreaseArrowY = new double[3],		//Arrow for decreasing speed
			menuIncreaseArrowX = new double[3], menuIncreaseArrowY = new double[3];		//Arrow for increasing speed
	
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
		long sleepTime = 25;
		double inc = ((double)(sleepTime*(speed.get()+2)))/200;
		double distance = inc;
		//its coefficient
		double coeff = (ysho-ypot)/(xsho-xpot);
		if (coeff > 0 && coeff < 0.2) {
			coeff = 0.2;
		}
		else if (coeff < 0 && coeff > -0.2) {
			coeff = -0.2;
		}
		
		//find first free position - size of potatoes included
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
				if (xnew == -1 || ynew == -1)
					break;
				//don't stop unconnected
				if (((int)y+parity.get())%2 == 1) {
					if (ynew > (int)y && xnew < (int)x && xnew+1 <= columns-2 &&
							!board.elementAt((int)y).elementAt(xnew).isPresent &&
							!board.elementAt(ynew).elementAt(xnew+1).isPresent) {
						xnew += 1;
					}
				}
				else {
					if (ynew > (int)y && xnew > (int)x && xnew-1 >= 1 &&
							!board.elementAt((int)y).elementAt(xnew).isPresent &&
							!board.elementAt(ynew).elementAt(xnew-1).isPresent) {
						xnew -= 1;
					}
				}
				break;
			}
			
			//don't allow shooting through potatoes
			if (board.elementAt((int)y+(1-(((int)y + parity.get())%2))).elementAt((int)(x+0.75)).isPresent || 
					board.elementAt((int)y+(((int)y + parity.get())%2)).elementAt((int)(x+1.25)).isPresent){
					xnew = (int)x;
					ynew = (int)y;
				if (y <= ((int)y) + 0.5 && !board.elementAt((int)y+1).elementAt((int)x).isPresent) {
					ynew = (int)y+1;
				}
				if (x >= ((int)x) + 0.5 && !board.elementAt((int)y).elementAt((int)x+1).isPresent) {
					xnew = (int)x+1;
				}
				break;
			}
			else if(board.elementAt((int)y+(1-(((int)y + parity.get())%2))).elementAt((int)(x-0.25)).isPresent || 
					board.elementAt((int)y+(((int)y + parity.get())%2)).elementAt((int)(x+0.25)).isPresent) {
				xnew = (int)x;
				ynew = (int)y;
				if (y <= ((int)y) + 0.5 && !board.elementAt((int)y+1).elementAt((int)x).isPresent) {
					ynew = (int)y+1;
				}
				if (x <= ((int)x) + 0.5 && !board.elementAt((int)y).elementAt((int)x-1).isPresent) {
					xnew = (int)x-1;
				}
					break;
			}
			xnew = (int)x;
			ynew = (int)y;
			flyingPotatoX.set(x);
			flyingPotatoY.set(y);
			Thread.sleep(sleepTime);
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
	
	int dfs(int x, int y, int color, int mode, boolean[][] visited) {
		if(mode == 2) {
			board.get(y).get(x).isPresent = false;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {}
		}
		else if(mode == 1) {
			board.get(y).get(x).look += 3;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {}
		}
		int howManyofThisColor = 1;
		if(visited[y][x] || x >= columns-1 || x < 1)
			return 0;
		visited[y][x] = true;
		if(y > 0) {
			if(board.get(y-1).get(x).isPresent && board.get(y-1).get(x).look == color)
				howManyofThisColor += dfs(x,y-1,color,mode, visited);
			int mod = (y + parity.get()) % 2 == 0 ? -1 : 1;
			if(x+mod > 0 && x+mod < columns-1 && board.get(y-1).get(x+mod).isPresent && board.get(y-1).get(x+mod).look == color)
				howManyofThisColor += dfs(x+mod,y-1,color,mode, visited);
		}
		if(x > 1 && board.get(y).get(x-1).isPresent && board.get(y).get(x-1).look == color)
			howManyofThisColor += dfs(x-1, y, color, mode, visited);
		if(x < columns-1 && board.get(y).get(x+1).isPresent && board.get(y).get(x+1).look == color)
			howManyofThisColor += dfs(x+1, y, color, mode, visited);
		if(y < rows) {
			if(board.get(y+1).get(x).isPresent && board.get(y+1).get(x).look == color)
				howManyofThisColor += dfs(x,y+1,color,mode, visited);
			int mod = (y + parity.get()) % 2 == 0 ? -1 : 1;
			if(x+mod > 0 && x+mod < columns-1 && board.get(y+1).get(x+mod).isPresent && board.get(y+1).get(x+mod).look == color)
				howManyofThisColor += dfs(x+mod,y+1,color,mode, visited);
		}
		return howManyofThisColor;
	}
	
	void makePotatoesDisappear(int x, int y) {
		int color = board.get(y).get(x).look;
		boolean[][] visited = new boolean[rows][columns];
		for(boolean[] i : visited)
			for(int j = 0; j < columns; j++)
				i[j] = false;
		
		int found = dfs(x,y,color,0,visited);
		
		if(found > 2) {
			currentScore.addAndGet(found);
			for(boolean[] i : visited)
				for(int j = 0; j < columns; j++)
					i[j] = false;
			dfs(x,y,color,1,visited);
			for(boolean[] i : visited)
				for(int j = 0; j < columns; j++)
					i[j] = false;
			dfs(x,y,color+3,2,visited);
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
		currentPerspective = Perspective.gameover;
		int p = 0, q = 6;
		while (p < q) {
			int s = (p+q)/2;
			if (highScores.get(s) > currentScore.get()) {
				p = s+1;
			}
			else {
				q = s;
			}
		}
		if (p == 6) {
			return;
		}
		for (int i = 5; i > p; i--) {
			highScores.set(i, highScores.get(i-1));
		}
		highScores.set(p, currentScore.get());
		saveToFile();
	}
	
	void saveToFile() {
		File dataFile = new File(fileName + ".tmp");
		byte[] b = new byte[64];
		int len = 1;
		b[0] = (byte)(speed.get() + '0');
		for (int i = 0; i < 6; i++) {
			if (highScores.get(i) == 0) {
				break;
			}
			b[len++] = (byte)' ';
			int div = 1;
			while (div <= highScores.get(i)) {
				div *= 10;
			}
			div /= 10;
			while (div > 1) {
				b[len++] = (byte)(((highScores.get(i)%(div*10))/div) + '0');
				div /= 10;
			}
			b[len++] = (byte)((highScores.get(i)%10) + '0');
		}
		try {
			dataFile.createNewFile();
			FileOutputStream outputFile = new FileOutputStream(dataFile);
			outputFile.write(b, 0, len);
			outputFile.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		File origFile = new File(fileName);
		origFile.delete();
		dataFile.renameTo(origFile);
	}
	
	void setBoard() {
		currentScore.set(0);
		shots.set(0);
		parity.set(0);
		//start with three rows and rest of the board empty
		for (int i = 0; i < 3; i++) {
			board.elementAt(i).elementAt(0).isPresent = false;
			board.elementAt(i).elementAt(columns-1).isPresent = false;
			for (int j = 1; j < columns-1; j++) {
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
		try {
			File dataFile = new File(fileName);
			if (dataFile.exists()) {
				FileInputStream inputFile = new FileInputStream(dataFile);
				int i = 0, a = inputFile.read();
				if (a == -1) {
					speed.set(2);
				}
				else {
					speed.set(a - '0');
					inputFile.read();
					while (inputFile.available() > 0) {
						a = inputFile.read();
						if ((char)a == ' ') {
							i++;
						}
						else {
							highScores.set(i, highScores.get(i)*10 + a-'0');
						}
					}
				}
				inputFile.close();
			}
			else {
				dataFile.createNewFile();
				speed.set(2);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// those two calls have to be called from the main thread - so they are called
		// here, since main thread will construct Logic
		// lib initialization
		GLFW.glfwInit();
		// window creation
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
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
		
		menuSpeedDigitCoordsX[0] = menuSpeedDigitCoordsX[3] = columns - 2;
		menuSpeedDigitCoordsX[1] = menuSpeedDigitCoordsX[2] = columns - 1;
		menuSpeedDigitCoordsY[0] = menuSpeedDigitCoordsY[1] = rows - 2;
		menuSpeedDigitCoordsY[2] = menuSpeedDigitCoordsY[3] = rows - 1;
		
		menuIncreaseArrowX[0] = menuIncreaseArrowX[2] = columns - 1.1;
		menuIncreaseArrowX[1] = columns - 0.6;
		
		menuDecreaseArrowY[0] = menuIncreaseArrowY[0] = rows - 1.2;
		menuDecreaseArrowY[1] = menuIncreaseArrowY[1] = rows - 1.5;
		menuDecreaseArrowY[2] = menuIncreaseArrowY[2] = rows - 1.8;
		
		menuDecreaseArrowX[0] = menuDecreaseArrowX[2] = columns - 1.9;
		menuDecreaseArrowX[1] = columns - 2.4;
		
		
		pauseButtonCoordsX[0] = pauseButtonCoordsX[3] = 1;
		pauseButtonCoordsX[1] = pauseButtonCoordsX[2] = columns - 1;
		pauseButtonCoordsY[0] = pauseButtonCoordsY[1] = 3;
		pauseButtonCoordsY[2] = pauseButtonCoordsY[3] = 7;
	}
}

enum Perspective{
	game, menu, pause, ranking, gameover;
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
