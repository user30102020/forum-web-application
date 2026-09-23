package ru.kpfu.forum.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Service
public class TranslationService {

    private final RestClient restClient;

    public TranslationService(@Value("${translation.url}") String translationUrl) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(30));
        this.restClient = RestClient.builder()
                .baseUrl(translationUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public String translate(String text, String targetLanguage) {
        TranslateResponse response = restClient.post()
                .uri("/translate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new TranslateRequest(text, "auto", targetLanguage))
                .retrieve()
                .body(TranslateResponse.class);
        return response.translatedText();
    }

    record TranslateRequest(String q, String source, String target) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TranslateResponse(String translatedText) {
    }
}