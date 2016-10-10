/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Martin Six
 */
public class EmailService {

    private static final DateTimeFormatter dayFormatter;
    private static final DateTimeFormatter dayTimeFormatter;

    private static final String SERVER_EMAILADDRESS = "zes@sviss.co.at";

    static {
        dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        dayTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    }

    private static void sendEmail(String subject, String body, User from, List<User> to) {
        String host = "smtp.world4you.com";
//        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "25");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");

        if (!to.isEmpty()) {

            // Get the default Session object.
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SERVER_EMAILADDRESS, "wLichanda=");
                }
            });

            try {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Set From: header field of the header.
                if (from != null) {
                    message.setFrom(new InternetAddress(from.getEmail()));
                } else {
                    message.setFrom(new InternetAddress(SERVER_EMAILADDRESS));
                }

                // Set To: header field of the header.
                for (User u : to) {
                    if (!u.getEmail().isEmpty()) {
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(u.getEmail()));
                    }
                }

                // Set Subject: header field
                message.setSubject(subject, "utf-8");

                // Now set the actual message
                message.setText(body, "utf-8", "html");

                // Send message
                Transport.send(message);
            } catch (MessagingException mex) {
                throw new RuntimeException(mex);
            }
        }
    }

    public static void sendUserEnteredAbsenceEmail(Absence a, List<User> approver) {
        String subject = "";
        String body = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = sender.getPersName() + " hat einen Krankenstandsantrag für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " gestellt.";
                if (!a.getReason().isEmpty()) {
                    body += "\n Grund: " + a.getReason();
                }

                break;
            case "holiday":
                subject = "Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(dayFormatter) + " -- " + a.getEndTime().format(dayFormatter);
                body = sender.getPersName() + " hat einen Urlaubsantrag für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " gestellt.";
                if (!a.getReason().isEmpty()) {
                    body += "\n Grund: " + a.getReason();
                }
                break;
            case "time compensation":
                subject = "Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = sender.getPersName() + " hat einen Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " gestellt.";
                if (!a.getReason().isEmpty()) {
                    body += "\n Grund: " + a.getReason();
                }
                break;
            case "business-related absence":
                subject = "Eintrag für eine unternehmensbezogene Abwesenheit: " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = sender.getPersName() + " hat einen Eintrag für eine unternehmensbezogene Abwesenheit für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " gemacht.";
                break;
            default:
                break;
        }

        sendEmail(subject, body, null, approver);
    }

    public static void sendAcknowledgmentEmail(Absence a, User approver, List<User> otherApprover) {
        String subject = "";
        String bodyApprover = "";
        String bodySender = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                bodyApprover = "Der Krankenstandsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " wurde von " + approver.getPersName() + " angenommen.";
                bodySender = "Ihr Krankenstandsantrag für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " wurde von " + approver.getPersName() + " angenommen.";
                break;
            case "holiday":
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(dayFormatter) + " -- " + a.getEndTime().format(dayFormatter);
                bodyApprover = "Der Urlaubsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " wurde von " + approver.getPersName() + " angenommen.";
                bodySender = "Ihr Urlaubsantrag für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " wurde von " + approver.getPersName() + " angenommen.";
                break;
            case "time compensation":
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                bodyApprover = "Der Antrag auf Zeitausgleich von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " wurde von " + approver.getPersName() + " angenommen.";
                bodySender = "Ihr Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " wurde von " + approver.getPersName() + " angenommen.";
                break;
            case "business-related absence":
                subject = "Re: Eintrag für eine unternehmensbezogene Abwesenheit: " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                bodyApprover = "Der Eintrag für eine unternehmensbezogene Abwesenheit von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " wurde von " + approver.getPersName() + " bestätigt.";
                bodySender = "Ihr Eintrag für eine unternehmensbezogene Abwesenheit für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " wurde von " + approver.getPersName() + " bestätigt.";
                break;
            default:
                break;
        }

        List<User> senderL = new ArrayList<>();
        senderL.add(sender);

        sendEmail(subject, bodyApprover, null, otherApprover);
        sendEmail(subject, bodySender, null, senderL);
    }

    public static void sendAbsenceDeletedByApprover(Absence a, User approver, List<User> otherApprover) {
        String subject = "";
        String bodyApprover = "";
        String bodySender = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter) + " zurückgewiesen";
                bodyApprover = "Der Krankenstandsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                bodySender = "Ihr Krankenstandsantrag für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                break;
            case "holiday":
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(dayFormatter) + " -- " + a.getEndTime().format(dayFormatter);
                bodyApprover = "Der Urlaubsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                bodySender = "Ihr Urlaubsantrag für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                break;
            case "time compensation":
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                bodyApprover = "Der Antrag auf Zeitausgleich von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                bodySender = "Ihr Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                break;
            case "business-related absence":
                subject = "Re: Eintrag für eine unternehmensbezogene Abwesenheit: " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                bodyApprover = "Der Eintrag für eine unternehmensbezogene Abwesenheit von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " wurde von " + approver.getPersName() + " gelöscht.";
                bodySender = "Ihr Eintrag für eine unternehmensbezogene Abwesenheit für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " wurde von " + approver.getPersName() + " gelöscht.";
                break;

            default:
                break;
        }

        List<User> senderL = new ArrayList<>();
        senderL.add(sender);

        sendEmail(subject, bodyApprover, null, otherApprover);
        sendEmail(subject, bodySender, null, senderL);
    }

    public static void sendUserDeletedOwnAbsenceEmail(Absence a, List<User> approver) {
        String subject = "";
        String body = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = sender.getPersName() + " hat den Krankenstand von" + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter) + " gelöscht";
                break;
            case "holiday":
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(dayFormatter) + " -- " + a.getEndTime().format(dayFormatter);
                body = sender.getPersName() + " hat den Urlaubsantrag für den Zeitraum von " + a.getStartTime().format(dayFormatter) + " bis " + a.getEndTime().format(dayFormatter) + " gelöscht.";
                break;
            case "time compensation":
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = sender.getPersName() + " hat den Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " gelöscht.";
                break;
            case "business-related absence":
                subject = "Re: Eintrag für eine unternehmensbezogene Abwesenheit : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = sender.getPersName() + " hat den Eintrag für eine unternehmensbezogene Abwesenheit für den Zeitraum von " + a.getStartTime().format(dayTimeFormatter) + " bis " + a.getEndTime().format(dayTimeFormatter) + " gelöscht.";
                break;
            default:
                break;
        }

        sendEmail(subject, body, null, approver);
    }

    public static void sendUserForgotPasswordEmail(User sender, List<User> admins) {
        String subject;
        String body;

        subject = sender.getUsername() + "  sendet Anfrage für Passwortzurücksetzung";
        body = sender.getPersName() + " hat das Passwort vergessen und fordert ein neues an.";

        sendEmail(subject, body, null, admins);
    }

    public static void sendUserNewPasswordEmail(String newPassword, User user) {
        String subject;
        String body;

        subject = "Passwort wurde zurückgesetzt";
        body = "Das neue Passwort für den User " + user.getUsername() + " lautet: " + newPassword;

        List<User> userL = new ArrayList<>();
        userL.add(user);

        sendEmail(subject, body, null, userL);
    }

    public static void sendReminderAcknowledgementEmail(Absence a, List<User> approver) {
        String subject = "";
        String body = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = "Der Krankenstand von " + sender.getPersName() + "von " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter) + " muss noch freigegeben werden!";
                break;
            case "holiday":
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(dayFormatter) + " -- " + a.getEndTime().format(dayFormatter);
                body = "Der Urlaub von " + sender.getPersName() + "von " + a.getStartTime().format(dayFormatter) + " -- " + a.getEndTime().format(dayFormatter) + " muss noch freigegeben werden!";
                break;
            case "time compensation":
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = "Der Zeitausgleich von " + sender.getPersName() + "von " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter) + " muss noch freigegeben werden!";
                break;
            case "business-related absence":
                subject = "Re: Eintrag für eine unternehmensbezogene Abwesenheit : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = "Der Eintrag für eine unternehmensbezogene Abwesenheit  von " + sender.getPersName() + "von " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter) + " muss noch freigegeben werden!";
                break;
            default:
                break;
        }
        sendEmail(subject, body, null, approver);
    }

    public static void sendAbsenceDeletedEmailBySystem(Absence a, List<User> approver) {
        String subject = "";
        String body = "";
        User sender = a.getUser();
        switch (a.getAbsenceType().getAbsenceName()) {
            case "medical leave":
                subject = "Re:Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = "Der Krankenstand von " + sender.getPersName() + "von " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter) + " wurde vom System gelöscht!";
                break;
            case "holiday":
                subject = "Re:Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(dayFormatter) + " -- " + a.getEndTime().format(dayFormatter);
                body = "Der Urlaub von " + sender.getPersName() + "von " + a.getStartTime().format(dayFormatter) + " -- " + a.getEndTime().format(dayFormatter) + " wurde vom System gelöscht!";
                break;
            case "time compensation":
                subject = "Re:Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = "Der Zeitausgleich von " + sender.getPersName() + "von " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter) + " wurde vom System gelöscht!";
                break;
            case "business-related absence":
                subject = "Re:Re: Eintrag für eine unternehmensbezogene Abwesenheit : " + sender.getPersName() + " " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter);
                body = "Der Eintrag für eine unternehmensbezogene Abwesenheit  von " + sender.getPersName() + "von " + a.getStartTime().format(dayTimeFormatter) + " -- " + a.getEndTime().format(dayTimeFormatter) + " wurde vom System gelöscht!";
                break;
            default:
                break;
        }
        sendEmail(subject, body, null, approver);
    }

}
