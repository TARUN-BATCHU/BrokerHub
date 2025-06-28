package com.brokerhub.brokerageapp.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    public void verifyEmail(String email, Integer otp) throws MessagingException{

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify ACCOUNT");
        mimeMessageHelper.setText("""
        <div>
          <a href="http://localhost:8080/verify-account?email=%s&otp=%s" target="_blank">click link to verify your email</a>
        </div>
        """.formatted(email, otp), true);

        javaMailSender.send(mimeMessage);
    }

    public void sendOtpToEmail(String email, Integer otp) throws MessagingException{
        String emailContent = """
    <html>
    <head>
        <style>
            .container {
                font-family: Arial, sans-serif;
                background-color: #f7f7f7;
                padding: 20px;
                border-radius: 10px;
                width: 100%%;
                max-width: 600px;
                margin: auto;
            }
            .header {
                background-color: #003366;
                color: white;
                padding: 15px;
                border-radius: 10px 10px 0 0;
                text-align: center;
            }
            .body {
                background-color: #ffffff;
                padding: 30px;
                border-radius: 0 0 10px 10px;
                box-shadow: 0 0 5px rgba(0,0,0,0.1);
            }
            .otp {
                font-size: 24px;
                font-weight: bold;
                color: #003366;
                background-color: #e6f0ff;
                padding: 10px;
                text-align: center;
                border-radius: 5px;
                margin: 20px 0;
            }
            .footer {
                font-size: 12px;
                color: #555555;
                margin-top: 30px;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h2>Broker Hub - Password Reset</h2>
            </div>
            <div class="body">
                <p>Hi %s,</p>
                <p>We received a request to reset your password. Use the One-Time Password (OTP) below to proceed:</p>
                <div class="otp">%s</div>
                <p><strong>Important Notes:</strong></p>
                <ul>
                    <li>Do not share this OTP with anyone.</li>
                    <li>This OTP is valid for a limited time.</li>
                    <li>If you did not request this, please ignore this email or contact our support.</li>
                </ul>
                <p>Thank you,<br>Broker Hub Team</p>
            </div>
            <div class="footer">
                &copy; 2025 Broker Hub. All rights reserved.<br>
                This is an automated message – please do not reply.
            </div>
        </div>
    </body>
    </html>
    """;

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify OTP");
        emailContent = String.format(emailContent, email, otp);
        mimeMessageHelper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);
    }
}
