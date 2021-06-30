package raven.iss.data.mail;

import java.util.List;

public interface MailSender {
    void sendSignUpNotification(String to);
    void sendRegisterAuthorNotification(String to, String confName);
    void sendRegisterListenerNotification(String to, String confName);
    void sendChairNotification(String to, String confName);
    void sendPCMemberNotification(String to, String confName);
    void sendAttendSessionNotification(String to, String confName, String sessionName);
    void sendSessionChairNotification(String to, String confName, String sessionName);
    void sendSessionSpeakerNotification(String to, String confName, String sessionName);
    void sendPaperResultNotification(List<String> to, String confName, String paperName, String result);
    void sendAssignedPaperNotification(String to, String confName, String paperName);
    void sendPaperStatusAfterCalculation(String to, String confName, String message);
    void sendSessionSpeakerRemovedNotification(String to, String confName, String sessionName);
}
