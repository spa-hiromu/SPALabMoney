package net.jp.keys.sunohara.labmoney.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class sendGmail {
    private static final String fromGmailAddress = "spalabmoney@gmail.com";
    public sendGmail(String toAddress) {
        Properties props = setProperties();

        Session session = javax.mail.Session.getDefaultInstance(props);
        session.setDebug(true);

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setSubject("gmail送信テスト", "utf-8");
            msg.setFrom(new InternetAddress(fromGmailAddress));
            msg.setSender(new InternetAddress(fromGmailAddress));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
            msg.setText("gmail送信テスト", "utf-8");

            Transport t = session.getTransport("smtp");
            t.connect(fromGmailAddress, "");
            t.sendMessage(msg, msg.getAllRecipients());
        } catch (MessagingException e) {
            e.printStackTrace();

        }
    }

    private Properties setProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return props;
    }
}
