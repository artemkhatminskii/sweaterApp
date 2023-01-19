package com.example.sweaterApp.Controllers;

import com.example.sweaterApp.Models.Usr;
import com.example.sweaterApp.Repository.UsrRepository;
import com.example.sweaterApp.Services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

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
    public String addUsr(@Valid Usr usr, BindingResult bindingResult, Model model) {
        if (usr.getPassword() != null && !usr.getPassword().equals(usr.getPassword2())) {
            model.addAttribute("passwordError", "Пароли не совпадают!");
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errors);
            return "registration";
        }
        if (!registrationService.register(usr)) {
            model.addAttribute("existError", "User exist!");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = registrationService.activateUser(code);

        if (isActivated) {
            model.addAttribute("message", "User successfully activated!");
        } else {
            model.addAttribute("message", "Activation code is not found");
        }
        return "login";
    }
}
