package test;

import httpServer.booter;

public class testad {
	public static void main(String[] args) {
		booter booter = new booter();
		System.out.println("GrapeAD!");
		try {
			System.setProperty("AppName", "GrapeAD");
			booter.start(6002);
		} catch (Exception e) {

		}
	}
}
