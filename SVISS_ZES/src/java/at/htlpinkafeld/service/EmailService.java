/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Martin Six
 */
public class EmailService {

    private static void sendEmail(String subject, String body, User from, List<User> to) {
        // Assuming you are sending email from localhost
        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            if (from != null) {
                message.setFrom(new InternetAddress(from.getEmail()));
            } else {
                message.setFrom(new InternetAddress("admin@noplan.at"));
            }

            // Set To: header field of the header.
            for (User u : to) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(u.getEmail()));
            }

            // Set Subject: header field
            message.setSubject(subject, "utf-8");

            // Now set the actual message
            message.setText(body, "utf-8", "html");

            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void sendUserEnteredAbsenceEmail(Absence a, List<User> approver) {
        String subject = "";
        String body = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Krankenstand : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                break;
            case "holiday":
                subject = "Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                body = sender.getPersName() + " hat einen Urlaubsantrag für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " gemacht.";
                if (!a.getReason().isEmpty()) {
                    body += "\n Grund: " + a.getReason();
                }
                break;
            case "time compensation":
                subject = "Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                body = sender.getPersName() + " hat einen Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " gemacht.";
                if (!a.getReason().isEmpty()) {
                    body += "\n Grund: " + a.getReason();
                }
                break;
            case "business-related absence":
                subject = "";
                break;
            default:
                break;
        }

        sendEmail(subject, body, sender, approver);
    }

    public static void sendAcknowledgmentEmail(Absence a, User approver, List<User> otherApprover) {
        String subject = "";
        String bodyApprover = "";
        String bodySender = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                break;
            case "holiday":
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                bodyApprover = "Der Urlaubsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " wurde von " + approver.getPersName() + " angenommen.";
                bodySender = "Ihr Urlaubsantrag für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " wurde von " + approver.getPersName() + " angenommen.";
                break;
            case "time compensation":
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                bodyApprover = "Der Antrag auf Zeitausgleich von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " wurde von " + approver.getPersName() + " angenommen.";
                bodySender = "Ihr Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " wurde von " + approver.getPersName() + " angenommen.";
                break;
            default:
                break;
        }

        List<User> senderL = new ArrayList<>();
        senderL.add(sender);

        sendEmail(subject, bodyApprover, approver, otherApprover);
        sendEmail(subject, bodySender, approver, senderL);
    }

    public static void sendAbsenceDeleted(Absence a, User approver, List<User> otherApprover) {
        String subject = "";
        String bodyApprover = "";
        String bodySender = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime() + " zurückgewiesen";
                break;
            case "holiday":
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                bodyApprover = "Der Urlaubsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                bodySender = "Ihr Urlaubsantrag für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                break;
            case "time compensation":
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                bodyApprover = "Der Antrag auf Zeitausgleich von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                bodySender = "Ihr Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                break;
            default:
                break;
        }

        List<User> senderL = new ArrayList<>();
        senderL.add(sender);

        sendEmail(subject, bodyApprover, approver, otherApprover);
        sendEmail(subject, bodySender, approver, senderL);
    }

    public static void sendUserDeletedOwnAbsenceEmail(Absence a, List<User> approver) {
        String subject = "";
        String body = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                body = sender.getPersName() + " hat den Krankenstand von" + " " + a.getStartTime() + " -- " + a.getEndTime() + " gelöscht";
                break;
            case "holiday":
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                body = sender.getPersName() + " hat den Urlaubsantrag für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " gelöscht.";
                break;
            case "time compensation":
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime() + " -- " + a.getEndTime();
                body = sender.getPersName() + " hat den Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime() + " bis " + a.getEndTime() + " gelöscht.";
                break;
            case "business-related absence":
                subject = " ";
                break;
            default:
                break;
        }

        sendEmail(subject, body, sender, approver);
    }

    public static void sendUserForgotPasswordEmail(User sender, List<User> admins) {
        String subject;
        String body;

        subject = sender.getUsername() + " vergaß das Passwort";
        body = sender.getPersName() + " hat das Passwort vergessen und fordert ein neues an.";

        sendEmail(subject, body, sender, admins);
    }

    public static void sendUserNewPasswordEmail(String newPassword, User user) {
        String subject;
        String body;

        subject = "Passwort wurde zurückgesetzt";
        body = "Hallo, dein neues Passwort lautet " + newPassword + " !";

        List<User> userL = new ArrayList<>();
        userL.add(user);

        sendEmail(subject, body, null, userL);
    }

}
