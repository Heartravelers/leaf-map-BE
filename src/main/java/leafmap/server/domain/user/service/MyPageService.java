package leafmap.server.domain.user.service;

import leafmap.server.domain.user.dto.FollowingUserDto;
import leafmap.server.domain.user.dto.MyPageResponseDto;
import leafmap.server.domain.user.dto.ProfileRequestDto;

import java.util.List;

public interface MyPageService {

    MyPageResponseDto getMyPage(Long userId);

    void patchUpdate(Long userId, ProfileRequestDto profileRequestDto);

    List<FollowingUserDto> getFollowing(Long userId);
}
