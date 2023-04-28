package com.example.sweaterApp.Controllers;

import com.example.sweaterApp.Models.Usr;
import com.example.sweaterApp.Security.UsrDetails;
import com.example.sweaterApp.Services.RegistrationService;
import com.example.sweaterApp.Services.UsrDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final RegistrationService registrationService;
    private final UsrDetailsService usrDetailsService;
    @Autowired
    public ProfileController(RegistrationService registrationService, UsrDetailsService usrDetailsService) {
        this.registrationService = registrationService;
        this.usrDetailsService = usrDetailsService;
    }

    @GetMapping
    public String getProfile(Model model, @AuthenticationPrincipal UsrDetails usr) {
        model.addAttribute("username", usr.getUsername());
        model.addAttribute("email", usr.getUsr().getEmail());
        return "profile";
    }

    @PostMapping
    public String profileSave(
            @AuthenticationPrincipal UsrDetails usr,
            @RequestParam String password,
            @RequestParam String password2,
            @RequestParam String email,
            Model model
    ) {
        if (password != null && !password.equals(password2)) {
            model.addAttribute("passwordError", "Пароли не совпадают!");
            return "profile";
        }

        registrationService.updateProfile(usr.getUsr(), password, email);

        return "redirect:/profile";
    }

    @GetMapping("subscribe/{id}")
    public String subscribe(
            @AuthenticationPrincipal UsrDetails currentUsr,
            @PathVariable Long id
    ) {
        Usr usr = usrDetailsService.getUsrById(id);
        usrDetailsService.subscribe(currentUsr.getUsr(), usr);

        return "redirect:/user_messages/" + usr.getId();
    }

    @GetMapping("unsubscribe/{id}")
    public String unsubscribe(
            @AuthenticationPrincipal UsrDetails currentUsr,
            @PathVariable Long id
    ) {
        Usr usr = usrDetailsService.getUsrById(id);
        usrDetailsService.unsubscribe(currentUsr.getUsr(), usr);

        return "redirect:/user_messages/" + usr.getId();
    }

    @GetMapping("{type}/{id}")
    public String userList(
            Model model,
            @PathVariable String type,
            @PathVariable Long id
    ) {
        Usr usr = usrDetailsService.getUsrById(id);
        model.addAttribute("usrChannel", usr);
        model.addAttribute("type", type);

        if ("subscriptions".equals(type)) {
            model.addAttribute("users", usr.getSubscriptions());
        } else {
            model.addAttribute("users", usr.getSubscribers());
        }

        return "subscriptions";
    }
}
