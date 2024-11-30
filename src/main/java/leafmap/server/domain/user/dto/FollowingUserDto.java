package leafmap.server.domain.user.dto;

import leafmap.server.domain.user.entity.Follow;
import leafmap.server.domain.user.entity.User;
import lombok.Getter;

@Getter
public class FollowingUserDto {

    private Long userId;

    private String username;

    private String bio;

    private String image;

    public FollowingUserDto(Follow follow) {
        User user = follow.getFollowing();
        this.userId = user.getId();
        this.username = user.getUsername();
        this.bio = user.getBio();
        this.image = user.getProfilePicture();
    }
}
