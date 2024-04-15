package com.tool.reg;

import com.tool.reg.controller.MainController;
import com.tool.reg.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@WebMvcTest(MainController.class)
public class MainControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    private MockMultipartFile file;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setup() {
        file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write("example@example.com".getBytes());
        } catch (IOException ex) {
            System.out.println("ex" + ex);
        }
    }

}
