package com.example.sweaterApp.Services;

import com.example.sweaterApp.Models.Role;
import com.example.sweaterApp.Models.Usr;
import com.example.sweaterApp.Repository.UsrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class RegistrationService {
    private final UsrRepository usrRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;
    @Autowired
    public RegistrationService(UsrRepository usrRepository, PasswordEncoder passwordEncoder, MailSenderService mailSenderService) {
        this.usrRepository = usrRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
    }

    @Transactional
    public boolean register(Usr usr) {
        Usr usrFromDb = usrRepository.findByUsername(usr.getUsername());
        if (usrFromDb != null) {
            return false;
        }
        usr.setPassword(passwordEncoder.encode(usr.getPassword()));
        usr.setRole(String.valueOf(Role.ROLE_USER));
        usr.setActive(true);
        usr.setActivationCode(UUID.randomUUID().toString());

        usrRepository.save(usr);

        if (!StringUtils.isEmpty(usr.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Sweater. Please, visit next link for activation:" +
                            " http://localhost:8080/activate/%s",
                    usr.getUsername(),
                    usr.getActivationCode()
            );
            mailSenderService.send(usr.getEmail(), "Activation code", message);
        }
        return true;
    }

    public boolean activateUser(String code) {
        Usr usr = usrRepository.findByActivationCode(code);
        if (usr == null) {
            return false;
        }
        usr.setActivationCode(null);
        usrRepository.save(usr);
        return true;
    }
}
