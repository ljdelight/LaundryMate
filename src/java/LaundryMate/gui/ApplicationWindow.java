package LaundryMate.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.PrintStream;


import LaundryMate.serial.SerialReader;
import LaundryMate.serial.SerialAction;
import LaundryMate.mail.SendMail;


public class ApplicationWindow extends JFrame {

	private JFrame frame;
	private final Action openSettingsAction = new SettingsOpen();
	private final Action exitAction = new ExitAction();
	private JPanel panel_washer;
	private JPanel panel_dryer;
	private SerialReader serialReader;
	private Settings settings; 
	
	public static final Color COLOR_ON = Color.GREEN;
	public static final Color COLOR_OFF = Color.RED;
	private final Action action = new SwingAction();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setErr( new PrintStream( new FileOutputStream("syserr.txt",true) ) );
					
					System.err.println("\n\n\n");
					System.err.println("****** STARTING LAUNDRYMATE INSTANCE *******");
					ApplicationWindow window = new ApplicationWindow();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ApplicationWindow() {
		initialize();
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = this;
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		panel_dryer = new JPanel();
		panel_dryer.setBackground( COLOR_OFF );
		panel_dryer.setBounds(210, 12, 226, 221);
		frame.getContentPane().add(panel_dryer);
		
		JLabel lblDryer = new JLabel("DRYER");
		panel_dryer.add(lblDryer);
		
		panel_washer = new JPanel();
		panel_washer.setBackground( COLOR_OFF );
		panel_washer.setBounds(12, 12, 197, 221);
		frame.getContentPane().add(panel_washer);
		
		JLabel lblWasher = new JLabel("WASHER");
		panel_washer.add(lblWasher);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmQuit = new JMenuItem("Exit");
		mntmQuit.setAction(exitAction);
		mnFile.add(mntmQuit);
		
		JMenu mnSettings = new JMenu("Edit");
		menuBar.add(mnSettings);
		
		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mntmPreferences.setAction(openSettingsAction);
		mnSettings.add(mntmPreferences);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setAction(action);
		mnHelp.add(mntmAbout);
		
		try {
			UART_TI_Action uartAction = new UART_TI_Action();
			serialReader = new SerialReader( uartAction );
			settings = new Settings( uartAction.getWasherTimer(), uartAction.getDryerTimer(),serialReader, frame ); 
			
			this.addWindowListener( new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if ( serialReader != null && serialReader.isConnected() )
					{
						serialReader.stopListen();
						serialReader.disconnect();
					}
					System.exit(0);
				}
				});
		} catch ( Exception err )
		{
			System.err.println("EXCEPTION CAUGHT: probably bad COM. " +err.getMessage() );
			err.printStackTrace();
		}
		
	}	
	
    private class UART_TI_Action implements SerialAction
    {
    	private static final byte WASHER_ON  = (byte)0xAA;
    	private static final byte WASHER_OFF = (byte)0xDA;
    	private static final byte DRYER_ON   = (byte)0xAD;
    	private static final byte DRYER_OFF  = (byte)0xDD;
    	private static final int FIVE_MINUTES = 5*60*1000; // 5 mins
    	private static final int TEN_SECONDS = 10*1000;
    	
    	private boolean washerOn;
    	private boolean dryerOn;
    	private Timer timer_washer;
    	private Timer timer_dryer;
    	private ActionListener timer_washer_action;
    	private ActionListener timer_dryer_action;
    	
    	public Timer getWasherTimer()
    	{
    		return timer_washer;
    	}
    	public Timer getDryerTimer()
    	{
    		return timer_dryer;
    	}
    	
		public UART_TI_Action() {
			washerOn = false;
			dryerOn = false;
			timer_washer_action = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.err.println("INFO: washer timer finished; email"
							+ (settings.getWasherNotify() ? " WAS SENT"
									: " wasn't sent due to settings"));

					if (settings.getWasherNotify()) {
						if (settings.getEmail().isEmpty()) {
							System.err.println("WARNING: email settings are empty and transition occurred");
						} else {
							SendMail.sendMessage(settings.getEmail(),
									"Washer Cycle Complete -- LaundryMate",
									settings.getWasherMessage());
						}
					}
					System.err.println("     Setting washer panel color to OFF and state to OFF");
					panel_washer.setBackground(COLOR_OFF);
					washerOn = false;
				}
			};
			timer_dryer_action = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.err.println("INFO: dryer timer finished; email"
									+ (settings.getDryerNotify() ? " WAS SENT"
											: " wasn't sent"));

					if (settings.getDryerNotify()) {
						if (settings.getEmail().isEmpty()) {
							System.err
									.println("WARNING: email settings are empty and transition occurred");
						} else {
							SendMail.sendMessage(settings.getEmail(),
									"Dryer Cycle Complete -- LaundryMate",
									settings.getDryerMessage());
						}
					}

					System.err.println("     Setting dryer panel color to OFF and state to OFF");
					panel_dryer.setBackground(COLOR_OFF);
					dryerOn = false;
				}
			};
			
			// NOTE: the delay time doesn't matter. Settings will overwrite any value we place.
			timer_washer = new Timer( FIVE_MINUTES, timer_washer_action );
			timer_washer.setRepeats(false);
			timer_dryer = new Timer( FIVE_MINUTES, timer_dryer_action );
			timer_dryer.setRepeats(false);
			
		}
    	@Override
		public void execute(byte[] serialData, int numBytes) 
		{
			if ( numBytes >= 1 )
			{
				
				System.err.println("UART_TI_action: " + numBytes + " bytes; firstByte=" + serialData[0] );
				//  LOW nibble: washer (0xA) or dryer (0xD)
				// HIGH nibble:     on (0xA) or   off (0xD)
				switch ( serialData[0] )
				{
				// rcvd status as washer is ON
				case WASHER_ON:
					// if previous state was off, then we have transition OFF->ON
					if ( !washerOn )
					{
						panel_washer.setBackground( COLOR_ON );
						washerOn = true;
						System.err.print("INFO: washer OFF->ON;");
						if ( timer_washer.isRunning() )
						{
							System.err.print(" timer was running; false-positive email avoided");
							timer_washer.stop();
						}
						System.err.print("\n");
					} else { System.err.println("INFO: rcvd washer on; washer status is already set to On; ignoring"); }
					break;
					
				case WASHER_OFF:
					// if ON -> OFF 
					if ( washerOn )
					{
						System.err.println( "INFO: washer transitioned to 'off'; starting timer" );
						timer_washer.restart();
						washerOn=false;
					} else { System.err.println("INFO: rcvd washer off; washer status is already set to Off; ignoring"); }
					break;
					
				case DRYER_ON:
					
					// if OFF -> ON 
					if ( !dryerOn )
					{
						panel_dryer.setBackground( COLOR_ON );
						dryerOn = true;
						System.err.print("INFO: dryer transitioned to ON;");
						if ( timer_dryer.isRunning() )
						{
							System.err.print(" timer was running; false-positive email avoided");
							timer_dryer.stop();
						}
						System.err.print("\n");
					}else { System.err.println("INFO: rcvd dryer on; dryer status is already set to On; ignoring"); }
					break;
					
				case DRYER_OFF:
					
					// if ON -> OFF
					if ( dryerOn )
					{
						System.err.println( "INFO: dryer transitioned to 'off'; starting timer" );
						timer_dryer.restart();
						dryerOn = false;
					} else { System.err.println("INFO: rcvd dryer off; dryer status is already set to Off; ignoring"); }
					break;
				}
			}
			else 
			{
				System.err.println("UART_TI_action called with empty byte[]");
			}
			
		}
    	
    }
    
    
    
	private class SettingsOpen extends AbstractAction {
		public SettingsOpen() {
			putValue(NAME, "Settings");
			putValue(SHORT_DESCRIPTION, "Modify the settings");
		}
		public void actionPerformed(ActionEvent e) {
			try {
				SettingsDialog s = new SettingsDialog(settings, serialReader.getPortIdentifiers() );

				s.setModal(true);
				s.setVisible(true);
			} catch ( Exception err )
			{
				err.printStackTrace();
			}
		}
	}
	private class ExitAction extends AbstractAction {
		public ExitAction() {
			putValue(NAME, "Exit");
			putValue(SHORT_DESCRIPTION, "Quit the program");
		}
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "About");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			AboutDialog s = new AboutDialog();
			s.setModal(true);
			s.setVisible(true);
		}
	}
}
