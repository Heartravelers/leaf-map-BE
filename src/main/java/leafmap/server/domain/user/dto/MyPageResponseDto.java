package leafmap.server.domain.user.dto;

import leafmap.server.domain.user.entity.User;
import lombok.Getter;

@Getter
public class MyPageResponseDto {

    private String username;

    private String image;

    private String email;

    private String bio;

    public MyPageResponseDto(User user) {
        this.username = user.getUsername();
        this.image = user.getProfilePicture();
        this.email = user.getEmail();
        this.bio = user.getBio();
    }

}
