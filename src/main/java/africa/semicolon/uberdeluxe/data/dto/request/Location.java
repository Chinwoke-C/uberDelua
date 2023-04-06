package africa.semicolon.uberdeluxe.data.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Location {
    private String houseNumber;
    private String street;
    private String city;
    private String state;

//    public Location(String address) {
//        String[] parts = address.split(", ");
//        this.houseNumber = parts[0];
//        this.street = parts[1];
//        this.city = parts[2];
//        this.state = parts[3];
//    }
}
