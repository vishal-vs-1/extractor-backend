package com.tool.reg.service;

import com.tool.reg.exception.NoDataException;
import com.tool.reg.exception.PdfGenerationException;
import com.tool.reg.exception.TextExtractionException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

public interface FileService {

    //Methods for url

    InputStreamResource extractEmailsFromUrl(String url);

    InputStreamResource extractCustomPatternsFromUrl(String url, String cPattern);

    InputStreamResource extractViaPromptUrl(String url, String input);


    //Methods for file

    InputStreamResource extractEmailsFromFile(MultipartFile file) throws IOException;

    InputStreamResource extractCustomPatternsFromFile(MultipartFile file, String cPattern) throws IOException;

    InputStreamResource extractViaPromptFile(MultipartFile file, String prompt) throws IOException;

}
