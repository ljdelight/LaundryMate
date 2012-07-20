package LaundryMate.mail;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendMail
{
	public static void main( String[] args )
	{
		
	}	
	
	public static void sendMessage( String to, String subject, String msg )
    {
		String host = "smtp.gmail.com";
        String username = "laundrymateprojectx";
        String password = "LaundryMate_GO!!!";
        int port = 587;
        
        try
        { 
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
           
            Session session = Session.getInstance(props);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, port, username, password);
            
            Address[] addresses = InternetAddress.parse(to);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(subject);
            message.setText( msg );
            
            transport.sendMessage(message, addresses);
            
            transport.close();
            System.err.println("SendMail: an email was sent");
        }
        catch(MessagingException e)
        {
            System.err.println( "ERROR: EXCEPTION in SENDMAIL: " + e.getStackTrace() );
            e.printStackTrace();
        }
    }
}
