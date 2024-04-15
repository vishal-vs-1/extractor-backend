package com.tool.reg.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;

public interface FileService {

    OutputStream extractCustomPatternsFromUrl(String url, String cPattern);

    OutputStream extractCustomPatternsFromFile(MultipartFile file, String cPattern) throws Exception;

    OutputStream extractEmailsFromFile(MultipartFile file) throws Exception;

    OutputStream extractEmailsFromUrl(String url);

    OutputStream extractViaPromptFile(MultipartFile file, String prompt) throws Exception;

    OutputStream extractViaPromptUrl(String url, String input);
}
