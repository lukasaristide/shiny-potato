package shiny_potatoes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

public class Interface{
	private Logic resource;
	private ReentrantLock lock;
	private ExecutorService executor;
	private boolean isPaused;
	
	void setMouseButtonCallback() {
		//this has to be called from the main thread
		GLFW.glfwSetMouseButtonCallback(resource.window, new GLFWMouseButtonCallbackI() {
			@Override
			public void invoke(long window, int button, int action, int mods){
				if (isPaused)
					return;
				if (action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					switch(resource.currentPerspective) {
					case menu:
						resource.currentPerspective = Perspective.game;
						break;
					case game:
						double xpos[] = new double[1], ypos[] = new double[1];
						GLFW.glfwGetCursorPos(resource.window, xpos, ypos);
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
					isPaused = !isPaused;
					switch(resource.currentPerspective) {
					case pause:
						resource.currentPerspective = Perspective.game;
						break;
					case game:
						resource.currentPerspective = Perspective.pause;
						break;
					default:
						break;	
					}
				}
			}
		});
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
