package africa.semicolon.uberdeluxe.data.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DistanceMatrixRow {
    private List<DistanceMatrixElement> elements;
}
