package africa.semicolon.uberdeluxe.data.mapper;

import africa.semicolon.uberdeluxe.data.dto.request.RegisterDriverRequest;
import africa.semicolon.uberdeluxe.data.dto.request.RegisterRequest;
import africa.semicolon.uberdeluxe.data.models.AppUser;

public class DriverMapper {
    public static AppUser map(RegisterDriverRequest request){
        AppUser appUser = new AppUser();
        appUser.setName(request.getName());
        appUser.setPassword(request.getPassword());
//        appUser.setEmail(request.getEmail());
        return appUser;
    }
}
