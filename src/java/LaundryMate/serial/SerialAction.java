package LaundryMate.serial;
public interface SerialAction 
{
	public void execute( byte[] serialData, int numBytes );
}
