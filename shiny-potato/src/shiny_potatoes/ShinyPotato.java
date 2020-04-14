package shiny_potatoes;

//import org.lwjgl.glfw.GLFW;	code for testing display

public class ShinyPotato {
	public static void main(String[] args) throws InterruptedException {
		//creation of the whole structure
		Logic logic = new Logic();
		@SuppressWarnings("unused")
		Interface interfac = new Interface(logic);
		Graphic graphic = new Graphic(logic);
		//start of the graphic
		graphic.start();
		
		/* code for testing display
		while(!GLFW.glfwWindowShouldClose(logic.window))
			GLFW.glfwWaitEvents();
		GLFW.glfwDestroyWindow(logic.window);
		GLFW.glfwTerminate();
		*/
	}
}
