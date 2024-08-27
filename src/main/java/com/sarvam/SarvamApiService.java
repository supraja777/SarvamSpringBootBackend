package com.sarvam;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

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

//    public String getTranscriptFromAudio() {
//        log.info("Calling Sarvam API");
//
//        WebClient webClient = webClientBuilder.build();
//
//        // Define the JSON body as a string
////        String jsonBody =  "-----011000010111000001101001\r\n" +
////                "Content-Disposition: form-data; name=\"language_code\"\r\n\r\n" +
////                "hi-IN\r\n" +
////                "-----011000010111000001101001\r\n" +
////                "Content-Disposition: form-data; name=\"model\"\r\n\r\n" +
////                "saarika:v1\r\n" +
////                "-----011000010111000001101001--\r\n\r\n";
//
//
//        String jsonBody = """
//                {
//                   "file" :"01001010010",
//                  "language_code": "hi-IN",
//                  "model": "saarika:v1",
//                  "target_language_code": "hi-IN",
//                }""";
//
//        Mono<String> response = webClient.post()
//                .uri("https://api.sarvam.ai/speech-to-text")
//                .header("API-Subscription-Key", SARVAM_API_KEY)
//                .header("Content-Type", "application/json")
//                .bodyValue(jsonBody)  // Use bodyValue for JSON string
//                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
//                        clientResponse.bodyToMono(String.class).flatMap(body ->
//                                Mono.error(new RuntimeException("4xx Client Error: " + body))
//                        )
//                )
//                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
//                        clientResponse.bodyToMono(String.class).flatMap(body ->
//                                Mono.error(new RuntimeException("5xx Server Error: " + body))
//                        )
//                )
//                .bodyToMono(String.class);
//
//        try {
//            String result = response.block();  // Blocking for simplicity; use async handling in production
//            log.info("Completed calling Sarvam API. Response: {}", result);
//            return result;
//        } catch (Exception e) {
//            log.error("Error calling Sarvam API", e);
//        }
//        return "null";
//    }


    public String getTranscriptFromAudio() {
        log.info("Calling Sarvam API");

        WebClient webClient = webClientBuilder.build();

        // Create a MultiValueMap to hold the form data and file
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("language_code", "hi-IN");  // Required language code
        formData.add("model", "saarika:v1");      // Model parameter

        // Add the file part
        // Update with the path to your audio file
        File file = new File("C:/Users/dell/Downloads/Bulleya Full Video.mp3"); // Use forward slashes or double backslashes
        FileSystemResource fileResource = new FileSystemResource(file);
        formData.add("file", fileResource);  // Field name should match API's requirement

        Mono<String> response = webClient.post()
                .uri("https://api.sarvam.ai/speech-to-text")
                .header("API-Subscription-Key", SARVAM_API_KEY)  // Use the correct authorization header
                .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)  // Set content type to multipart/form-data
                .body(BodyInserters.fromMultipartData(formData))  // Add multipart data
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new RuntimeException("4xx Client Error: " + body))
                        )
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
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
            log.info("Error calling Sarvam API : {}", e.getMessage());
        }
        return "null";
    }

    public String getTranscriptFromAudioFile(MultipartFile multipartFile) {
        log.info("Calling Sarvam API using file upload");

        WebClient webClient = webClientBuilder.build();

        // Create a MultiValueMap to hold the form data and file
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("language_code", "hi-IN");  // Required language code
        formData.add("model", "saarika:v1");      // Model parameter

        FileSystemResource fileResource;

        // Add the file part
        // Update with the path to your audio file
        // Create a temporary file
        try{
            File tempFile = File.createTempFile("upload", ".mp3");
            // Transfer the MultipartFile to the temporary file
            multipartFile.transferTo(tempFile);
            // Create a FileSystemResource from the temporary file
            fileResource = new FileSystemResource(tempFile);
        } catch (IOException e) {
            // Handle IOException
            throw new RuntimeException("Error creating FileSystemResource from MultipartFile", e);
        }

        formData.add("file", fileResource);  // Field name should match API's requirement

        Mono<String> response = webClient.post()
                .uri("https://api.sarvam.ai/speech-to-text")
                .header("API-Subscription-Key", SARVAM_API_KEY)  // Use the correct authorization header
                .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)  // Set content type to multipart/form-data
                .body(BodyInserters.fromMultipartData(formData))  // Add multipart data
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new RuntimeException("4xx Client Error: " + body))
                        )
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new RuntimeException("5xx Server Error: " + body))
                        )
                )
                .bodyToMono(String.class);

        try {
            String result = response.block();  // Blocking for simplicity; use async handling in production
            log.info("Completed calling Sarvam API. Response after upoloading: {}", result);
            return result;
        } catch (Exception e) {
            log.info("Error calling Sarvam API : {}", e.getMessage());
        }
        return "null";
    }
}