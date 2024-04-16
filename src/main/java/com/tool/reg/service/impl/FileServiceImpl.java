package com.tool.reg.service.impl;

import com.tool.reg.dto.GeneratedPattern;
import com.tool.reg.exception.*;
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
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.parser.BeanOutputParser;
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

    private final OpenAiChatClient chatClient;

    private final Tika tika;

    public FileServiceImpl(OpenAiChatClient chatClient, Tika tika) {
        this.tika = tika;
        this.chatClient = chatClient;
    }

    @Override
    public OutputStream extractCustomPatternsFromUrl(String url, String cPattern) throws TextExtractionException, MalformedURLException, NoDataException, PdfGenerationException {

        String text = extractTextFromURL(url);

        Set<String> matches = new HashSet<>();
        Pattern pattern = Pattern.compile(cPattern);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        if(matches.isEmpty()){
            throw new NoDataException("No email present in provided URL");
        }

        return generatePdf(matches);
    }

    @Override
    public OutputStream extractCustomPatternsFromFile(MultipartFile file, String cPattern) throws TextExtractionException, InvalidFileException, FileReadingException, IOException, NoDataException, PdfGenerationException {

        String text = extractTextFromFile(file);

        Set<String> matches = new HashSet<>();
        Pattern pattern = Pattern.compile(cPattern);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        if(matches.isEmpty()){
            throw new NoDataException("No email present in provided URL");
        }
        return generatePdf(matches);
    }

    @Override
    public OutputStream extractEmailsFromFile(MultipartFile file) throws TextExtractionException, InvalidFileException, FileReadingException, IOException, NoDataException, PdfGenerationException {

        String text = extractTextFromFile(file);

        Set<String> matches = new HashSet<>();
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        if(matches.isEmpty()){
            throw new NoDataException("No email present in provided URL");
        }
        return generatePdf(matches);
    }

    @Override
    public OutputStream extractEmailsFromUrl(String url) throws TextExtractionException, MalformedURLException, NoDataException, PdfGenerationException {

        String text = extractTextFromURL(url);

        Set<String> matches = new HashSet<>();
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        if(matches.isEmpty()){
            throw new NoDataException("No email present in provided URL");
        }

        return generatePdf(matches);
    }

    @Override
    public OutputStream extractViaPromptUrl(String url, String input) throws TextExtractionException, MalformedURLException, PdfGenerationException, NoDataException {
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

    private OutputStream generatePdf(Set<String> extractedData) throws PdfGenerationException {

        ByteArrayOutputStream stream = null;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 13);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            extractedData.forEach(text -> {
                try {
                    contentStream.showText(text);
                    contentStream.newLineAtOffset(0, -15);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
            contentStream.endText();
            contentStream.close();

            stream = new ByteArrayOutputStream();
            document.save(stream);
        } catch (IOException ex) {
            throw new PdfGenerationException("Failed to generate PDF", ex);
        }
        return stream;
    }

    private boolean validateFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();

        if(filename==null){
            return false;
        }

        String fileType = tika.detect(file.getInputStream());
        return (
                (filename.endsWith(".pdf") && fileType.equals("application/pdf")) ||
                        (filename.endsWith(".txt") && fileType.equals("text/plain")) ||
                        (filename.endsWith(".docx")) && fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }

    private String extractTextFromFile(MultipartFile file) throws InvalidFileException, TextExtractionException, NoDataException, FileReadingException, IOException {

        boolean validated = this.validateFile(file);
        if (!validated)
            throw new InvalidFileException("File is not of valid type");

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

    private String extractTextFromURL(String url) throws MalformedURLException, TextExtractionException, NoDataException {
        StringBuilder result = new StringBuilder();
        try {
            URL urlObj = new URL(url);
            URLConnection urlConnection = urlObj.openConnection();
            try (
                    var inputStream = new InputStreamReader(urlConnection.getInputStream());
                    var bufferedReader = new BufferedReader(inputStream);
            ) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (IOException ex) {
            throw new TextExtractionException("Problem while extracting text from provided URL", ex);
        }

        String resultStr = result.toString();

        if (resultStr.trim().isEmpty()) {
            throw new NoDataException("Provided URL has no text");
        }

        return resultStr;
    }

    private String extractTextWordDocx(MultipartFile file) throws TextExtractionException, NoDataException {
        String text = null;
        try (var inputStream = file.getInputStream();
             var document = new XWPFDocument(inputStream);
             var extractor = new XWPFWordExtractor(document);) {

            text = extractor.getText();
        } catch (IOException ex) {
            throw new TextExtractionException("Error extracting text from Word File", ex);
        }

        if (text == null || text.trim().isEmpty()) {
            throw new NoDataException("The word file has no words");
        }

        return text;
    }

    private String extractTextTxt(MultipartFile file) throws TextExtractionException, NoDataException {

        StringBuilder result = new StringBuilder();

        try (var inputStream = file.getInputStream();
             var inputStreamReader = new InputStreamReader(inputStream);
             var reader = new BufferedReader(inputStreamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (IOException ex) {
            throw new TextExtractionException("Error while extracting text fro TXT file", ex);
        }

        String resultStr = result.toString();

        if (resultStr.trim().isEmpty()) {
            throw new NoDataException("Provided TXT file has no text to extract");
        }

        return resultStr;
    }

    private String extractTextPdf(MultipartFile file) throws TextExtractionException, FileReadingException, NoDataException {
        byte[] bytes = null;
        String text = null;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new FileReadingException("Failed to read bytes from PDF", ex);
        }
        try (PDDocument document = Loader.loadPDF(bytes)) {
            var stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (IOException ex) {
            throw new TextExtractionException("Error extracting text from PDF", ex);
        }

        if (text == null || text.trim().isEmpty()) {
            throw new NoDataException("Provided PDF has no text to extract");
        }

        return text;
    }

}
