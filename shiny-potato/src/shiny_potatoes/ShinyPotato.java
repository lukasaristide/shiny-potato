package shiny_potatoes;

import org.lwjgl.glfw.GLFW;

public class ShinyPotato {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException {
		//creation of the whole structure
		Logic logic = new Logic();
		@SuppressWarnings("unused")
		Interface interfac = new Interface(logic);
		Graphic graphic = new Graphic(logic);
		//start of the graphic
		graphic.start();
		//start of the listener
		interfac.listen();
		//terminate lib
		graphic.stop();
		GLFW.glfwTerminate();
		
	}
}
