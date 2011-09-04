package de.mdgonline.tuioServer;

import java.util.ArrayList;
import java.util.Random;

import de.mdgonline.tuioServer.touches.CursorTouch;
import de.mdgonline.tuioServer.touches.Touch;


public class TestLupe implements Runnable {

	private int waitTime = 50;
	private boolean ready2Create = true;
		
	private Random randomGenerator = new Random();
	
	private ArrayList<Touch> currentTouchesOnTable = new ArrayList<Touch>();
	
	private boolean running = true;
	
	private ArrayList<Touch> newTouchState;
	
	private final int maxTouches = 10;
	
	private TUIOServer server;
		
	
	/**
	 * Creates Touches 4 Testing 
	 * take the created touchs with getNewTouchState()
	 * 
	 */
	public TestLupe(TUIOServer server){
		
		this.server = server;

		new Thread( this ).start();
	}
	
	
	@Override
	public void run() {
		
		
		while(running)
		{
			
			if(ready2Create)
			{
				ready2Create = false;
				newTouchState = generateRandomTouches();
				server.setTouchesOnTable(newTouchState);
                ready2Create = true
                ;
			}

	            try {
	                    Thread.sleep(waitTime);
	            } catch (InterruptedException e) {
	            }

            
			
		}
	}
	
	
	private ArrayList<Touch> generateRandomTouches(){
	
	// 5% chance einen alten touch zu löschen
    
    if(currentTouchesOnTable.size() > 1 && randomGenerator.nextInt(100) > 95)
    {
    	currentTouchesOnTable.remove(randomGenerator.nextInt(currentTouchesOnTable.size()));
    }
	
	
	
	// 20 % chance touch hinzuzufügen  jedoch nicht mehr wie maxTouches
    
    if(currentTouchesOnTable.size() < maxTouches && randomGenerator.nextInt(100) > 80)
    {
    	CursorTouch touch = new CursorTouch(randomGenerator.nextInt(640), randomGenerator.nextInt(480));
    	currentTouchesOnTable.add(touch);
    }
        
		

    for(Touch touch : currentTouchesOnTable)
    {
        manipulateTouch(touch);
    }
    
	return currentTouchesOnTable;
	}
	
	


	
	private void manipulateTouch(Touch touch){
		int newXPos = (int)(((CursorTouch) touch).getXpos()*Settings.resolution_width);
		int newYPos = (int)(((CursorTouch) touch).getYpos()* Settings.resolution_height);
			
		
    	if(randomGenerator.nextInt(100)>70) // 80 % das Wert verändert wird
    	{
		float randomFloat = (float)((randomGenerator.nextInt(10) / (float)100)); 
			
			if(randomGenerator.nextInt(2) == 0) // Richtung ... 50% chance zu subtrahieren oder zu addieren
			{
				newXPos = newXPos - (int)(Settings.resolution_width*randomFloat);
				if(newXPos<0)
				{
					newXPos = 0;
				}
			}
			else
			{
				newXPos = newXPos + (int)(Settings.resolution_width*randomFloat);
				if(newXPos>Settings.resolution_width)
				newXPos = Settings.resolution_width;
					
			}
			
		
		
			
		randomFloat = (float)((randomGenerator.nextInt(10) / (float)100));
		
			if(randomGenerator.nextInt(2) == 0) // Richtung ... 50% chance zu subtrahieren oder zu addieren
			{
				newYPos = newYPos - (int)(Settings.resolution_height*randomFloat);
				if(newYPos<0)
				{
					newYPos = 0;
				}
			}
			else
			{
				newYPos = newYPos + (int)(Settings.resolution_height*randomFloat);
				if(newYPos>Settings.resolution_height)
					newYPos = Settings.resolution_height;
			}
    	}

			//System.out.println("manipulate ... new X: " + newXPos + " new Y: " + newYPos);
		((CursorTouch)touch).setNewTouchValues(newXPos, newYPos);
		
	}


}
