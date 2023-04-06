package africa.semicolon.uberdeluxe.service;

import africa.semicolon.uberdeluxe.data.dto.request.RegisterDriverRequest;
import africa.semicolon.uberdeluxe.data.dto.request.RegisterRequest;
import africa.semicolon.uberdeluxe.data.dto.response.RegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class DriverServiceImplTest {
    @Autowired
    private DriverService driverService;
    private RegisterDriverRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterDriverRequest();
        registerRequest.setEmail("test@email.com");
        registerRequest.setPassword("test_Password");
        registerRequest.setName("test driver");

    }
    @Test
    void register() throws IOException{
        MockMultipartFile file =
                new MockMultipartFile("test_license",
                        new FileInputStream("C:\\Users\\user\\Downloads\\license img.jpg"));
        registerRequest.setLicenseImage(file);
        var response = driverService.register(registerRequest);
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();

    }

}
