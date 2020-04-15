package shiny_potatoes;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

public class Interface{
	private Logic resource;
	
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
						double xpos[] = new double[1], ypos[] = new double[1];
						GLFW.glfwGetCursorPos(resource.window, xpos, ypos);
						resource.shootPotato(xpos[0], ypos[0]);
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
		setMouseButtonCallback();
	}
}
