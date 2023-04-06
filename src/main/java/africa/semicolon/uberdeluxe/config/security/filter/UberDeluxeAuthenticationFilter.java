package africa.semicolon.uberdeluxe.config.security.filter;

import africa.semicolon.uberdeluxe.config.security.util.JwtUtil;
import africa.semicolon.uberdeluxe.data.models.AppUser;
import africa.semicolon.uberdeluxe.exception.BusinessLogicException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UberDeluxeAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    ObjectMapper mapper = new ObjectMapper();

    //1. Create an authenticated object that contains authentication credentials,
    // but is not yet authenticated.

    AppUser user;
    try{
      user = mapper.readValue(request.getInputStream(), AppUser.class);
      Authentication authentication =
              new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
      //2. Delegate authentication responsibility for authentication object in 1 to the manager
      //3. Get back the now authenticated authentication object from the manager
      Authentication authenticationResult =
              authenticationManager.authenticate(authentication);
      //4. store authenticated authentication object in the security context
      if (authenticationResult !=null) return getAuthentication(authenticationResult);
    }catch (IOException e){
      throw new BusinessLogicException(e.getMessage());
    }
    throw new BusinessLogicException("oops!");
}

  private Authentication getAuthentication(Authentication authenticationResult) {
    SecurityContextHolder.getContext().setAuthentication(authenticationResult);
    return SecurityContextHolder.getContext().getAuthentication();
  }
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    ObjectMapper mapper = new ObjectMapper();

    Map<String, Object> claims = authResult.getAuthorities().stream()
            .collect(Collectors.toMap(GrantedAuthority::getAuthority, v->v));
    String accessToken = Jwts.builder()
                              .setIssuer("uber_deluxe")
                              .setIssuedAt(new Date())
                              .setClaims(claims)
                              .setExpiration(Date.from(Instant.now()
                                        .plusSeconds(BigInteger.valueOf(60).longValue()*
                                        BigInteger.valueOf(60).intValue())))
                              .signWith(SignatureAlgorithm.HS512, jwtUtil.getJwtSecret())
                              .compact();

    String refreshToken = Jwts.builder()
            .setIssuer("uber_deluxe")
            .setExpiration(Date.from(Instant.now()
                    .plusSeconds(BigInteger.valueOf(3600).longValue()*
                            BigInteger.valueOf(24).intValue())))
            .signWith(SignatureAlgorithm.HS512, jwtUtil.getJwtSecret())
            .compact();

    Map<String, String> tokens = new HashMap<>();
    tokens.put("access_token", accessToken);
    tokens.put("refresh_token", refreshToken);

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    mapper.writeValue(response.getOutputStream(), tokens);




  }
  }

