package africa.semicolon.uberdeluxe.service;

import africa.semicolon.uberdeluxe.data.dto.request.RegisterDriverRequest;
import africa.semicolon.uberdeluxe.data.dto.request.RegisterRequest;
import africa.semicolon.uberdeluxe.data.dto.response.RegisterResponse;
import africa.semicolon.uberdeluxe.data.dto.response.UpdateDriverResponse;
import africa.semicolon.uberdeluxe.data.models.Driver;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface DriverService {
    RegisterResponse register(RegisterDriverRequest registerRequest);
    Optional<Driver> getDriverBy(Long driverId);
    Driver getDriverById(Long driverId);
    void saveDriver(Driver driver);
    Driver updateDriver(Long driverId, JsonPatch updateDriverPayload);
    UpdateDriverResponse changeImage(MultipartFile licenseImg, Long driverId);
}
