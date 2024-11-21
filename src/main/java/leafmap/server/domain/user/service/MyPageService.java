package leafmap.server.domain.user.service;

import leafmap.server.domain.user.dto.FollowingUserDto;
import leafmap.server.domain.user.dto.MyPageResponseDto;
import leafmap.server.domain.user.dto.ProfileRequestDto;
import leafmap.server.domain.user.dto.ScrapResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MyPageService {

    MyPageResponseDto getMyPage(Long userId);

    void patchUpdate(Long userId, ProfileRequestDto profileRequestDto, MultipartFile file);

    void deleteProfileImage(Long userId);

    List<FollowingUserDto> getFollowing(Long userId);

    List<ScrapResponseDto> getScraps(Long userId);
}
