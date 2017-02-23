package io.anuke.novi.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Shaders{
	public static ShaderProgram outline;
	
	public static void loadAll(){
		outline = load("outline", "outline");
	}
	
	public static void setParams(ShaderProgram shader, Object... params){
		if(shader == outline){
			float width = Gdx.graphics.getWidth();
			float height = Gdx.graphics.getHeight();
			
			shader.begin();
			shader.setUniformf("u_viewportInverse", new Vector2(1f / width, 1f / height));
			//shader.setUniformf("u_offset", 1f);
			//shader.setUniformf("u_step", Math.min(1f, width / 70f));
			shader.setUniformf("u_color", new Vector3((float)params[0], (float)params[1], (float)params[2]));
			shader.end();
		}
	}
	
	private static ShaderProgram load(String fragl, String vertl){
		String frag = Gdx.files.internal("shaders/" + fragl + ".fragment").readString();
		String vert = Gdx.files.internal("shaders/" + vertl + ".vertex").readString();
		
		ShaderProgram program = new ShaderProgram(vert, frag);
		
		if(!program.isCompiled()){
			throw new RuntimeException("Error compiling shader " + fragl + "/" + vertl + ": " + program.getLog());
		}
		
		return program;
	}
}
