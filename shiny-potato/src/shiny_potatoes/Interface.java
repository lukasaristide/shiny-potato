package shiny_potatoes;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

//I decided to use glfw's callbacks instead of creating new thread
public class Interface{
	private Logic resource;
	
	void setMouseButtonCallback() {
		//this has to be called from the main thread
		GLFW.glfwSetMouseButtonCallback(resource.window, new GLFWMouseButtonCallbackI() {
			@Override
			public void invoke(long window, int button, int action, int mods){
				if (action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					//do something
				}
			}
		});
	}

	Interface(Logic Resource) {
		this.resource = Resource;
		setMouseButtonCallback();
	}
}
