package africa.semicolon.uberdeluxe.data.dto.response;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ApiResponse {
    private String message;
    private BigDecimal fare;
    private String estimatedTimeOfArrival;
}
