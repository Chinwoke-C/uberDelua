package africa.semicolon.uberdeluxe.data.dto.response;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateDriverResponse {
    private String imageUrl;
    private String updateTime;
}
