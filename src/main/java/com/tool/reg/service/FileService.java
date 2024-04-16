package com.tool.reg.service;

import com.tool.reg.exception.NoDataException;
import com.tool.reg.exception.PdfGenerationException;
import com.tool.reg.exception.TextExtractionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.net.MalformedURLException;

public interface FileService {

    OutputStream extractCustomPatternsFromUrl(String url, String cPattern) throws TextExtractionException, MalformedURLException, NoDataException, PdfGenerationException;

    OutputStream extractCustomPatternsFromFile(MultipartFile file, String cPattern) throws Exception;

    OutputStream extractEmailsFromFile(MultipartFile file) throws Exception;

    OutputStream extractEmailsFromUrl(String url) throws TextExtractionException, MalformedURLException, NoDataException, PdfGenerationException;

    OutputStream extractViaPromptFile(MultipartFile file, String prompt) throws Exception;

    OutputStream extractViaPromptUrl(String url, String input) throws TextExtractionException, MalformedURLException, PdfGenerationException, NoDataException;
}
