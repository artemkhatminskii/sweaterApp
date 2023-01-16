package com.example.sweaterApp.Controllers;

import com.example.sweaterApp.Models.Message;
import com.example.sweaterApp.Repository.MessageRepository;
import com.example.sweaterApp.Repository.UsrRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.UUID;

@Controller
public class MessageController {

    @Value("${upload.path}")
    private String uploadPath;

    private final MessageRepository messageRepository;
    private final UsrRepository usrRepository;

    @Autowired
    public MessageController(MessageRepository messageRepository, UsrRepository usrRepository) {
        this.messageRepository = messageRepository;
        this.usrRepository = usrRepository;
    }


    @GetMapping("/")
    public String greeting(Model model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(value = "tag", required = false, defaultValue = "") String tag,
                       Model model) {
        if (tag != null && !tag.isBlank())
            model.addAttribute("messages", messageRepository.findByTag(tag));
        else
        model.addAttribute("messages", messageRepository.findAll());
        model.addAttribute("tag", tag);
        return "main";
    }

    @GetMapping("/new_message")
    public String addMessage(@ModelAttribute("message") Message message) {
        return "newMessage";
    }

    @PostMapping("/new_message")
    public String newMessage(
            Principal usr,
            @ModelAttribute("message") Message message,
                             BindingResult bindingResult,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (!file.isEmpty() && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            message.setFilename(resultFilename);
        }

        if (bindingResult.hasErrors()) return "newMessage";
        message.setAuthor(usrRepository.findByUsername(usr.getName()));
        messageRepository.save(message);

        return "redirect:/main";
    }

//    @PostMapping("/filter")
//    public String filter(@RequestParam String tag, Model model) {
//        if (tag != null && !tag.isBlank())
//        model.addAttribute("messages", messageRepository.findByTag(tag));
//        else model.addAttribute("messages", messageRepository.findAll());
//        return "main";
//    }
}
