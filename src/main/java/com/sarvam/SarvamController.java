package com.sarvam;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SarvamController {
    private final SarvamApiService sarvamApiService;

    @GetMapping(path = "/sarvam/translateText")
    public String sarvamTranslateText() {
        return sarvamApiService.translateText();
    }
}