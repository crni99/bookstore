package com.crni99.bookstore.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailServiceTest {
	
	private JavaMailSender mailSender = mock(JavaMailSender.class);
	private EmailService emailService = new EmailService(mailSender);

	@Test
	void sendEmailTest() {
		String to = "to@example.com";
		String subject = "Test subject";
		String message = "Test message body.";

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(to);
		mailMessage.setSubject(subject);
		mailMessage.setText(message);

		emailService.sendEmail(to, subject, message);

		verify(mailSender, times(1)).send(mailMessage);
	}
}
