package de.mdgonline.tuioServer.touches;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;


public class Touch {

	protected static int touchcount = 0;
	protected int id;
	
	protected final int historyLength = 5;
	
	// History
	protected CopyOnWriteArrayList<Date> touchHistoryTime = new CopyOnWriteArrayList<Date>();
	protected CopyOnWriteArrayList<float[]> touchHistoryValues = new CopyOnWriteArrayList<float[]>();
	
	
	protected float[] values;
	
	protected String profil = "noProfil";
	
	protected Date touchTime;
	
	public Touch(){
		
		Touch.touchcount++;
		this.id = CursorTouch.touchcount;
						
	}
	
	public int getID() {
		return id;
	}
	

		
	
	protected synchronized void add2History(float[] array4history){
		
		if(touchHistoryValues.size()<=historyLength)
		{
			touchHistoryTime.add(touchTime);
			touchHistoryValues.add(array4history);
		}
		else
		{
			touchHistoryTime.remove(0);
			touchHistoryTime.add(touchTime);
			
			touchHistoryValues.remove(0);
			touchHistoryValues.add(array4history);
		}
		
	}

	public CopyOnWriteArrayList<float[]> getTouchHistoryValues() {
		return touchHistoryValues;
	}
	
	public CopyOnWriteArrayList<Date> getTouchHistoryTimeStamps() {
		return touchHistoryTime;
	}

	public String getProfil() {
		return profil;
	}

	public Date getTouchTime() {
		return touchTime;
	}

	
}
