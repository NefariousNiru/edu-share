package com.nefarious.edu_share.shared.service;

import com.nefarious.edu_share.auth.util.enums.AuthError;
import com.nefarious.edu_share.shared.exceptions.BusinessException;
import com.nefarious.edu_share.shared.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpService implements EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public Mono<Void> sendOtpEmail(String to, String otpCode) throws BusinessException {
        return Mono.fromRunnable(() -> {
            MimeMessage message = mailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
                helper.setFrom(from);
                helper.setTo(to);
                helper.setSubject("Your Verification Code");
                String html = """
                <p>Hello,</p>
                <p>Your one-time code is <b>%s</b></p>
                <p>This code expires in 10 minutes.</p>
                """.formatted(otpCode);
                helper.setText(html, true);
                mailSender.send(message);
            } catch (MessagingException ex) {
                log.error("Failed to send OTP to {}: {}", to, ex.getMessage());
                throw new BusinessException(AuthError.FAILED_TO_SEND_OTP);
            }
        })
        .subscribeOn(Schedulers.boundedElastic()) // run on a thread‐pool for blocking tasks
        .then();
    }
}
