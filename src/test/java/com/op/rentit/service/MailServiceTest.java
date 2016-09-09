package com.op.rentit.service;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import static com.op.rentit.web.rest.TestsHelper.fakeUser;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@IntegrationTest
@Transactional
public class MailServiceTest {


    @Mock
    private JavaMailSenderImpl javaMailSender;

    @Inject
    @InjectMocks
    MailService mailService;

    @Before
    public void before(){
        initMocks(this);
    }

    @Test
    public void sendEmail() throws Exception {
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        mailService.sendEmail("alex@test.com","subject","hello!",false,false);
    }

    @Test
    public void sendActivationEmail() throws Exception {
        mailService.sendActivationEmail(fakeUser() ,"/");
    }

    @Test
    public void sendCreationEmail() throws Exception {
        mailService.sendCreationEmail(fakeUser() ,"/");
    }

    @Test
    public void sendPasswordResetMail() throws Exception {
        mailService.sendPasswordResetMail(fakeUser() ,"/");
    }

    @Test
    public void sendSocialRegistrationValidationEmail() throws Exception {
        mailService.sendSocialRegistrationValidationEmail(fakeUser() ,"/");
    }

}
