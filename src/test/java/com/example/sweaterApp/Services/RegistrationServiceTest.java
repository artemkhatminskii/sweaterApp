package com.example.sweaterApp.Services;

import com.example.sweaterApp.Models.Role;
import com.example.sweaterApp.Models.Usr;
import com.example.sweaterApp.Repository.UsrRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @MockBean
    private UsrRepository usrRepository;

    @MockBean
    private MailSenderService mailSenderService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void register() {
        Usr usr = new Usr();
        usr.setEmail("some@mail.ru");
        boolean isUsrCreated = registrationService.register(usr);

        Assert.assertTrue(isUsrCreated);
        Assert.assertNotNull(usr.getActivationCode());
        Assert.assertTrue(usr.getRole().matches(String.valueOf(Role.ROLE_USER)));

        Mockito.verify(usrRepository, Mockito.times(1)).save(usr);
        Mockito.verify(mailSenderService, Mockito.times(1))
                .send(ArgumentMatchers.eq(usr.getEmail()),
                        ArgumentMatchers.eq("Activation code"),
                        ArgumentMatchers.contains("Welcome to Sweater"));
    }

    @Test
    public void addUserFailTest() {
        Usr usr = new Usr();

        usr.setUsername("John");

        doReturn(new Usr()).when(usrRepository).findByUsername("John");

        boolean isUserCreated = registrationService.register(usr);

        Assert.assertFalse(isUserCreated);

        Mockito.verify(usrRepository, Mockito.times(0)).save(ArgumentMatchers.any(Usr.class));
        Mockito.verify(mailSenderService, Mockito.times(0))
                .send(ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());
    }

    @Test
    void activateUser() {
        Usr usr = new Usr();
        usr.setActivationCode("bingo");
        doReturn(usr)
                .when(usrRepository)
                .findByActivationCode("activate");

        boolean isUsrActivated = registrationService.activateUser("activate");

        Assert.assertTrue(isUsrActivated);
        Assert.assertNull(usr.getActivationCode());
        Mockito.verify(usrRepository, Mockito.times(1)).save(usr);

    }

    @Test
    public void activateUserFailTest() {
        boolean isUsrActivated = registrationService.activateUser("activate me");

        Assert.assertFalse(isUsrActivated);
        Mockito.verify(usrRepository, Mockito.times(0)).save(ArgumentMatchers.any(Usr.class));
    }
}