package de.mdgonline.tuioServer;


public class Launcher {

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TUIOServer server = new TUIOServer("127.0.0.1",3333);
		TestLupe lupeSim = new TestLupe(server);
	}
	
	
	

}
