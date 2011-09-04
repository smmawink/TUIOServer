package de.mdgonline.tuioServer;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import de.mdgonline.tuioServer.touches.CursorTouch;
import de.mdgonline.tuioServer.touches.Touch;



public class TUIOServer implements Runnable{
		
	private OSCPortOut oscPort;
	private boolean running = true;
	
	private int count = 1;
	
	private Map<Integer, Touch> setTouches = new TreeMap<Integer, Touch>(); // all new and updated touches
	private Map<Integer, Touch> aliveTouches = new TreeMap<Integer, Touch>(); // alive Touches
	
	private int waitTime = 33;
		
	private CopyOnWriteArrayList<Touch> currentTouchesOnTable = new CopyOnWriteArrayList<Touch>();
		
	public enum ProfilState {TWODCUR , TWODOBJ};
	private ProfilState profilState;
	
	
	public TUIOServer(String host, int port){

		profilState = ProfilState.TWODCUR;
		
		try {
			oscPort = new OSCPortOut(java.net.InetAddress.getByName(host), port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread( this ).start();
		System.out.println("TUIO Server started: " + host+":"+port);
	}
	
	
	
    public void run() {
    	
        while(running) {


     			handleTouchState();
    			sendOSCBundle();


				


	            try {
	                    Thread.sleep(waitTime);
	            } catch (InterruptedException e) {
	            }        
	        
        }
    }



	private synchronized void handleTouchState(){
				
		
		
		CopyOnWriteArrayList<Touch> newTouchState = currentTouchesOnTable;
		
		//System.out.println("##############################################");
		//System.out.println("neue touches erhalten: " + newTouchState.size());
		
		// generate new set and alive lists
		Map<Integer, Touch> newSetTouches = new TreeMap<Integer, Touch>(); // new SetTouch List
		Map<Integer, Touch> newAliveTouches = new TreeMap<Integer, Touch>(); // new AliveTouch List
		
		// 0. Prüfen ob touch vorhanden
			// 1. touch noch nicht vorhanden
			// 2. touch bereits in Alive oder Set vorhanden
					// 2.1 prüfen ob sich was geändert hat
							//2.1.1 wenn ja dann zu set
							//2.1.2 wenn nein dann zu alive

		
        for(Touch touch : newTouchState) {
        	
        	//System.out.println("touchID " + touch.getID() + ":");
        	
        	if(!(setTouches.containsKey(touch.getID()) || aliveTouches.containsKey(touch.getID()))) // prüfe ob touch schon vorhanden
        	{
        		newSetTouches.put(touch.getID(), touch); // wenn nicht dann füge ihn zu newSetTouches
        		System.out.println("SettouchID " + ((CursorTouch)touch).getID() + ": "  + ((CursorTouch)touch).getXpos() + " " + ((CursorTouch)touch).getYpos() + " " + ((CursorTouch)touch).getXspeed() + " " + ((CursorTouch)touch).getYspeed() + " " +((CursorTouch)touch).getMaccel());
        	}
        	else
        	{
    			if(touch.getTouchHistoryValues().size()>=1)
    			{
            		if(check4Changes(touch)) // nun prüfen ob die Werte des alten und neuen Touch identisch sind (zwecks Zuornung Alive und Set)
            		{
            			newSetTouches.put(touch.getID(), touch);
            			//System.out.println("UpdateTouchID " + ((CursorTouch)touch).getID() + ": "  + ((CursorTouch)touch).getXpos() + " " + ((CursorTouch)touch).getYpos() + " " + ((CursorTouch)touch).getXspeed() + " " + ((CursorTouch)touch).getYspeed() + " " +((CursorTouch)touch).getMaccel());
            		}
    			}


        		newAliveTouches.put(touch.getID(), touch);
        		//System.out.println("AliveTouchID " + ((CursorTouch)touch).getID() + ": "  + ((CursorTouch)touch).getXpos() + " " + ((CursorTouch)touch).getYpos() + " " + ((CursorTouch)touch).getXspeed() + " " + ((CursorTouch)touch).getYspeed() + " " +((CursorTouch)touch).getMaccel());

        	}
       	}
        
    	setTouches = newSetTouches;
    	aliveTouches = newAliveTouches;

	}
	
	
	/**
	 *  Compares 2 floats
	 *  return false if they are different
	 */
	private boolean floatsDifferent(float float1, float float2){
		
		//System.out.println("float1 " + float1 + " float2 " + float2 + " float1-float2" + (float1-float2) + " Abs" + Math.abs(float1-float2) + " vergleich: " + (Math.abs(float1-float2) > 0.001));
		
		if(Math.abs(float1-float2) > 0.001)
		{
			return true;
		}
		else
		{
			return false;	
		}
	}
	
	private boolean check4Changes(Touch touch2check){
		
		boolean changed = false;
		
		switch (profilState) {
		case TWODCUR:
			
		CursorTouch cursorTouch = (CursorTouch) touch2check;
					
		if(floatsDifferent(cursorTouch.getTouchHistoryValues().get(cursorTouch.getTouchHistoryValues().size()-1)[0],cursorTouch.getXpos()) 
		|| floatsDifferent(cursorTouch.getTouchHistoryValues().get(cursorTouch.getTouchHistoryValues().size()-1)[1],cursorTouch.getYpos()))
		{
			changed = true;
		}

			
		break;

		case TWODOBJ:
			
			
		break;
		
		
		default:
			
		break;
		}
		
		
		

		return changed;
	}
	
	
	private synchronized void sendOSCBundle(){
				
		
		String profil; // Standardadresskanal
		
		switch (profilState) {
		case TWODCUR:  profil = "/tuio/2Dcur";	break;
		case TWODOBJ:  profil = "/tuio/2Dobj";	break;
		default: profil = "wrong profil";		break;
		}
		
		if(profil.equals("wrong profil"))
		{
			System.err.println("Fehler:" + profil);
			new WrongProfilException("Before sending OSCBundles, set a valid ProfilState!");
		}
		else
		{
			OSCMessage sourceMessage = createSourceMessage(profil);
			OSCMessage aliveMessage = createAliveMessage(profil);
			OSCMessage frameMessage = createFrameMessage(profil);
			
	        List<OSCMessage> setMessages = new LinkedList<OSCMessage>();
	        boolean issueDelete = false;
	        
	        for(Map.Entry<Integer,Touch> entry : setTouches.entrySet()) {
	        	
	            if(!setTouches.containsKey(entry.getKey())) {
	                issueDelete = true;
	                continue;
	        	}
	        	
	        	setMessages.add(createSetMessage(entry.getValue()));
	        }
	      
	        
	        Iterator<OSCMessage> setMessageIterator = setMessages.iterator();
	        
	        while(setMessageIterator.hasNext()) { // sende setMessages immer in fünferpacketen
	                OSCBundle bundle = new OSCBundle();
	                       
	                bundle.addPacket(sourceMessage);
	                bundle.addPacket(aliveMessage);

	                int messageCount = 0;

	                while(setMessageIterator.hasNext() && messageCount < 5) {
	                        bundle.addPacket(setMessageIterator.next());
	                        messageCount++;
	                }
	                       
	                if(!setMessageIterator.hasNext()) {
	                        bundle.addPacket(frameMessage);
	                }

	        		try { 
	        			oscPort.send(bundle); 
	        		}
	        		catch (java.io.IOException e) {}
	        }
	       
	        if(setMessages.isEmpty() && issueDelete) {
	                OSCBundle bundle = new OSCBundle();
	                bundle.addPacket(sourceMessage);
	                bundle.addPacket(aliveMessage);
	                bundle.addPacket(frameMessage);

	        		try { oscPort.send(bundle); }
	        		catch (java.io.IOException e) {}
	        }
		}

	}
	
	private OSCMessage createSourceMessage(String profil){
		
		OSCMessage sourceMessage = new OSCMessage(profil);
        sourceMessage.addArgument("source");
        sourceMessage.addArgument("LupeServer");  
                        
        return sourceMessage;
	}
	
	
	private OSCMessage createAliveMessage(String profil){
		
		OSCMessage aliveMessage = new OSCMessage(profil);
		aliveMessage.addArgument("alive");
                        
        for(Map.Entry<Integer,Touch> entry : aliveTouches.entrySet()) {
        	aliveMessage.addArgument(entry.getKey());
        }
		
        return aliveMessage;
	}
	
	
	private OSCMessage createFrameMessage(String profil){
		
        OSCMessage frameMessage = new OSCMessage(profil);
        frameMessage.addArgument("fseq");
        frameMessage.addArgument(count);
        count++;
		
        return frameMessage;
	}
	
	
	private OSCMessage createSetMessage(Touch touch){
		
		OSCMessage setMessage = null;
		
		switch (profilState) {
		case TWODCUR:  
			
			setMessage = new OSCMessage(touch.getProfil()); 
	        setMessage.addArgument("set");
	        setMessage.addArgument(touch.getID());
	        setMessage.addArgument(new Float(((CursorTouch)touch).getXpos()));
	        setMessage.addArgument(new Float(((CursorTouch)touch).getYpos()));
	        setMessage.addArgument(new Float(((CursorTouch)touch).getXspeed()));
	        setMessage.addArgument(new Float(((CursorTouch)touch).getYspeed()));
	        setMessage.addArgument(new Float(0));
			
		break;

		case TWODOBJ:  
			
			
			
		
			
			
			
			
		break;
		
		default: setMessage = null; break;
		}
		
                        
        return setMessage;
	}
	

	
	public void setTouchesOnTable(ArrayList<Touch> touches){
		
		currentTouchesOnTable.clear();
		currentTouchesOnTable.addAll(touches);
		
	}

	
	
	public void switchProfilState(ProfilState state){
		profilState = state;
		currentTouchesOnTable.clear();
		setTouches.clear();
		aliveTouches.clear();
	}

}


class WrongProfilException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5087802484587588398L;

	public WrongProfilException(String fehlermeldung) {
        super(fehlermeldung);
    }
}

