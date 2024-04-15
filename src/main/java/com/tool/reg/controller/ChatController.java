package com.tool.reg.controller;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class ChatController {
    private OpenAiChatClient chatClient;

    public ChatController(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    String test(@RequestBody String message){
        String call = chatClient.call(message);
        System.out.println(call);
        return call;
    }
}
