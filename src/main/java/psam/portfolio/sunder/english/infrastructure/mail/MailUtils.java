package psam.portfolio.sunder.english.infrastructure.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Slf4j
@RequiredArgsConstructor
public class MailUtils {

    private final JavaMailSender javaMailSender;
    private final String fromEmail;

    // TODO: 2024-01-29 mail username, password 설정

    public boolean sendMail(String recipient, String subject, String text) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(recipient);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
            javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            log.info("failed to send mail to {}", recipient, e);
            return false;
        }
        return true;
    }
}
