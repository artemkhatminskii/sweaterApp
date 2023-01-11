package com.example.sweaterApp.Controllers;

import com.example.sweaterApp.Models.Message;
import com.example.sweaterApp.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class greetingController {

    private final MessageRepository messageRepository;

    @Autowired
    public greetingController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false,
            defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/")
    public String main(Model model) {
        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }

    @GetMapping("/new_message")
    public String addMessage(@ModelAttribute("message") Message message) {
        return "newMessage";
    }
    @PostMapping("/")
    public String newMessage(@ModelAttribute("message") Message message,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) return "newMessage";

        messageRepository.save(message);

        return "redirect:/";
    }
}
