package LaundryMate.serial;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;


public class SerialReader implements SerialPortEventListener
{    
    private InputStream inputStream;
    private SerialPort serialPort;
    
    private boolean disconnected = true;
    private SerialAction serialAction;
    private static final int BUFFER_CAPACITY = 512;
    byte[] buffer;
  
    public static ArrayList<String> getPortIdentifiers()
    {
    	ArrayList<String> list = new ArrayList<String>();
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                list.add(portId.getName());
            }
        }
        return list;

    }
    
    public void startListen()
    {
    	if ( disconnected ) {
    		System.err.println("ERROR: SerialReader startListen called after port was disconnected");
    		return; 	
    	}
    	System.err.println("INFO: SerialReader: clearing COM istream then starting listen");
    	try {
    		// CLEAR THE STREAM before we begin listening
    		int bytesAvail=inputStream.available();
    		if ( bytesAvail > 0 )
    		{
    			byte[] buff = new byte[bytesAvail];
    			inputStream.read( buff, 0, bytesAvail );
    			buff = null;
    		}
    		
    		// set notify to ENABLED
			serialPort.notifyOnDataAvailable(true);
			
    	} catch (IOException err) {
			throw new IllegalStateException( err.getMessage() + ";;;;" + err.getStackTrace() );
		} 
    }
    
    public void stopListen()
    {
    	if ( disconnected ) { 
    		System.err.println("ERROR: SerialReader stopListen called after port was disconnected");
    		return; 
    	}
    	else {
    		System.err.println("INFO: SerialReader: haltling listen on COM");
    		serialPort.notifyOnDataAvailable(false);
    	}
    }
    
    public boolean isConnected()
    {
    	return !disconnected;
    }
    public void setComAndListen( String portName )
    {
    	if ( isConnected() )
    	{
    		disconnect();
    	}
    	setupCom(portName);
    	startListen();
    }

    public SerialReader(SerialAction serialAction )
    {   
    	disconnected = true;
        this.serialAction = serialAction;   
    }
    public SerialReader(String portName, SerialAction serialAction )
    {
        
        this.serialAction = serialAction;
        
        setupCom( portName );
        
        // TODO: remove; debugging.
        try {
        	System.err.print( "AVAILABLE ComPortIdentifier names:" );
        	Enumeration<CommPortIdentifier> s = CommPortIdentifier.getPortIdentifiers();
			while ( s.hasMoreElements() )
			{
				System.err.print( " " + s.nextElement().getName() );
			}
			System.err.print( "\n" );
        } catch ( Exception e )
        {
        	System.err.println("exception while listing CommPortIdentifiers");
        }
	}
    
    private void setupCom( String portName )
    {
    	 try
         {
    		 buffer = new byte[BUFFER_CAPACITY];
    		 
         	// may throw NoSuchPortException
         	CommPortIdentifier portId = (CommPortIdentifier) CommPortIdentifier.getPortIdentifier(portName);
 			
 			// may throw PortInUseException
 			serialPort = (SerialPort) portId.open("SerialReader", 2000);
 			
 			// may throw IOException
 			inputStream = serialPort.getInputStream();
 			serialPort.setInputBufferSize(10);

 	    	// may throw TooManyListenersException
 			serialPort.addEventListener(this);
 			
 			// may throw UnsupportedCommOperationException
 			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
 					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
 			
 			disconnected = false;

 		} catch ( NoSuchPortException err ) {
 			throw new IllegalArgumentException("CommPort '" + portName + "' does not exist!\n" + err.getStackTrace() );
 		} catch ( PortInUseException err ) {
 			throw new IllegalStateException("CommPort " + portName + " + exists but is in use!\n" + err.getStackTrace() );
 		} catch (IOException err) {
 			throw new IllegalStateException( err.getMessage() + "\n" + err.getStackTrace() );
 		} catch (TooManyListenersException err) {
 			throw new IllegalStateException( err.getMessage() + "\n" + err.getStackTrace() );
     	} catch (UnsupportedCommOperationException err) {
 			throw new IllegalStateException( err.getMessage() + "\n" + err.getStackTrace() );			
 		}
    }
    
    @Override
    public void serialEvent(SerialPortEvent event)
    {
    	if ( disconnected ) 
    	{ 
    		System.err.println("ERROR: SerialReader is DISCONNECTED but a serialEvent exectued");
    		return; 
    	}
    	switch ( event.getEventType() )
    	{
	    	case SerialPortEvent.DATA_AVAILABLE:
	    		
	    		try
	             {
	                 int numBytes = inputStream.read(buffer);
	                 serialAction.execute(buffer, numBytes);
	             } catch(IOException err)
	             {
	            	 err.printStackTrace();
	            	 throw new IllegalStateException( err.getMessage() + "\n" + err.getStackTrace() );	
	             }
	             
	    		break;
	    	
	    	case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
    	}
    }
    

    public void disconnect()
    {
    	if ( serialPort != null )
    	{
    		System.err.println("INFO: SerialReader: disconnecting COM port");
    		disconnected = true;
    		try {
				inputStream.close();
			} catch (IOException e) { /* we don't care */ }
    		serialPort.close();
    		serialPort = null;
    		inputStream = null;
    		buffer = null;
    	}
    }
}




