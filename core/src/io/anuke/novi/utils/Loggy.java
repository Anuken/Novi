package io.anuke.novi.utils;

import java.io.*;

import com.esotericsoftware.minlog.Log.Logger;

import io.anuke.novi.Novi;

public class Loggy extends Logger{
	public void log(int level, String category, String message, Throwable ex){
		StringBuilder builder = new StringBuilder(256);
		builder.append('[');
		builder.append(category);
		builder.append("] ");
		builder.append(message);
		if(ex != null){
			StringWriter writer = new StringWriter(256);
			ex.printStackTrace(new PrintWriter(writer));
			builder.append('\n');
			builder.append(writer.toString().trim());
		}
		Novi.log(builder.toString());
	}
}
