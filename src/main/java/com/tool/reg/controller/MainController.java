package com.tool.reg.controller;

import com.tool.reg.dto.UrlDto;
import com.tool.reg.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@RestController
public class MainController {

    private final FileService fileService;

    MainController(FileService file){
        fileService = file;
    }

    @PostMapping("/web/email")
    ResponseEntity<byte[]> handleUrlEmailExtract(@RequestBody UrlDto dto){
        try {
            // Call your service method to get the ByteArrayOutputStream
            ByteArrayOutputStream baos = (ByteArrayOutputStream) fileService.extractEmailsFromUrl(dto.getUrl());

            // Prepare headers to instruct browser to download the file
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            // Log the exception (adjust based on your logging framework)
            System.out.println("An error occurred: " + e.getMessage());

            // Return an internal server error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/web/custom")
    ResponseEntity<byte[]> handleUrlCustomPatternExtract(@RequestBody UrlDto dto){
        try {
            // Call your service method to get the ByteArrayOutputStream
            ByteArrayOutputStream baos = (ByteArrayOutputStream) fileService.extractCustomPatternsFromUrl(dto.getUrl(), dto.getInput());

            // Prepare headers to instruct browser to download the file
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            // Log the exception (adjust based on your logging framework)
            System.out.println("An error occurred: " + e.getMessage());

            // Return an internal server error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/file/email")
    ResponseEntity<byte[]> handleFileEmailExtract(@RequestBody MultipartFile file){
        try {
            // Call your service method to get the ByteArrayOutputStream
            ByteArrayOutputStream baos = (ByteArrayOutputStream) fileService.extractEmailsFromFile(file);

            // Prepare headers to instruct browser to download the file
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            // Log the exception (adjust based on your logging framework)
            System.out.println("An error occurred: " + e.getMessage());

            // Return an internal server error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/file/custom")
    ResponseEntity<byte[]> handleFileCustomPatternExtract(@RequestParam MultipartFile file ,@RequestParam String pattern){
        try {
            // Call your service method to get the ByteArrayOutputStream
            ByteArrayOutputStream baos = (ByteArrayOutputStream) fileService.extractCustomPatternsFromFile(file, pattern);

            // Prepare headers to instruct browser to download the file
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            // Log the exception (adjust based on your logging framework)
            System.out.println("An error occurred: " + e.getMessage());

            // Return an internal server error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/web/abstract")
    ResponseEntity<byte[]> handleUrlAbstractText(@RequestBody UrlDto dto){
        try {
            // Call your service method to get the ByteArrayOutputStream
            ByteArrayOutputStream baos = (ByteArrayOutputStream) fileService.extractViaPromptUrl(dto.getUrl(), dto.getInput());

            // Prepare headers to instruct browser to download the file
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            // Log the exception (adjust based on your logging framework)
            System.out.println("An error occurred: " + e.getMessage());

            // Return an internal server error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/file/abstract")
    ResponseEntity<byte[]> handleFileAbstractText(@RequestParam MultipartFile file, @RequestParam String text){
        try {
            // Call your service method to get the ByteArrayOutputStream
            ByteArrayOutputStream baos = (ByteArrayOutputStream) fileService.extractViaPromptFile(file, text);

            // Prepare headers to instruct browser to download the file
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"emails.pdf\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            // Log the exception (adjust based on your logging framework)
            System.out.println("An error occurred: " + e.getMessage());

            // Return an internal server error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
