package com.neptuneDockyard;

public class LoggerSelf {

	public LoggerSelf(String filename) {
		System.out.println("Starting Logger");
	}

	public void log(String msg, String level) {
		System.out.println("LOG: " + msg + ", " + level);
	}
}
