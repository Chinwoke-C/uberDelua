package africa.semicolon.uberdeluxe.controller;

import africa.semicolon.uberdeluxe.data.dto.request.RegisterDriverRequest;
import africa.semicolon.uberdeluxe.data.dto.response.ApiResponse;
import africa.semicolon.uberdeluxe.data.dto.response.UpdateDriverResponse;
import africa.semicolon.uberdeluxe.exception.BusinessLogicException;
import africa.semicolon.uberdeluxe.service.DriverService;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/driver")
@AllArgsConstructor
public class DriverController {
    private final DriverService driverService;
@PostMapping(value= "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute RegisterDriverRequest registerDriverRequest){
        try{
            var response = driverService.register(registerDriverRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessLogicException exception) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.builder()
                    .message(exception.getMessage())
                    .build()
                    );
        }
    }
    @GetMapping("{driverId}")
    public ResponseEntity<?>getDriverById(@PathVariable Long driverId){
    var foundDriver = driverService.getDriverBy(driverId);
        return ResponseEntity.status(HttpStatus.OK).body(foundDriver);
    }
    @PatchMapping(value = "/update/{driverId}", consumes = "application/json-patch+json")
    public ResponseEntity<?>updateDriver(@PathVariable Long driverId, @RequestBody JsonPatch updatePatch){
    try{
        var response = driverService.updateDriver(driverId, updatePatch);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    }
    @PostMapping("{id}")
    public ResponseEntity<?> changeImage(@PathVariable Long id, @ModelAttribute MultipartFile licenseImg){
    var response = driverService.changeImage(licenseImg, id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
