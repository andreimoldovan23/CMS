package raven.iss.data.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Profile("mail")
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {

    private static final String from = "noreply@cms.com";

    private static final String signUpSubject = "Account created on the CMS platform";
    private static final String registerAsAuthorSubject = "Becoming an author on our platform";
    private static final String registerAsListenerSubject = "Becoming a listener on our platform";
    private static final String attendingSessionSubject = "Attending sessions";
    private static final String becomingAChairSubject = "You have been chosen as chair";
    private static final String becomingAMemberSubject = "You have been chosen as PCMember";
    private static final String becomingSessionChairSubject = "You have been chosen as session chair";
    private static final String becomingSessionSpeakerSubject = "You have been chosen as speaker";
    private static final String removedSessionSpeakerSubject = "You are no longer a session speaker";
    private static final String paperResultSubject = "Your paper is finished being reviewed";
    private static final String assignedPaperSubject = "You have been assigned to review a paper";
    private static final String papersStatusSubject = "Results after review analysis for papers";

    private static final String signUpText = "You have successfully created an account " +
            "with us. \n" +
            "Now you can jump right in and start checking out what we have to offer!";

    private static final String registerAuthorTemplate = "You are now an author at: ";
    private static final String registerListenerTemplate = "You are now a listener at: ";
    private static final String chairTemplate = "You are now a chair at: ";
    private static final String memberTemplate = "You are now a PCMember at: ";
    private static final String sessionChairTemplate = "You are now a session chair at: ";
    private static final String sessionSpeakerTemplate = "You are now a session speaker at: ";
    private static final String removedSessionSpeakerTemplate = "You are no longer a session speaker at: ";
    private static final String attendSessionTemplate = "You are now attending: ";
    private static final List<String> paperResultTemplates = List.of("At conference ", ",  your paper ",
            " has been ");
    private static final String assignedPaperTemplate = "You have been assigned to review ";

    private final JavaMailSender mailSender;

    private SimpleMailMessage getMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    public void sendSignUpNotification(String to) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, signUpSubject, signUpText)));
    }

    public void sendRegisterAuthorNotification(String to, String confName) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, registerAsAuthorSubject,
                registerAuthorTemplate + confName)));
    }

    public void sendRegisterListenerNotification(String to, String confName) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, registerAsListenerSubject,
                registerListenerTemplate + confName)));
    }

    public void sendChairNotification(String to, String confName) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, becomingAChairSubject,
                chairTemplate + confName)));
    }

    public void sendPCMemberNotification(String to, String confName) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, becomingAMemberSubject,
                memberTemplate + confName)));
    }

    public void sendAttendSessionNotification(String to, String confName, String sessionName) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, attendingSessionSubject,
                attendSessionTemplate + "Conference " + confName + ", Session " + sessionName)));
    }

    public void sendSessionChairNotification(String to, String confName, String sessionName) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, becomingSessionChairSubject,
                sessionChairTemplate + "Conference " + confName + ", Session " + sessionName)));
    }

    public void sendSessionSpeakerNotification(String to, String confName, String sessionName) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, becomingSessionSpeakerSubject,
                 sessionSpeakerTemplate + "Conference " + confName + ", Session " + sessionName)));
    }

    public void sendPaperResultNotification(List<String> to, String confName, String paperName, String result) {
        CompletableFuture.runAsync(() -> to.forEach(receiver ->
                mailSender.send(getMessage(receiver, paperResultSubject,
                paperResultTemplates.get(0) + confName +
                     paperResultTemplates.get(1) + paperName +
                     paperResultTemplates.get(2) + result))));
    }

    public void sendAssignedPaperNotification(String to, String confName, String paperName) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, assignedPaperSubject,
                assignedPaperTemplate + paperName + ", at " + confName)));
    }

    public void sendPaperStatusAfterCalculation(String to, String confName, String message) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, papersStatusSubject,
                message + '\n' + confName)));
    }

    public void sendSessionSpeakerRemovedNotification(String to, String confName, String sessionName) {
        CompletableFuture.runAsync(() -> mailSender.send(getMessage(to, removedSessionSpeakerSubject,
                removedSessionSpeakerTemplate + "Conference " + confName + ", Session " + sessionName)));
    }

}
