package hrm.common.reportform;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.vaadin.ui.Window;

public class SendEmail extends Window
{
	public SendEmail() throws AddressException, MessagingException
	{
		Test();
	}

	private void Test() throws AddressException, MessagingException
	{
		String host = "smtp.gmail.com";
		String from = "yeaheabulbul911@gmail.com";
		String pass = "";
		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true"); // added this line
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		String[] to = {"support@eslctg.com"}; // added this line

		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));

		InternetAddress[] toAddress = new InternetAddress[to.length];

		// To get the array of addresses
		for( int i=0; i < to.length; i++ )
		{ // changed from a while loop
			toAddress[i] = new InternetAddress(to[i]);
		}

		System.out.println(Message.RecipientType.TO);

		for( int i=0; i < toAddress.length; i++)
		{ // changed from a while loop
			message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		}

		message.setSubject("sending in a group");
		message.setText("Welcome to JavaMail");

		// create the message part 
		MimeBodyPart messageBodyPart = new MimeBodyPart();

		//fill message
		messageBodyPart.setText("Hi");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		String fileAttachment = "d://asd.pdf";

		// Part two is attachment
		messageBodyPart = new MimeBodyPart();
		DataSource source = 
				new FileDataSource(fileAttachment);
		messageBodyPart.setDataHandler(
				new DataHandler(source));
		messageBodyPart.setFileName(fileAttachment);
		multipart.addBodyPart(messageBodyPart);

		// Put parts in message
		message.setContent(multipart);

		Transport transport = session.getTransport("smtp");
		transport.connect(host, from, pass);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		showNotification("Send successfully.");
	}
}
