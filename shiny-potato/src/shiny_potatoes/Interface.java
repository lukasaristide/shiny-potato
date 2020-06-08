package shiny_potatoes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

public class Interface {
	private Logic resource;
	private ReentrantLock lock;
	private ExecutorService executor;
	private boolean isPaused;
	private boolean speedChanged;

	boolean isInBoundsButton(double xpos, double ypos, double[] coordsX, double[] coordsY) {
		// position of the cursor in potatoes
		double xpot = ((xpos * resource.columns + resource.width - 1) / resource.width) - 1;
		double ypot = ((ypos * resource.rows + resource.height - 1) / resource.height) - 1;
		if (xpot < coordsX[0] || xpot >= coordsX[1])
			return false;
		if (ypot < coordsY[0] || ypot >= coordsY[2])
			return false;
		return true;
	}
	
	boolean isInBoundsArrow(double xpos, double ypos, double[] coordsX, double[] coordsY) {
		// position of the cursor in potatoes
		double xpot = ((xpos * resource.columns + resource.width - 1) / resource.width) - 1;
		double ypot = ((ypos * resource.rows + resource.height - 1) / resource.height) - 1;
		double a = (coordsY[0]-coordsY[1])/(coordsX[0]-coordsX[1]);
		double b = (coordsY[0]-ypot)/(coordsX[0]-xpot);
		if (a*b < 0)
			return false;
		if (Math.abs(a) > Math.abs(b))
			return false;
		a = (coordsY[2]-coordsY[1])/(coordsX[2]-coordsX[1]);
		b = (coordsY[2]-ypot)/(coordsX[2]-xpot);
		if (a*b < 0)
			return false;
		if (Math.abs(a) > Math.abs(b))
			return false;
		return true;
	}

	void setMouseButtonCallback() {
		//this has to be called from the main thread
		GLFW.glfwSetMouseButtonCallback(resource.window, new GLFWMouseButtonCallbackI() {
			@Override
			public void invoke(long window, int button, int action, int mods){
				if (action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					double xpos[] = new double[1], ypos[] = new double[1];
					GLFW.glfwGetCursorPos(resource.window, xpos, ypos);
					switch(resource.currentPerspective) {
					case menu:
						if (isInBoundsButton(xpos[0], ypos[0], resource.menuButton1CoordsX, resource.menuButton1CoordsY)) {
							resource.currentPerspective = Perspective.game;
							if (speedChanged) {
								resource.saveToFile();
								speedChanged = false;
							}
						}
						else if (isInBoundsButton(xpos[0], ypos[0], resource.menuButton2CoordsX, resource.menuButton2CoordsY)) {
							resource.currentPerspective = Perspective.ranking;
						}
						else if (isInBoundsArrow(xpos[0], ypos[0], resource.menuDecreaseArrowX, resource.menuDecreaseArrowY)) {
							if (resource.speed.decrementAndGet() == 0) {
								resource.speed.set(3);
							}
							speedChanged = true;
						}
						else if (isInBoundsArrow(xpos[0], ypos[0], resource.menuIncreaseArrowX, resource.menuIncreaseArrowY)) {
							if (resource.speed.incrementAndGet() == 4) {
								resource.speed.set(1);
							}
							speedChanged = true;
						}
						break;
					case game:
						executor.execute(new Thread() {
							public void run() {
								try {
									if (lock.tryLock(0, TimeUnit.SECONDS)) {
										try {
											resource.shootPotato(xpos[0], ypos[0]);
										}
										finally {
											lock.unlock();
										}
									}
								}
								catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						});
						break;
					case pause:
						if (isInBoundsButton(xpos[0], ypos[0], resource.pauseButtonCoordsX, resource.pauseButtonCoordsY)) {
							resource.currentPerspective = Perspective.menu;
							resource.setBoard();
						}
						break;
					case gameover:
						resource.currentPerspective = Perspective.menu;
						resource.setBoard();
					default:
						break;	
					}
				}
			}
		});
	}

	void setKeyCallback() {
		GLFW.glfwSetKeyCallback(resource.window, new GLFWKeyCallbackI() {
			@Override
			public void invoke(long window, int key, int scanode, int action, int mods) {
				if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_ESCAPE) {
					switch (resource.currentPerspective) {
					case pause:
						isPaused = !isPaused;
						resource.currentPerspective = Perspective.game;
						break;
					case game:
						isPaused = !isPaused;
						resource.currentPerspective = Perspective.pause;
						break;
					case ranking:
						resource.currentPerspective = Perspective.menu;
					default:
						break;
					}
				}
			}
		});
	}

	void listen() {
		while (!GLFW.glfwWindowShouldClose(resource.window))
			GLFW.glfwWaitEvents();
		GLFW.glfwDestroyWindow(resource.window);
	}

	Interface(Logic Resource) {
		this.resource = Resource;
		lock = new ReentrantLock();
		executor = Executors.newCachedThreadPool();
		isPaused = false;
		speedChanged = false;
		setMouseButtonCallback();
		setKeyCallback();
	}
}
