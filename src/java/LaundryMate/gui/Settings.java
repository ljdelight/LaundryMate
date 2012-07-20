package LaundryMate.gui;

import java.awt.Frame;

import javax.swing.Timer;
import LaundryMate.serial.*;

public class Settings 
{
	public final static String DEFAULT_MESSAGE_WASHER = "Your washer cycle is complete!";
	public final static String DEFAULT_MESSAGE_DRYER  = "Your dryer cycle is complete!";
	public final static String TITLE_DISCONNECTED = "*** NOT CONNECTED--EDIT SETTINGS ***";
	public final static String TITLE_CONNECTED = "USB Device Is Readable";
	
	private boolean notifyWasherFinished;
	private boolean notifyDryerFinished;
	private String msg_dryer;
	private String msg_washer;
	private String email;
	private String com;
	private SerialReader serialReader;
	private Frame frame;
	private Timer timer_washer;
	private Timer timer_dryer;
	
	public Settings( Timer timer_washer, Timer timer_dryer, SerialReader sReader, Frame frame )
	{
		this.frame = frame;
		frame.setTitle(TITLE_DISCONNECTED);
		
		this.timer_washer = timer_washer;
		this.timer_dryer = timer_dryer;
		
		timer_washer.setInitialDelay(5*60*1000); /* 5 min */
		timer_dryer.setInitialDelay(1*60*1000);  /* 1 min */
		
		serialReader = sReader; 
		notifyWasherFinished = true;
		notifyDryerFinished = true;
		msg_dryer = DEFAULT_MESSAGE_DRYER;
		msg_washer = DEFAULT_MESSAGE_WASHER;
		
		email = "";
		com = "";
	}
	
	public String getComIdentifier()
	{
		return com;
	}
	public void setComIdentifier( String s )
	{
		if ( s.isEmpty() )
		{
			System.err.println("INFO: changing COM from '" + com + "' to empty;" );
			com = s;
			serialReader.disconnect();
			frame.setTitle(TITLE_DISCONNECTED);
		} 
		else
		{
			System.err.println("INFO: changing COM from '" + com + "' to '" + s + "'; starting listen" );
			serialReader.setComAndListen(s);
			com = s;
			if ( serialReader.isConnected() )
			{
				frame.setTitle(TITLE_CONNECTED);
			}
		}
	}
	
	public String getDryerMessage()
	{
		return msg_dryer;
	}
	public void setDryerMessage( String s )
	{
		msg_dryer = s;
	}
	
	public String getWasherMessage()
	{
		return msg_washer;
	}
	public void setWasherMessage( String s ) 
	{
		msg_washer = s;
	}
	
	public boolean getDryerNotify() { return notifyDryerFinished; }
	public void setDryerNotify( boolean val ) { notifyDryerFinished = val; }
	public void toggleDryerNotify() { notifyDryerFinished = ! notifyDryerFinished; }
	
	public boolean getWasherNotify() { return notifyWasherFinished; }
	public void setWasherNotify( boolean val ) { notifyWasherFinished = val; }
	public void toggleWasherNotify() { notifyWasherFinished = ! notifyWasherFinished; }
	
	public String getEmail() { return email; }
	
	public void setDryerDelayInMin( int timeInMin )
	{
		int time_ms = timeInMin * 60 * 1000;
		timer_dryer.setInitialDelay(time_ms);
		if ( timer_dryer.isRunning() ) { timer_dryer.restart(); }
		System.err.println("INFO: setting Dryer delay to " + timeInMin + " minutes");
	}
	public void setWasherDelayInMin( int timeInMin )
	{
		int time_ms = timeInMin * 60 * 1000;
		timer_washer.setInitialDelay(time_ms);
		if ( timer_washer.isRunning() ) { timer_washer.restart(); }
		System.err.println("INFO: setting Washer delay to " + timeInMin + " minutes");
	}
	
	public int getDryerDelayInMin()
	{
		int delay_ms = timer_dryer.getInitialDelay();
		return delay_ms / 1000 / 60;
	}
	public int getWasherDelayInMin()
	{
		int delay_ms = timer_washer.getInitialDelay();
		return delay_ms / 1000 / 60;
	}
	
	
	// comma delimited list
	public void setEmail( String email )
	{
		this.email = email;
	}
	public void addEmail( String addr ) 
	{ 
		if ( email.isEmpty() )
		{
			email = addr;
		}
		else
		{
			email = email + "," + addr; 
		}
	}
}
