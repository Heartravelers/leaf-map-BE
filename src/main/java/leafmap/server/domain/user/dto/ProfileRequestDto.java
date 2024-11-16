package leafmap.server.domain.user.dto;

import lombok.Getter;

@Getter
public class ProfileRequestDto {

    private String username;

    private String bio;

    private Boolean isPublic;

}
