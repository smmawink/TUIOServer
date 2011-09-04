package de.mdgonline.tuioServer.touches;
import java.util.Date;

import de.mdgonline.tuioServer.Settings;



public class ObjectTouch extends Touch{
	
 	int classID;

	
	public ObjectTouch(int classID, float xPos, float yPos, float angle){
		
		super();
		
		profil = "/tuio/2Dobj";
		this.classID = classID;  // Class ID (e.g. marker ID)
		
		// /tuio/2Dobj set s i x y a X Y A m r
		values = new float[8];     
						
		this.values[0] = xPos; //  xpos
		this.values[1] = yPos; //  ypos
		this.values[2] = angle;	   //  a Angle range 0..2PI
		this.values[3] = 0;	   //  XSpeed
		this.values[4] = 0;	   // YSpeed
		this.values[5] = 0;	   // A Rotation velocity vector (rotation speed & direction)
		this.values[6] = 0;		// Motion acceleration (maccel)
		this.values[7] = 0;		// Rotation acceleration
		
	}



	public float getXpos() {
		return values[0];
	}

	public float getYpos() {
		return values[1];
	}

	public float getAngle() {
		return values[2];
	}
	
	public float getXspeed() {
		return values[3];
	}

	public float getYspeed() {
		return values[4];
	}
	
	public float getASpeed() {
		return values[5];
	}

	public float getMaccel() {
		return values[6];
	}
	
	public float getRaccel() {
		return values[7];
	}

	public void setNewTouchValues(int xPos, int yPos, float angle) {
		add2History(values);
		
		touchTime = new Date();
		
		float[] newTouchValues = new float[8];
		float deltaT = touchTime.getTime()-touchHistoryTime.get(touchHistoryTime.size()-1).getTime();
		// richtige zeitdifferenz finden

		
		this.values[0] = (float)xPos / Settings.resolution_width;; //  xpos
		this.values[1] = (float)yPos / Settings.resolution_height; //  ypos
		this.values[2] = angle;	   //  a Angle
		this.values[3] = newTouchValues[0] / deltaT; ;	   //  XSpeed
		this.values[4] = newTouchValues[1] / deltaT;	   // YSpeed
		this.values[5] = (float)((angle - touchHistoryValues.get(touchHistoryValues.size()-1)[2]) / (2*Math.PI));	   // A Rotation velocity vector (rotation speed & direction) ((a - last_a) / 2*PI) / dt
		this.values[6] = (float)Math.sqrt(newTouchValues[0]*newTouchValues[0]+newTouchValues[1]*newTouchValues[1])/deltaT;;		// Motion acceleration (maccel)
		this.values[7] = (values[5] - touchHistoryValues.get(touchHistoryValues.size()-1)[5]) / deltaT;		// Rotation acceleration (A - last_A) / dt
		
		values = newTouchValues;
	}

	
	

	
	
	
}
