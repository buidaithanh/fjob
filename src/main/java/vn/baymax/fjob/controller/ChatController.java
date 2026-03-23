package vn.baymax.fjob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import vn.baymax.fjob.dto.request.ChatRequest;
import vn.baymax.fjob.dto.response.ChatResponse;
import vn.baymax.fjob.service.ChatbotService;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Chat & Chatbot", description = "Chatbot AI endpoints for job-related inquiries")

public class ChatController {

    private final ChatbotService chatbotService;

    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Send message to chatbot", description = "Send a question or message to the AI chatbot for job-related inquiries. "
            +
            "The chatbot can answer questions about jobs, companies, application process, " +
            "career advice, and more.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chatbot response received successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request - question is required", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"code\": 400, \"message\": \"Question cannot be empty\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error - chatbot service error", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"code\": 500, \"message\": \"Error processing your question\", \"data\": null}")))
    })
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {

        String answer = chatbotService.chat(request.question());

        return ResponseEntity.ok(new ChatResponse(answer));
    }
}