package de.mdgonline.tuioServer.touches;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import de.mdgonline.tuioServer.Settings;


public class CursorTouch extends Touch{
	
 	

	
	public CursorTouch(int xPos, int yPos){
		
		super();
		
		profil = "/tuio/2Dcur";
		
		touchTime = new Date();
		
		// /tuio/2Dcur set s x y X Y m
		values = new float[5];  // [0] = xpos  [1] = ypos [2] = XSpeed [3] = YSpeed [4] = maccel
					
		this.values[0] = (float)xPos / Settings.resolution_width;
		this.values[1] = (float)yPos / Settings.resolution_height;
		this.values[2] = 0f;
		this.values[3] = 0f;
		this.values[4] = 0f;

	}



	public float getXpos() {
		return (float)values[0];
	}

	public float getYpos() {
		return (float)values[1];
	}

	public float getXspeed() {
		return (float)values[2];
	}

	public float getYspeed() {
		return (float)values[3];
	}

	public float getMaccel() {
		return (float)values[4];
	}

	public void setNewTouchValues(int xPos, int yPos) {
		add2History(values);
		
		touchTime = new Date();
		
		float[] newTouchValues = new float[5];
		float deltaT = touchTime.getTime()-touchHistoryTime.get(touchHistoryTime.size()-1).getTime();
		// richtige zeitdifferenz finden
		newTouchValues[0] =  (float)xPos / Settings.resolution_width;
		newTouchValues[1] =  (float)yPos / Settings.resolution_height;
		newTouchValues[2] =  newTouchValues[0] / deltaT; 
		newTouchValues[3] =  newTouchValues[1] / deltaT; 
		newTouchValues[4] =  (float)Math.sqrt(newTouchValues[0]*newTouchValues[0]+newTouchValues[1]*newTouchValues[1])/deltaT;	;
		
		values = newTouchValues;

	}
	
	


	
	
	
}
