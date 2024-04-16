package com.tool.reg.service;

import com.tool.reg.exception.NoDataException;
import com.tool.reg.exception.PdfGenerationException;
import com.tool.reg.exception.TextExtractionException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.net.MalformedURLException;

public interface FileService {

    InputStreamResource extractCustomPatternsFromUrl(String url, String cPattern) throws TextExtractionException, MalformedURLException, NoDataException, PdfGenerationException;

    InputStreamResource extractCustomPatternsFromFile(MultipartFile file, String cPattern) throws Exception;

    InputStreamResource extractEmailsFromFile(MultipartFile file) throws Exception;

    InputStreamResource extractEmailsFromUrl(String url) throws TextExtractionException, MalformedURLException, NoDataException, PdfGenerationException;

    InputStreamResource extractViaPromptFile(MultipartFile file, String prompt) throws Exception;

    InputStreamResource extractViaPromptUrl(String url, String input) throws TextExtractionException, MalformedURLException, PdfGenerationException, NoDataException;
}
