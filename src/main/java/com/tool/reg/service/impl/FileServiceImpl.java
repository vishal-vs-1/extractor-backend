package com.tool.reg.service.impl;

import com.tool.reg.dto.GeneratedPattern;
import com.tool.reg.service.FileService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileServiceImpl implements FileService {

    private OpenAiChatClient chatClient;

    private Tika tika;

    public FileServiceImpl(OpenAiChatClient chatClient, Tika tika){
        this.tika = tika;
        this.chatClient = chatClient;
    }

    @Override
    public OutputStream extractCustomPatternsFromUrl(String url, String cPattern){

        String text = extractTextFromURL(url);

        Set<String> matches = new HashSet<>();
        Pattern pattern = Pattern.compile(cPattern);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return generatePdf(matches);
    }

    @Override
    public OutputStream extractCustomPatternsFromFile(MultipartFile file, String cPattern) throws Exception {

        String text = extractTextFromFile(file);

        Set<String> matches = new HashSet<>();
        Pattern pattern = Pattern.compile(cPattern);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return generatePdf(matches);
    }

    @Override
    public OutputStream extractEmailsFromFile(MultipartFile file) throws Exception {

        String text = extractTextFromFile(file);

        Set<String> matches = new HashSet<>();
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        System.out.println(String.join(", ", matches));
        return generatePdf(matches);
    }

    @Override
    public OutputStream extractEmailsFromUrl(String url){

        String text = extractTextFromURL(url);

        Set<String> matches = new HashSet<>();
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        System.out.println(String.join(", ", matches));
        return generatePdf(matches);
    }

    @Override
    public OutputStream extractViaPromptUrl(String url, String input){
        var outputParser = new BeanOutputParser<>(GeneratedPattern.class);
        String message =
                """
                Create a regular expression in java language for this - " {input} ".
                {format}
                """;

        var promptTemplate = new PromptTemplate(message, Map.of("input", input, "format", outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();
        GeneratedPattern pattern = outputParser.parse(generation.getOutput().getContent());

        System.out.println(pattern.getRegexPattern());

        return extractCustomPatternsFromUrl(url, pattern.getRegexPattern());
    }

    @Override
    public OutputStream extractViaPromptFile(MultipartFile file, String input) throws Exception {
        BeanOutputParser<GeneratedPattern> outputParser = new BeanOutputParser<>(GeneratedPattern.class);
        String message =
                """
                Create a regular expression in java language for this - " {input} ".
                {format}
                """;

        var promptTemplate = new PromptTemplate(message, Map.of("input", input, "format", outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();
        GeneratedPattern pattern = outputParser.parse(generation.getOutput().getContent());

        return extractCustomPatternsFromFile(file, pattern.getRegexPattern());
    }

    private OutputStream generatePdf(Set<String> extractedData){

        ByteArrayOutputStream stream = null;

        try(PDDocument document = new PDDocument();){
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 13);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            extractedData.forEach(text -> {
                try {
                    contentStream.showText(text);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            contentStream.endText();
            contentStream.close();

            stream = new ByteArrayOutputStream();
            document.save(stream);
        }
        catch (IOException ex){
            System.out.println(ex);
        }
        return stream;
    }

    private boolean validateFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String fileType = tika.detect(file.getInputStream());
        boolean check = (
                (filename.endsWith(".pdf") && fileType.equals("application/pdf")) ||
                        (filename.endsWith(".txt") && fileType.equals("text/plain"))||
                        (filename.endsWith(".docx")) && fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
        return check;
    }

    private String extractTextFromFile(MultipartFile file) throws Exception {

        boolean validated = this.validateFile(file);
        if(!validated)
            throw new Exception("error");

        String result = null;
        String fileName = file.getOriginalFilename();

        if (fileName.endsWith(".txt"))
            result = this.extractTextTxt(file);
        else if (fileName.endsWith(".pdf"))
            result = this.extractTextPdf(file);
        else if (fileName.endsWith(".docx"))
            result = this.extractTextWordDocx(file);

        return result;
    }

    private String extractTextFromURL(String url){
        StringBuilder result = new StringBuilder();
        try {
            URL urlObj = new URL(url);
            URLConnection urlConnection = urlObj.openConnection();
            var inputStream = new InputStreamReader(urlConnection.getInputStream());
            var bufferedReader = new BufferedReader(inputStream);
            String line;
            while((line = bufferedReader.readLine()) != null){
                result.append(line);
            }
        } catch(MalformedURLException ex){
            System.out.println(ex);
        } catch (IOException ex){
            System.out.println();
        }

        return result.toString();
    }

    private String extractTextWordDocx(MultipartFile file){
        String text = null;
        try (var inputStream = file.getInputStream();
             var document = new XWPFDocument(inputStream);
             var extractor = new XWPFWordExtractor(document);){

            text = extractor.getText();
        } catch (IOException ex){
            System.out.println("Some error occured");
        }

        return text;
    }

    private String extractTextTxt(MultipartFile file){

        StringBuilder result = new StringBuilder();

        try(var inputStream = file.getInputStream();
            var inputStreamReader = new InputStreamReader(inputStream);
            var reader= new BufferedReader(inputStreamReader)){

            String line;
            while((line = reader.readLine()) != null){
                result.append(line);
            }

        } catch (IOException ex){
            System.out.println("error in reading file");
        }

        return result.toString();
    }

    private String extractTextPdf(MultipartFile file){
        byte[] bytes = null;
        String text = null;
        try{
            bytes = file.getBytes();
        }
        catch (IOException ex){
            System.out.println("can't get byte stream");
        }
        try(PDDocument document = Loader.loadPDF(bytes)){
            var stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (IOException ex){
            System.out.println("error while reading file");
        }

        return text;
    }

}
