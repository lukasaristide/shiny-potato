package shiny_potatoes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;


public class Texture{
	ByteBuffer image;
	int id;
	int width, height;
	void bind() {
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, id);
	}
	public Texture() {}
	Texture(String filename) throws IOException{
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer comp = BufferUtils.createIntBuffer(1);
		
		image = STBImage.stbi_load(filename, width, height, comp, 4);
		
		id = GL46.glGenTextures();
		this.width = width.get();
		this.height = height.get();
		bind();
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
		GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGBA, this.width, this.height, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, image);
	}
}