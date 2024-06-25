package com.tool.reg.controller;

import com.tool.reg.dto.UrlDto;
import com.tool.reg.service.FileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class MainController {

    private final FileService fileService;

    MainController(FileService file) {
        fileService = file;
    }

    //Endpoints for url handling

    @PostMapping("/web/email")
    ResponseEntity<InputStreamResource> handleUrlEmailExtract(@RequestBody UrlDto dto) {

        // Call your service method to get the inputstreamresource
        InputStreamResource resource = fileService.extractEmailsFromUrl(dto.getUrl());

        // Prepare headers to instruct browser to download the file
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

    }

    @PostMapping("/web/custom")
    ResponseEntity<InputStreamResource> handleUrlCustomPatternExtract(@RequestBody UrlDto dto) {

        // Call your service method to get the ByteArrayOutputStream
        InputStreamResource resource = fileService.extractCustomPatternsFromUrl(dto.getUrl(), dto.getInput());

        // Prepare headers to instruct browser to download the file
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

    }

    @PostMapping("/web/abstract")
    ResponseEntity<InputStreamResource> handleUrlAbstractText(@RequestBody UrlDto dto) {

        // Call your service method to get the ByteArrayOutputStream
        InputStreamResource resource = fileService.extractViaPromptUrl(dto.getUrl(), dto.getInput());

        // Prepare headers to instruct browser to download the file
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

    }


    //Endpoints for file handling

    @PostMapping("/file/email")
    ResponseEntity<InputStreamResource> handleFileEmailExtract(@RequestBody MultipartFile file) throws IOException {

        // Call your service method to get the ByteArrayOutputStream
        InputStreamResource resource = fileService.extractEmailsFromFile(file);

        // Prepare headers to instruct browser to download the file
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

    }

    @PostMapping("/file/custom")
    ResponseEntity<InputStreamResource> handleFileCustomPatternExtract(@RequestParam MultipartFile file, @RequestParam String pattern) throws IOException {

        // Call your service method to get the ByteArrayOutputStream
        InputStreamResource resource = fileService.extractCustomPatternsFromFile(file, pattern);

        // Prepare headers to instruct browser to download the file
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }


    @PostMapping("/file/abstract")
    ResponseEntity<InputStreamResource> handleFileAbstractText(@RequestParam MultipartFile file, @RequestParam String text) throws IOException {

        // Call your service method to get the ByteArrayOutputStream
        InputStreamResource resource = fileService.extractViaPromptFile(file, text);

        // Prepare headers to instruct browser to download the file
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

    }
}
