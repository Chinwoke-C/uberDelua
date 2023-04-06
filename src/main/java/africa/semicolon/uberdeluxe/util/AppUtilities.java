package africa.semicolon.uberdeluxe.util;

import africa.semicolon.uberdeluxe.data.dto.request.Location;
import africa.semicolon.uberdeluxe.data.dto.response.GoogleDistance;
import africa.semicolon.uberdeluxe.data.models.Passenger;
import africa.semicolon.uberdeluxe.exception.BusinessLogicException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.stream.Collectors;


public class AppUtilities {
    public static final String JSON_CONSTANT = "json" ;
    private static final String USER_VERIFICATION_BASE_URL = "localhost:8080/api/v1/user/account/verify";
    public static final String ADMIN_INVITE_MAIL_TEMPLATE_LOCATION = "C:\\Users\\semicolon\\Documents\\code\\springboot-projects\\uber_deluxe\\src\\main\\resources\\adminMail.txt";

    public static String getMailTemplate(){
        try(BufferedReader reader = new BufferedReader(new FileReader(
                "C:\\Users\\user\\Downloads\\uberdeluxe\\src\\main\\resources\\welcome.txt"))){
        return reader.lines().collect(Collectors.joining());
    }catch (IOException exception){
            throw new BusinessLogicException(exception.getMessage());
        }
    }
    public static String getAdminMailTemplate(){
        try(BufferedReader reader = new BufferedReader(new FileReader(ADMIN_INVITE_MAIL_TEMPLATE_LOCATION))){
        return reader.lines().collect(Collectors.joining());
    } catch (IOException exception) {
            throw new BusinessLogicException(exception.getMessage());
        }
    }


        public static String generateVerificationLink(Long userId) {
        return USER_VERIFICATION_BASE_URL+"?userId="+userId+"&token="+generateVerificationToken();
    }

    private static String generateVerificationToken() {
        return Jwts.builder()
                .setIssuer("uber_deluxe")
                .signWith(SignatureAlgorithm.HS512,
                TextCodec.BASE64.decode("${jwt.secret.key}"))
                .setIssuedAt(new Date())
                .compact();
    }

    public static boolean isValidToken(String token){
        return Jwts.parser()
                .isSigned(token);
    }

    public static BigDecimal calculateRideFare(String distance){
        return BigDecimal
                .valueOf(Double.parseDouble(distance.split("k")[0]))
                .multiply(BigDecimal.valueOf(1000));
    }

    public static String buildLocation(Location location)  {

        return location.getHouseNumber()+","+location.getStreet()+","+location.getCity()+","+location.getState();
    }
}
