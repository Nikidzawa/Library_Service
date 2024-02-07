package ru.nikidzawa.app.configs.bookkeepingSystem.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {
    @Bean
    public JavaMailSender javaMailSender () {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.mail.ru");
        javaMailSender.setUsername("nikidzawa@mail.ru");
        javaMailSender.setPassword("dmkMwHtK7mnisELviifv");
        javaMailSender.setPort(465);

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", "smtps");
        return javaMailSender;
    }
}
