package africa.semicolon.uberdeluxe.controller;

import africa.semicolon.uberdeluxe.data.dto.request.BookRideRequest;
import africa.semicolon.uberdeluxe.data.dto.request.RegisterRequest;
import africa.semicolon.uberdeluxe.data.dto.response.ApiResponse;
import africa.semicolon.uberdeluxe.data.dto.response.RegisterResponse;
import africa.semicolon.uberdeluxe.service.PassengerService;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
    @RequestMapping("/api/v1/passenger")
    @AllArgsConstructor
public class PassengerController {
        private final PassengerService passengerService;
        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
            RegisterResponse registerResponse = passengerService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
        }
        @GetMapping("{passengerId}")
        public  ResponseEntity<?> getPassengerById(@PathVariable Long passengerId){
            var foundPassenger = passengerService.getPassengerById(passengerId);
            return ResponseEntity.status(HttpStatus.OK).body(foundPassenger);
        }
        @PatchMapping (value= "{passengerId}", consumes = "application/json-patch+json")
        public ResponseEntity<?> updatePassenger(@PathVariable Long passengerId, @RequestBody JsonPatch updatePatch){
            try{
                var response = passengerService.updatePassenger(passengerId, updatePatch);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } catch (Exception exception) {
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }
        @DeleteMapping("{passengerId}")
        public ResponseEntity<?> deletePassenger(@PathVariable Long passengerId){
            passengerService.deletePassenger(passengerId);
            return ResponseEntity.ok("Passenger deleted successfully");
        }
        @GetMapping("all/{pageNumber}")
        public  ResponseEntity<?> getAllPassengers(@PathVariable int pageNumber){
            var response =  passengerService.getAllPassenger(pageNumber);
            return ResponseEntity.ok(response.getContent());
        }
        @PostMapping(value = "/book")
        public ResponseEntity<?> bookRide(@RequestBody BookRideRequest bookRideRequest){
            ApiResponse apiResponse = passengerService.bookRide(bookRideRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        }
}
