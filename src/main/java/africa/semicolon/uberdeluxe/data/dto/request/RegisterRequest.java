package africa.semicolon.uberdeluxe.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RegisterRequest {
    @JsonProperty("email")
    private String email;
    @JsonProperty("full_name")
    private String name;
    @JsonProperty("password")
    private String password;
}
