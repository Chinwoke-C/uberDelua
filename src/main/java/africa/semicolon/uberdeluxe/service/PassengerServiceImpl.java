package africa.semicolon.uberdeluxe.service;

import africa.semicolon.uberdeluxe.config.distance.DistanceConfig;
import africa.semicolon.uberdeluxe.data.dto.request.BookRideRequest;
import africa.semicolon.uberdeluxe.data.dto.request.Location;
import africa.semicolon.uberdeluxe.data.dto.request.RegisterRequest;
import africa.semicolon.uberdeluxe.data.dto.response.*;
import africa.semicolon.uberdeluxe.data.mapper.ParaMapper;
import africa.semicolon.uberdeluxe.data.models.AppUser;
import africa.semicolon.uberdeluxe.data.models.Passenger;
import africa.semicolon.uberdeluxe.data.models.Role;
import africa.semicolon.uberdeluxe.data.repositories.PassengerRepository;
import africa.semicolon.uberdeluxe.exception.BusinessLogicException;
import africa.semicolon.uberdeluxe.util.AppUtilities;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class PassengerServiceImpl implements PassengerService{
    private final PassengerRepository passengerRepository;
    private final MockLocationService mockLocationService;
    private  static  final int NUMBER_OF_ITEMS_PER_PAGE = 3;
    private final PasswordEncoder passwordEncoder;
    private final DistanceConfig directionConfig;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
//        AppUser appUser = new AppUser();
//        appUser.setEmail(registerRequest.getEmail());
//        appUser.setPassword(registerRequest.getPassword());
//        appUser.setName(registerRequest.getName());
        AppUser appUser = ParaMapper.map(registerRequest);
        appUser.setRoles(new HashSet<>());
        appUser.getRoles().add(Role.PASSENGER);
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        appUser.setCreatedAt(LocalDateTime.now().toString());
        Passenger passenger = new Passenger();
        passenger.setUserDetails(appUser);
        Passenger savedPassenger = passengerRepository.save(passenger);
        RegisterResponse registerResponse = getRegisterPassengerResponse(savedPassenger);
        return registerResponse;
    }

    @Override
    public void savePassenger(Passenger passenger) {
        passengerRepository.save(passenger);
    }

    @Override
    public Optional<Passenger> getPassengerBy(Long passengerId) {
        return passengerRepository.findById(passengerId);
    }

    @Override
    public Passenger getPassengerById(Long passengerId) {
        return passengerRepository.findById(passengerId).orElseThrow(()->
                new BusinessLogicException(
                        String.format("Passenger with id %d not found", passengerId)
                ));
    }

    @Override
    public Passenger updatePassenger(Long passengerId, JsonPatch updatePayLoad) {
        ObjectMapper mapper = new ObjectMapper();
        Passenger foundPassenger = getPassengerById(passengerId);
        //Passenger Object to node
        JsonNode node = mapper.convertValue(foundPassenger, JsonNode.class);
        try {
            //apply patch
            JsonNode updatedNode = updatePayLoad.apply(node);
            //node to Passenger Object
            var updatedPassenger = mapper.convertValue(updatedNode, Passenger.class);
            updatedPassenger = passengerRepository.save(updatedPassenger);
            return updatedPassenger;

        } catch (JsonPatchException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public Page<Passenger> getAllPassenger(int pageNumber) {
        if(pageNumber < 1) pageNumber = 0;
       else pageNumber = pageNumber -1;
        Pageable pageable = PageRequest.of(pageNumber, NUMBER_OF_ITEMS_PER_PAGE);
        return passengerRepository.findAll(pageable);
    }

    @Override
    public void deletePassenger(Long id) {
        passengerRepository.deleteById(id);
    }

    @Override
    public ApiResponse bookRide(BookRideRequest bookRideRequest) {
        //1. find passenger
        getPassengerById(bookRideRequest.getPassengerId());
        //2.calculate distance between origin and destination
        var response = mockLocationService.getDistanceInformation(bookRideRequest.getOrigin(), bookRideRequest.getDestination());
        DistanceMatrixElement distanceInformation = response.getRows().get(0).getElements().get(0);
        log.info("response!!->{}", distanceInformation);
        //3. calculate eta
        var eta = distanceInformation.getDuration().getText();
        //4. calculate price
        BigDecimal fare = AppUtilities.calculateRideFare(distanceInformation.getDistance().getText());
        return ApiResponse.builder().fare(fare).estimatedTimeOfArrival(eta).build();

    }

    private DistanceMatrixElement getDistanceInformation(Location origin, Location destination) {
        RestTemplate restTemplate = new RestTemplate();
        String url = buildDistanceRequestUrl(origin, destination);
        ResponseEntity<GoogleDistanceResponse> response =
                restTemplate.getForEntity(url, GoogleDistanceResponse.class);

        return Objects.requireNonNull(response.getBody()).getRows().stream()
                .findFirst().orElseThrow()
                .getElements().stream()
                .findFirst()
                .orElseThrow();
    }

    private String buildDistanceRequestUrl(Location origin, Location destination) {
        return directionConfig.getGoogleDistanceUrl()+"/"+AppUtilities.JSON_CONSTANT+"?"
                +"destinations="+AppUtilities.buildLocation(destination)+"&origins="
                +AppUtilities.buildLocation(origin)+"&mode=driving"+"&traffic_model=pessimistic"
                +"&departure_time="+ LocalDateTime.now().toEpochSecond(ZoneOffset.of("+01:00"))
                +"&key="+directionConfig.getGoogleApiKey();

    }


    private static RegisterResponse getRegisterPassengerResponse (Passenger savedPassenger){
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setId(savedPassenger.getId());
            registerResponse.setSuccessful(true);
            registerResponse.setMessage("User Registration Successful");
            return registerResponse;
        }

    }
