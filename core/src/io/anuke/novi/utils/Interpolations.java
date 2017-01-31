package io.anuke.novi.utils;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;

public class Interpolations{
	static Bezier<Vector2> bezier = new Bezier<Vector2>();
	static CatmullRomSpline<Vector2> spline = new CatmullRomSpline<Vector2>();
	static Vector2 out = new Vector2();
	static Vector2 tmp = new Vector2();
	static Vector2 tempa = new Vector2();
	static Vector2 tempb = new Vector2();
	static Vector2 tempav = new Vector2();
	static Vector2 tempbv = new Vector2();

	static Vector2 curve(float time, float x1, float y1, float x2, float y2, float x1v, float y1v, float x2v, float y2v){
		Bezier.cubic(out, time, tempa.set(x1, y1), tempav.set(x1v, y1v), tempbv.set(x2v, y2v),  tempb.set(x1, y1), tmp);
		return out;
	}
}
