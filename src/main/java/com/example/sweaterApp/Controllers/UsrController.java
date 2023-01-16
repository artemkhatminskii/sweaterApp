package com.example.sweaterApp.Controllers;

import com.example.sweaterApp.Models.Role;
import com.example.sweaterApp.Models.Usr;
import com.example.sweaterApp.Repository.UsrRepository;
import com.example.sweaterApp.Services.UsrDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UsrController {
    private final UsrRepository usrRepository;
    private final UsrDetailsService usrDetailsService;
    @Autowired
    public UsrController(UsrRepository usrRepository, UsrDetailsService usrDetailsService) {
        this.usrRepository = usrRepository;
        this.usrDetailsService = usrDetailsService;
    }

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", usrRepository.findAll());
        return "userList";
    }

    @GetMapping("/{usr_id}")
    public String userEditForm(@PathVariable("usr_id") Long usr_id, Model model) {
        model.addAttribute("usr", usrDetailsService.findOne(usr_id));
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PostMapping("/{usr_id}")
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("id") Usr usr
            ) {
        usr.setUsername(username);
        usr.setRole(form.get("role"));
        usrRepository.save(usr);
        return "redirect:/user";
    }
}
