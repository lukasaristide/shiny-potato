package shiny_potatoes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

public class Interface{
	private Logic resource;
	private ReentrantLock lock;
	private ExecutorService executor;
	
	void setMouseButtonCallback() {
		//this has to be called from the main thread
		GLFW.glfwSetMouseButtonCallback(resource.window, new GLFWMouseButtonCallbackI() {
			@Override
			public void invoke(long window, int button, int action, int mods){
				if (action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					switch(resource.currentPerspective) {
					case menu:
						resource.currentPerspective = Perspective.game;
						break;
					case game:
						if (lock.isLocked()) {
							//System.out.println("locked");
							break;
						}
						double xpos[] = new double[1], ypos[] = new double[1];
						GLFW.glfwGetCursorPos(resource.window, xpos, ypos);
						executor.execute(new Thread() {
							public void run() {
								try {
									lock.lock();
									resource.shootPotato(xpos[0], ypos[0]);
								}
								catch (InterruptedException e) {
									e.printStackTrace();
								}
								finally {
									lock.unlock();
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

	Interface(Logic Resource) {
		this.resource = Resource;
		lock = new ReentrantLock();
		executor = Executors.newCachedThreadPool();
		setMouseButtonCallback();
	}
}
