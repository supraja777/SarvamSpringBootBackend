package com.sarvam;

import io.swagger.v3.oas.models.media.MediaType;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

@RestController
@RequiredArgsConstructor
public class SarvamController {
    private final SarvamApiService sarvamApiService;

    @GetMapping(path = "/sarvam/translateText")
    public String sarvamTranslateText() {
        return sarvamApiService.translateText();
    }

    @GetMapping(path = "/sarvam/audio-to-transcript")
    public String sarvamAudioToTranscript() {
        return sarvamApiService.getTranscriptFromAudio();
    }

    @PostMapping(path = "/sarvam/audio-to-transcript/file")
    public String sarvamAudioToTranscriptFile(@RequestParam(value = "file") MultipartFile file) {
        return sarvamApiService.getTranscriptFromAudioFile(file);
    }

}