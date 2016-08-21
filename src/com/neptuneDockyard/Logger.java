package com.neptuneDockyard;

public class Logger {

	public Logger(String filename){
		System.out.println("Starting Logger");
	}
	
	public void log(String msg, String level){
		System.out.println("LOG: "+ msg + ", " + level);
	}
}
