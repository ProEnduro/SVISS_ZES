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

    private static final DateTimeFormatter DAY_FORMATTER;
    private static final DateTimeFormatter DAY_TIME_FORMATTER;

    private static final String CURRENTZESLINK = "http://zes.sviss.at:8080/SVISS_ZES/";
    private static final String CURRENTVPNZESLINK = "http://192.168.14.106:8080/SVISS_ZES/";

    private static final String SERVER_EMAILADDRESS = "zes@sviss.co.at";

    private static final String CURRENT_CC_EMAILADDRESS = "einsatzleitung@sviss.at";

    static {
        DAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
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

    private static void sendEmailWithCC(String subject, String body, User from, List<User> to, String ccEmailAddress) {
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

                message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccEmailAddress));

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
        switch (a.getAbsenceType()) {
            case MEDICAL_LEAVE:
                subject = "Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = sender.getPersName() + " hat einen Krankenstandsantrag für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " gestellt.";
                if (!a.getReason().isEmpty()) {
                    body += "\n Grund: " + a.getReason();
                }

                break;
            case HOLIDAY:
                subject = "Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(DAY_FORMATTER) + " -- " + a.getEndTime().format(DAY_FORMATTER);
                body = sender.getPersName() + " hat einen Urlaubsantrag für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " gestellt.";
                if (!a.getReason().isEmpty()) {
                    body += "\n Grund: " + a.getReason();
                }
                break;
            case TIME_COMPENSATION:
                subject = "Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = sender.getPersName() + " hat einen Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " gestellt.";
                if (!a.getReason().isEmpty()) {
                    body += "\n Grund: " + a.getReason();
                }
                break;
            case BUSINESSRELATED_ABSENCE:
                subject = "Eintrag für eine unternehmensbezogene Abwesenheit: " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = sender.getPersName() + " hat einen Eintrag für eine unternehmensbezogene Abwesenheit für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " gemacht.";
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
        switch (a.getAbsenceType()) {
            case MEDICAL_LEAVE:
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                bodyApprover = "Der Krankenstandsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " wurde von " + approver.getPersName() + " angenommen.";
                bodySender = "Ihr Krankenstandsantrag für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " wurde von " + approver.getPersName() + " angenommen.";
                break;
            case HOLIDAY:
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(DAY_FORMATTER) + " -- " + a.getEndTime().format(DAY_FORMATTER);
                bodyApprover = "Der Urlaubsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " wurde von " + approver.getPersName() + " angenommen.";
                bodySender = "Ihr Urlaubsantrag für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " wurde von " + approver.getPersName() + " angenommen.";
                break;
            case TIME_COMPENSATION:
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                bodyApprover = "Der Antrag auf Zeitausgleich von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde von " + approver.getPersName() + " angenommen.";
                bodySender = "Ihr Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde von " + approver.getPersName() + " angenommen.";
                break;
            case BUSINESSRELATED_ABSENCE:
                subject = "Re: Eintrag für eine unternehmensbezogene Abwesenheit: " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                bodyApprover = "Der Eintrag für eine unternehmensbezogene Abwesenheit von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde von " + approver.getPersName() + " bestätigt.";
                bodySender = "Ihr Eintrag für eine unternehmensbezogene Abwesenheit für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde von " + approver.getPersName() + " bestätigt.";
                break;
            default:
                break;
        }

        List<User> senderL = new ArrayList<>();
        senderL.add(sender);

        sendEmailWithCC(subject, bodyApprover, null, otherApprover, CURRENT_CC_EMAILADDRESS);
        sendEmail(subject, bodySender, null, senderL);
    }

    public static void sendAbsenceDeletedByApprover(Absence a, User approver, List<User> otherApprover) {
        String subject = "";
        String bodyApprover = "";
        String bodySender = "";
        User sender = a.getUser();
        switch (a.getAbsenceType()) {
            case MEDICAL_LEAVE:
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER) + " zurückgewiesen";
                bodyApprover = "Der Krankenstandsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                bodySender = "Ihr Krankenstandsantrag für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                break;
            case HOLIDAY:
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(DAY_FORMATTER) + " -- " + a.getEndTime().format(DAY_FORMATTER);
                bodyApprover = "Der Urlaubsantrag von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                bodySender = "Ihr Urlaubsantrag für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                break;
            case TIME_COMPENSATION:
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                bodyApprover = "Der Antrag auf Zeitausgleich von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                bodySender = "Ihr Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde von " + approver.getPersName() + " zurückgewiesen.";
                break;
            case BUSINESSRELATED_ABSENCE:
                subject = "Re: Eintrag für eine unternehmensbezogene Abwesenheit: " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                bodyApprover = "Der Eintrag für eine unternehmensbezogene Abwesenheit von " + sender.getPersName() + " für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde von " + approver.getPersName() + " gelöscht.";
                bodySender = "Ihr Eintrag für eine unternehmensbezogene Abwesenheit für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde von " + approver.getPersName() + " gelöscht.";
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
        switch (a.getAbsenceType()) {
            case MEDICAL_LEAVE:
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = sender.getPersName() + " hat den Krankenstand von" + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER) + " gelöscht";
                break;
            case HOLIDAY:
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(DAY_FORMATTER) + " -- " + a.getEndTime().format(DAY_FORMATTER);
                body = sender.getPersName() + " hat den Urlaubsantrag für den Zeitraum von " + a.getStartTime().format(DAY_FORMATTER) + " bis " + a.getEndTime().format(DAY_FORMATTER) + " gelöscht.";
                break;
            case TIME_COMPENSATION:
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = sender.getPersName() + " hat den Antrag auf Zeitausgleich für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " gelöscht.";
                break;
            case BUSINESSRELATED_ABSENCE:
                subject = "Re: Eintrag für eine unternehmensbezogene Abwesenheit : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = sender.getPersName() + " hat den Eintrag für eine unternehmensbezogene Abwesenheit für den Zeitraum von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " bis " + a.getEndTime().format(DAY_TIME_FORMATTER) + " gelöscht.";
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
        body = "Das neue Passwort für den User " + user.getUsername() + " lautet: " + newPassword
                + "<br>Der Link für das ZES ist derzeit: " + CURRENTZESLINK
                + "<br>Der Link für VPN-User ist derzeit: " + CURRENTVPNZESLINK;

        List<User> userL = new ArrayList<>();
        userL.add(user);

        sendEmail(subject, body, null, userL);
    }

    public static void sendReminderAcknowledgementEmail(Absence a, List<User> approver) {
        String subject = "";
        String body = "";
        User sender = a.getUser();
        switch (a.getAbsenceType()) {
            case MEDICAL_LEAVE:
                subject = "Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = "Der Krankenstand von " + sender.getPersName() + "von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER) + " muss noch freigegeben werden!";
                break;
            case HOLIDAY:
                subject = "Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(DAY_FORMATTER) + " -- " + a.getEndTime().format(DAY_FORMATTER);
                body = "Der Urlaub von " + sender.getPersName() + "von " + a.getStartTime().format(DAY_FORMATTER) + " -- " + a.getEndTime().format(DAY_FORMATTER) + " muss noch freigegeben werden!";
                break;
            case TIME_COMPENSATION:
                subject = "Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = "Der Zeitausgleich von " + sender.getPersName() + "von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER) + " muss noch freigegeben werden!";
                break;
            case BUSINESSRELATED_ABSENCE:
                subject = "Re: Eintrag für eine unternehmensbezogene Abwesenheit : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = "Der Eintrag für eine unternehmensbezogene Abwesenheit  von " + sender.getPersName() + "von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER) + " muss noch freigegeben werden!";
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
        switch (a.getAbsenceType()) {
            case MEDICAL_LEAVE:
                subject = "Re:Re: Krankenstand : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = "Der Krankenstand von " + sender.getPersName() + "von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde vom System gelöscht!";
                break;
            case HOLIDAY:
                subject = "Re:Re: Urlaubsantrag : " + sender.getPersName() + " " + a.getStartTime().format(DAY_FORMATTER) + " -- " + a.getEndTime().format(DAY_FORMATTER);
                body = "Der Urlaub von " + sender.getPersName() + "von " + a.getStartTime().format(DAY_FORMATTER) + " -- " + a.getEndTime().format(DAY_FORMATTER) + " wurde vom System gelöscht!";
                break;
            case TIME_COMPENSATION:
                subject = "Re:Re: Antrag auf Zeitausgleich : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = "Der Zeitausgleich von " + sender.getPersName() + "von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde vom System gelöscht!";
                break;
            case BUSINESSRELATED_ABSENCE:
                subject = "Re:Re: Eintrag für eine unternehmensbezogene Abwesenheit : " + sender.getPersName() + " " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER);
                body = "Der Eintrag für eine unternehmensbezogene Abwesenheit  von " + sender.getPersName() + "von " + a.getStartTime().format(DAY_TIME_FORMATTER) + " -- " + a.getEndTime().format(DAY_TIME_FORMATTER) + " wurde vom System gelöscht!";
                break;
            default:
                break;
        }
        sendEmail(subject, body, null, approver);
    }

    public static void sendIstZeitReminderEmail(User u) {
        String subject;
        String body;

        subject = "ZES Ist-Zeiten Erinnerung";
        body = "Bitte tragen Sie Ihre Zeiten für dieses Monat korrekt im ZES ein!"
                + "<br>Es ist nur möglich Ist-Zeiten im aktuellen Monat einzugeben!"
                + "<br>"
                + "<br>Der Link für das ZES ist derzeit: " + CURRENTZESLINK
                + "<br>Der Link für VPN-User ist derzeit: " + CURRENTVPNZESLINK;

        List<User> userL = new ArrayList<>();
        userL.add(u);

        sendEmail(subject, body, null, userL);
    }

}
