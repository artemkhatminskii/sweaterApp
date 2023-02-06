package com.example.sweaterApp;

import com.example.sweaterApp.Controllers.MessageController;
import com.example.sweaterApp.Models.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("test_user")
public class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MessageController controller;

    @Test
    public void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/div/nav/div/div/span").string("test_user"));
    }

    @Test
    public void messageListTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/div/div[2]/div/*").nodeCount(4));
    }

    @Test
    public void messageFilterTest() throws Exception {
        this.mockMvc.perform(get("/main").param("filter", "test"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/div/div[2]/div/*").nodeCount(1));
    }

    @Test
    public void addMessageToListTest() throws Exception {
        Message message = new Message();
        message.setText("some text");
        message.setTag("some_tag");
        MockHttpServletRequestBuilder multipart = multipart("/new_message")
                .file("file", "123".getBytes())
                .requestAttr("message", message)
                .with(SecurityMockMvcRequestPostProcessors.csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/div/div[2]/div/*").nodeCount(5));

    }
}
