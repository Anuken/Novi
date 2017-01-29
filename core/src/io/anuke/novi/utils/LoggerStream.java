package io.anuke.novi.utils;

import java.io.*;

public class LoggerStream extends PrintStream{

	public LoggerStream(String fileName) throws FileNotFoundException{
		super(fileName);
	}
	
	public void println(Object object){
		
	}

}
