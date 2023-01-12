package com.example.sweaterApp.Controllers;

import com.example.sweaterApp.Models.Role;
import com.example.sweaterApp.Models.Usr;
import com.example.sweaterApp.Repository.UsrRepository;
import com.example.sweaterApp.Services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UsrRepository usrRepository;
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(UsrRepository usrRepository, RegistrationService registrationService) {
        this.usrRepository = usrRepository;
        this.registrationService = registrationService;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUsr(Usr usr, Model model) {
        registrationService.register(usr);

        return "redirect:/login";
    }
}
