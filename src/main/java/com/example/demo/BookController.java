package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final ChatClient chatClient;

    public BookController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/author/{author}")
    public Map<String, Object> getAuthorsSocialLinks(@PathVariable String author) {
        String promptMessage = """
            Generate a list of social media or professional links for the author {author}. make author name as key.
            Return the response in the following format: {format}
        """;

        MapOutputConverter mapOutputConverter = new MapOutputConverter();
        String format = mapOutputConverter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage);
        Prompt prompt = promptTemplate.create(Map.of(
                "author", author,
                "format", format
        ));

        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

        return mapOutputConverter.convert(chatResponse.getResult().getOutput().getText());
    }
}
