package africa.semicolon.uberdeluxe.service;

import africa.semicolon.uberdeluxe.data.dto.request.EmailNotificationRequest;
import africa.semicolon.uberdeluxe.data.dto.request.Recipient;
import africa.semicolon.uberdeluxe.data.dto.request.RegisterDriverRequest;
import africa.semicolon.uberdeluxe.data.dto.response.RegisterResponse;
import africa.semicolon.uberdeluxe.data.dto.response.UpdateDriverResponse;
import africa.semicolon.uberdeluxe.data.models.AppUser;
import africa.semicolon.uberdeluxe.data.models.Driver;
import africa.semicolon.uberdeluxe.data.repositories.DriverRepository;
import africa.semicolon.uberdeluxe.cloud.CloudService;
import africa.semicolon.uberdeluxe.exception.BusinessLogicException;
import africa.semicolon.uberdeluxe.exception.ImageUploadException;
import africa.semicolon.uberdeluxe.notification.MailService;
import africa.semicolon.uberdeluxe.util.AppUtilities;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService{

    private final CloudService cloudService;
private final DriverRepository driverRepository;
private final ModelMapper modelMapper;
private final MailService mailService;
    @Override
    public RegisterResponse register(RegisterDriverRequest registerRequest) {
        AppUser driverDetails = modelMapper.map(registerRequest, AppUser.class);
        driverDetails.setCreatedAt(LocalDateTime.now().toString());
        //steps
        //1. upload drivers license image
        var imageUrl = cloudService.upload(registerRequest.getLicenseImage());
        if (imageUrl==null)
            throw new ImageUploadException("Driver Registration failed");
        //2. create driver object
        Driver driver = Driver.builder()
                .userDetails(driverDetails)
                .licenseImage(imageUrl)
                .build();
        //3. save driver
        Driver savedDriver = driverRepository.save(driver);
        //4. send verification mail to driver
        EmailNotificationRequest emailRequest = buildNotificationRequest(
                savedDriver.getUserDetails().getEmail(),
                savedDriver.getUserDetails().getName()
                );

        String response = mailService.sendHtmlMail(emailRequest);
        if(response==null) return getRegisterFailureResponse();

        return RegisterResponse.builder()
                .id(savedDriver.getId())
                .isSuccessful(true)
                .message("Driver Registration Successful")
                .build();
    }

    @Override
    public Optional<Driver> getDriverBy(Long driverId) {
        return driverRepository.findById(driverId);
    }

    @Override
    public Driver getDriverById(Long driverId) {
        return driverRepository.findById(driverId).orElseThrow(()->
               new BusinessLogicException(
                       String.format("driver with id %d not found",driverId)
               ));
    }

    @Override
    public void saveDriver(Driver driver) {
        driverRepository.save(driver);

    }

    @Override
    public Driver updateDriver(Long driverId, JsonPatch updateDriverPayload) {
        ObjectMapper mapper = new ObjectMapper();
        Driver foundDriver = getDriverById(driverId);
        JsonNode node = mapper.convertValue(foundDriver, JsonNode.class);
        try{
            JsonNode updateDriverNode = updateDriverPayload.apply(node);
            var updatedDriver = mapper.convertValue(updateDriverNode, Driver.class);
            updatedDriver = driverRepository.save(updatedDriver);
            return updatedDriver;
        } catch (JsonPatchException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public UpdateDriverResponse changeImage(MultipartFile licenseImg, Long driverId) {
        Driver driver = getDriverById(driverId);
        String imageUrl = cloudService.upload(licenseImg);
        if (imageUrl.isEmpty()){
            throw new ImageUploadException("Please insert your license image");
        }
        driver.setLicenseImage(imageUrl);
        driverRepository.save(driver);
        return UpdateDriverResponse.builder()
                .imageUrl(imageUrl)
                .updateTime(LocalDateTime.now().toString())
                .build();
    }

    private RegisterResponse getRegisterFailureResponse() {
        return RegisterResponse.builder()
                .id(-1L)
                .isSuccessful(false)
                .message("Driver Registration Failed")
                .build();
    }

    private EmailNotificationRequest buildNotificationRequest(String email, String name) {
        EmailNotificationRequest request = new EmailNotificationRequest();
        request.getTo().add(new Recipient(name, email));
        String template = AppUtilities.getMailTemplate();

        String content = String.format(template, name, "name");
        request.setHtmlContent(content);
        return request;
    }



//    private RegisterResponse getRegisterResponse(Driver savedDriver) {
//        RegisterResponse registerResponse = new RegisterResponse();
//        registerResponse.setId(savedDriver.getId());
//        registerResponse.setCode(HttpStatus.CREATED.value());
//        registerResponse.setSuccessful(true);
//        registerResponse.setMessage("User Registration Successful");
//        return registerResponse;
//    }
}
