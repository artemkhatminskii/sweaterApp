package com.example.sweaterApp.Controllers;

import com.example.sweaterApp.Models.Message;
import com.example.sweaterApp.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class MessageController {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    @GetMapping("/")
    public String greeting(@RequestParam(name = "name", required = false,
            defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "main";
    }

    @GetMapping("/new_message")
    public String addMessage(@ModelAttribute("message") Message message) {
        return "newMessage";
    }

    @PostMapping("/new_message")
    public String newMessage(@ModelAttribute("message") Message message,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) return "newMessage";

        messageRepository.save(message);

        return "redirect:/main";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam String tag, Model model) {
        if (tag != null && !tag.isBlank())
        model.addAttribute("messages", messageRepository.findByTag(tag));
        else model.addAttribute("messages", messageRepository.findAll());
        return "main";
    }
}
