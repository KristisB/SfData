package Services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class EmailHandler {
    String login = "jjonaitis1901@gmail.com";
    String pass="Vikingas01";
    String host = "smtp.gmail.com";
    String port= "587";

    public void sendEmail(String receiver, String newPassword) {
//        SETTING REQUIRED PROPERTIES
//        mail.smtp.auth
//        mail.smtp.starttls.enable
//        mail.smtp.host - smtp.gmail.com
//        mail.smtp.port - 587

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(login,pass);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(login));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            message.setSubject("models.Workout Reservation: Password has been changed");
            message.setText("Hello,\n\n your password has been reset. New temporary password is: "+
                    newPassword+"\n\n dont forget to change it after you log in.\n\n Best regards, \n Support team");

            // Send message
            Transport.send(message);
            System.out.println("email sent");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
