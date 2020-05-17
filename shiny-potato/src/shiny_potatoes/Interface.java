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

	boolean isInBounds(double xpos, double ypos, int[] coordsX, int[] coordsY) {
		// position of the cursor in potatoes
		int xpot = (int) ((xpos * resource.columns + resource.width - 1) / resource.width) - 1;
		int ypot = (int) ((ypos * resource.rows + resource.height - 1) / resource.height) - 1;
		if (xpot < coordsX[0] || xpot >= coordsX[1])
			return false;
		if (ypot < coordsY[0] || ypot >= coordsY[2])
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
						if (isInBounds(xpos[0], ypos[0], resource.menuButton1CoordsX, resource.menuButton1CoordsY))
							resource.currentPerspective = Perspective.game;
						else if (isInBounds(xpos[0], ypos[0], resource.menuButton2CoordsX, resource.menuButton2CoordsY))
							resource.currentPerspective = Perspective.ranking;
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
						if (isInBounds(xpos[0], ypos[0], resource.pauseButtonCoordsX, resource.pauseButtonCoordsY)) {
							resource.currentPerspective = Perspective.menu;
							resource.setBoard();
						}
						break;
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
		setMouseButtonCallback();
		setKeyCallback();
	}
}
