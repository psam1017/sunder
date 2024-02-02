package psam.portfolio.sunder.english.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@RequiredArgsConstructor
public class MailUtils {

    private final JavaMailSender javaMailSender;

    // TODO: 2024-01-29 mail username, password 설정

    public boolean sendMail(String recipient, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            log.info("failed to send mail to {}", recipient, e);
            return false;
        }
        return true;
    }
}
