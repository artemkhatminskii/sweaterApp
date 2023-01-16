package com.example.sweaterApp.Services;

import com.example.sweaterApp.Models.Role;
import com.example.sweaterApp.Models.Usr;
import com.example.sweaterApp.Repository.UsrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final UsrRepository usrRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public RegistrationService(UsrRepository usrRepository, PasswordEncoder passwordEncoder) {
        this.usrRepository = usrRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(Usr usr) {
        usr.setPassword(passwordEncoder.encode(usr.getPassword()));
        usr.setRole(String.valueOf(Role.ROLE_USER));
        usr.setActive(true);
        usrRepository.save(usr);
    }
}
