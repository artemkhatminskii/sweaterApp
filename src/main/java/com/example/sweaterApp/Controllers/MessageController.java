package com.example.sweaterApp.Controllers;

import com.example.sweaterApp.Models.Message;
import com.example.sweaterApp.Models.Usr;
import com.example.sweaterApp.Repository.MessageRepository;
import com.example.sweaterApp.Repository.UsrRepository;
import com.example.sweaterApp.Security.UsrDetails;
import com.example.sweaterApp.Services.UsrDetailsService;
import jakarta.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller

public class MessageController {

    @Value("${upload.path}")
    private String uploadPath;

    private final MessageRepository messageRepository;
    private final UsrRepository usrRepository;
    private final UsrDetailsService usrDetailsService;

    @Autowired
    public MessageController(MessageRepository messageRepository, UsrRepository usrRepository, UsrDetailsService usrDetailsService) {
        this.messageRepository = messageRepository;
        this.usrRepository = usrRepository;
        this.usrDetailsService = usrDetailsService;
    }


    @GetMapping("/")
    public String greeting(Model model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@AuthenticationPrincipal UsrDetails usr,
                       @RequestParam(value = "filter", required = false, defaultValue = "") String filter,
                       Model model,
                       @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC)Pageable pageable) {
        if (filter != null && !filter.isBlank())
            model.addAttribute("messages", messageRepository.findByTag(filter, pageable));
        else
        model.addAttribute("messages", messageRepository.findAll(pageable));
        Page<Message> page = messageRepository.findAll(pageable);
        page.getSize();
        model.addAttribute("filter", filter);
        model.addAttribute("usr", usr.getUsr());
        return "main";
    }


    @GetMapping("/user_messages")
    public String userMessages(Model model,
                               @AuthenticationPrincipal UsrDetails usr,
                               @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC)Pageable pageable) {

        Page<Message> messageSet = messageRepository.findByAuthor(Optional.ofNullable(usr.getUsr()), pageable);
        Usr getUsr = usrRepository.findByUsername(usr.getUsername());
        model.addAttribute("usr", usr.getUsr());
        model.addAttribute("messages", messageSet);
        model.addAttribute("isCurrentUsr", true);
        model.addAttribute("subscriptionsCount", getUsr.getSubscriptions().size());
        model.addAttribute("subscribersCount", getUsr.getSubscribers().size());
        model.addAttribute("isSubscriber", getUsr.getSubscribers().contains(usr.getUsr()));
        return "userMessages";
    }
    @GetMapping("/user_messages/{id}")
    public String userMessages(@PathVariable(required = false) Long id,
                               Model model,
                               @AuthenticationPrincipal UsrDetails usr,
                               @RequestParam(required = false) Message message,
                               @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC)Pageable pageable) {
        Page<Message> messageSet = messageRepository.findByAuthor(usrRepository.findById(id), pageable);
        Usr getUsr = usrDetailsService.getUsrById(id);
        Usr currentUsr = usrRepository.findByUsername(usr.getUsername());
        boolean isSubscriber = false;
        if (currentUsr.getSubscriptions().contains(getUsr)) isSubscriber=true;

        model.addAttribute("message", message);
        model.addAttribute("id", id);
        model.addAttribute("messages", messageSet);
        model.addAttribute("usr", getUsr);
        model.addAttribute("isCurrentUsr", usr.getUsr().getId().equals(id));
        model.addAttribute("subscriptionsCount", getUsr.getSubscriptions().size());
        model.addAttribute("subscribersCount", getUsr.getSubscribers().size());
        model.addAttribute("isSubscriber", isSubscriber);


        return "userMessages";
    }

    @PostMapping("/user_messages/{id}")
    public String updateMessage(
            @AuthenticationPrincipal UsrDetails currentUser,
            @PathVariable(required = false) Long id,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Message message
            ) throws IOException {
        if (message.getAuthor().getId().equals(currentUser.getUsr().getId())) {
            if (!StringUtils.isEmpty(text)) {
                message.setText(text);
            }

            if (!StringUtils.isEmpty(tag)) {
                message.setTag(tag);
            }

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

            messageRepository.save(message);
        }

        return "redirect:/user_messages/" + message.getAuthorId();
    }


    @GetMapping(value = "/img/{imageUrl}")
    public @ResponseBody byte[] image(@PathVariable String imageUrl) throws IOException {
        String url = "/home/artem/sweaterAppDir/" + imageUrl; //здесь указываете СВОЙ путь к папке с картинками
        InputStream in = new FileInputStream(url);
        return IOUtils.toByteArray(in);
    }

    @GetMapping("/new_message")
    public String addMessage(@ModelAttribute("message") Message message) {
        return "newMessage";
    }

    @PostMapping("/new_message")
    public String newMessage(
            Principal usr,
            @ModelAttribute("message") @Valid Message message,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file,
            Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
            return "newMessage";
        } else {
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
            model.addAttribute("message", null);
            message.setAuthor(usrRepository.findByUsername(usr.getName()));
            messageRepository.save(message);
        }
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
