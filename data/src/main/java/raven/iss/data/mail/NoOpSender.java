package raven.iss.data.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@Slf4j
public class NoOpSender implements MailSender {
    @Override
    public void sendSignUpNotification(String to) {
        log.info("sending sign up notification to {}", to);
    }

    @Override
    public void sendRegisterAuthorNotification(String to, String confName) {
        log.info("sending author registration notification to {} about {}", to, confName);
    }

    @Override
    public void sendRegisterListenerNotification(String to, String confName) {
        log.info("sending listener registration notification to {} about {}", to, confName);
    }

    @Override
    public void sendChairNotification(String to, String confName) {
        log.info("sending chair election notification to {} about {}", to, confName);
    }

    @Override
    public void sendPCMemberNotification(String to, String confName) {
        log.info("sending pcmember election notification to {} about {}", to, confName);
    }

    @Override
    public void sendAttendSessionNotification(String to, String confName, String sessionName) {
        log.info("sending session attendance notification to {} about {}, {}", to, confName, sessionName);
    }

    @Override
    public void sendSessionChairNotification(String to, String confName, String sessionName) {
        log.info("sending session chair notification to {} about {}, {}", to, confName, sessionName);
    }

    @Override
    public void sendSessionSpeakerNotification(String to, String confName, String sessionName) {
        log.info("sending session speaker notification to {} about {}, {}", to, confName, sessionName);
    }

    @Override
    public void sendPaperResultNotification(List<String> to, String confName, String paperName, String result) {
        to.forEach(receiver ->
                log.info("sending paper result notification to {} about {}, result {}",
                receiver, paperName, result));
    }

    @Override
    public void sendAssignedPaperNotification(String to, String confName, String paperName) {
        log.info("sending assigned paper notification to {} about {}, {}", to, paperName, confName);
    }

    @Override
    public void sendPaperStatusAfterCalculation(String to, String confName, String message) {
        log.info("sending papers status after review analysis to {}, from {}, message: {}", to, confName, message);
    }

    @Override
    public void sendSessionSpeakerRemovedNotification(String to, String confName, String sessionName) {
        log.info("sending session speaker removed notification to {}, from conf {}, session {}",
                to, confName, sessionName);
    }
}
