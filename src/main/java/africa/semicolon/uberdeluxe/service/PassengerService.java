package africa.semicolon.uberdeluxe.service;

import africa.semicolon.uberdeluxe.data.dto.request.BookRideRequest;
import africa.semicolon.uberdeluxe.data.dto.request.RegisterRequest;
import africa.semicolon.uberdeluxe.data.dto.response.ApiResponse;
import africa.semicolon.uberdeluxe.data.dto.response.RegisterResponse;
import africa.semicolon.uberdeluxe.data.models.Passenger;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface PassengerService {
    RegisterResponse register(RegisterRequest registerRequest);
    void savePassenger(Passenger passenger);
    Optional<Passenger> getPassengerBy(Long passengerId);

    Passenger getPassengerById(Long passengerId);

    Passenger updatePassenger(Long passengerId, JsonPatch updatePayLoad);
    Page<Passenger> getAllPassenger( int pageNumber);
    void deletePassenger(Long id);
    ApiResponse bookRide(BookRideRequest bookRideRequest);



}