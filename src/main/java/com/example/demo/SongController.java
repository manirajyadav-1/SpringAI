package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SongController {

    private final ChatClient chatClient;

    public SongController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/songs")
    public List<String> getSongs(@RequestParam(value = "artist", defaultValue = "Khesari Lal Yadav") String artist) {
        String message = """
                List top 10 songs of {artist}.
                {format}
                """;

        ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());
        PromptTemplate promptTemplate = new PromptTemplate(message);

        Prompt prompt = promptTemplate.create(Map.of(
                "artist", artist,
                "format", listOutputConverter.getFormat()
        ));
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        return listOutputConverter.convert(content);
    }
}
