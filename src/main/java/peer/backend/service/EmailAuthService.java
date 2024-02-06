package peer.backend.service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import peer.backend.dto.security.EmailMessage;
import peer.backend.dto.security.Message;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.UnauthorizedException;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private static final String EMAIL_REDIS_KEY_PREFIX = "email-auth:";

    private final JavaMailSender sender;
    private final RedisTemplate<String, String> redisTemplate;
    private final SecureRandom random = new SecureRandom();

    private String getAuthCode(String email) {
        int length = 10;
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String numericChars = "0123456789";
        String specialChars = "!@#$%^&*";
        String allChars = upperCaseChars + lowerCaseChars + numericChars + specialChars;

        StringBuilder sb = new StringBuilder(length);
        sb.append(upperCaseChars.charAt(this.random.nextInt(upperCaseChars.length())));
        sb.append(lowerCaseChars.charAt(this.random.nextInt(lowerCaseChars.length())));
        sb.append(numericChars.charAt(this.random.nextInt(numericChars.length())));
        sb.append(specialChars.charAt(this.random.nextInt(specialChars.length())));
        for (int i = 4; i < length; i++) {
            int randomIndex = this.random.nextInt(allChars.length());
            char randomChar = allChars.charAt(randomIndex);
            sb.append(randomChar);
        }
        String code = sb.toString();

        this.putRedisEmailCode(email, code);
        return code;
    }

    private void send(EmailMessage emailMessage) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        try {
            mailMessage.setTo(emailMessage.getTo());
            mailMessage.setSubject(emailMessage.getSubject());
            mailMessage.setText(emailMessage.getText());
            sender.send(mailMessage);
        } catch (Exception e) {
            throw new ForbiddenException("잘못된 접근 입니다.");
        }
    }

    public void sendAuthCode(String email, String text) {
        this.sendEmail(email, "Peer 인증 코드", String.format(text, getAuthCode(email)));
    }

    public void sendEmail(String email, String subject, String text) {
        Message message = new Message();
        EmailMessage emailMessage = new EmailMessage();
        try {
            emailMessage.setTo(email);
            emailMessage.setSubject("Peer 인증 코드");
            emailMessage.setText(text);
            this.send(emailMessage);
            message.setStatus(HttpStatus.OK);
        } catch (Exception e) {
            throw new ForbiddenException("Failed to send E-mail");
        }
    }

    public void emailCodeVerification(String email, String code) {
        String redisCode = this.redisTemplate.opsForValue().get(EMAIL_REDIS_KEY_PREFIX + email);
        if (redisCode == null) {
            throw new BadRequestException("잘못된 이메일입니다!");
        }
        if (!redisCode.equals(code)) {
            throw new UnauthorizedException("잘못된 인증 코드입니다!");
        }
        this.redisTemplate.delete(EMAIL_REDIS_KEY_PREFIX + email);
    }

    private void putRedisEmailCode(String email, String code) {
        this.redisTemplate.opsForValue()
            .set(EMAIL_REDIS_KEY_PREFIX + email, code, 5, TimeUnit.MINUTES);
    }
}
