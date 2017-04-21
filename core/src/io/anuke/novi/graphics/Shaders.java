package io.anuke.novi.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shaders{
	public static ShaderProgram outline;
	
	public static void loadAll(){
		outline = load("outline", "outline");
	}
	
	public static void setParams(ShaderProgram shader, Object... params){
		if(shader == outline){
			shader.begin();
			shader.setUniformf("u_color", new Color((float)params[0], (float)params[1], (float)params[2], (float)params[3]));
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
