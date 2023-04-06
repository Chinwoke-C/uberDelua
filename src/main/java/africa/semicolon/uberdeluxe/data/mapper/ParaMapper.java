package africa.semicolon.uberdeluxe.data.mapper;

import africa.semicolon.uberdeluxe.data.dto.request.RegisterDriverRequest;
import africa.semicolon.uberdeluxe.data.dto.request.RegisterRequest;
import africa.semicolon.uberdeluxe.data.models.AppUser;

public class ParaMapper {
    public static AppUser map(RegisterRequest request){
        AppUser appUser = new AppUser();
        appUser.setName(request.getName());
        appUser.setPassword(request.getPassword());
        appUser.setEmail(request.getEmail());
        return appUser;
    }
}
