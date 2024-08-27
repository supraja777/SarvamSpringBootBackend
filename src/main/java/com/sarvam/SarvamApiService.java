package com.sarvam;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SarvamApiService {

    private static final String SARVAM_API_KEY = "b433415a-8d9c-4120-8354-ed576ed1ce75";

    @Autowired
    private WebClient.Builder webClientBuilder;

    public String translateText() {
        log.info("Calling Sarvam API");

        WebClient webClient = webClientBuilder.build();

        // Define the JSON body as a string
        String jsonBody = "{\n" +
                "  \"input\": \"Hi today is a wonderful day. How are you??\",\n" +
                "  \"source_language_code\": \"en-IN\",\n" +
                "  \"target_language_code\": \"hi-IN\",\n" +
                "  \"speaker_gender\": \"Male\",\n" +
                "  \"mode\": \"formal\",\n" +
                "  \"model\": \"mayura:v1\",\n" +
                "  \"enable_preprocessing\": true\n" +
                "}";

        Mono<String> response = webClient.post()
                .uri("https://api.sarvam.ai/translate")
                .header("API-Subscription-Key", SARVAM_API_KEY)  // Use the correct authorization header
                .header("Content-Type", "application/json")  // Set content type to application/json
                .bodyValue(jsonBody)  // Use bodyValue for JSON string
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new RuntimeException("4xx Client Error: " + body))
                        )
                )
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new RuntimeException("5xx Server Error: " + body))
                        )
                )
                .bodyToMono(String.class);

        try {
            String result = response.block();  // Blocking for simplicity; use async handling in production
            log.info("Completed calling Sarvam API. Response: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error calling Sarvam API", e);
        }
        return "null";
    }
}