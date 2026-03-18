package vn.baymax.fjob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.baymax.fjob.dto.request.ChatRequest;
import vn.baymax.fjob.dto.response.ChatResponse;
import vn.baymax.fjob.service.ChatbotService;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatbotService chatbotService;

    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {

        String answer = chatbotService.chat(request.question());

        return ResponseEntity.ok(new ChatResponse(answer));
    }
}