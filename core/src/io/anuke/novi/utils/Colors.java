package io.anuke.novi.utils;

import com.badlogic.gdx.graphics.Color;

public class Colors{
	public static Color mix(Color a, Color b, float s){
		float i = 1f - s;
		return new Color(a.r*i + b.r*s, a.g*i + b.g*s, a.b*i + b.b*s, a.a*i + b.a*s);
	}
}
