package vn.baymax.fjob.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.baymax.fjob.service.JobToolService;

@Configuration
public class AiConfig {

    @Bean
    ToolCallbackProvider jobTools(JobToolService jobToolService) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(jobToolService)
                .build();
    }

    @Bean
    ChatClient chatClient(ChatModel chatModel,
            ToolCallbackProvider toolCallbackProvider) {

        return ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();
    }
}