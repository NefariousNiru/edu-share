package com.nefarious.edu_share.shared.interfaces;

import com.nefarious.edu_share.shared.exceptions.BusinessException;
import reactor.core.publisher.Mono;

/** Send e-mails */
public interface EmailService {
    /**
     * Send a one-time password (OTP) to the given address.
     *
     * @param to      recipient e-mail address
     * @param otpCode the 10-char alphanumeric code
     * @throws BusinessException if there is an error while sending the email.
     */
    Mono<Void> sendOtpEmail(String to, String otpCode) throws BusinessException;
}
