package leafmap.server.domain.user.service;

import leafmap.server.domain.user.dto.MyPageResponseDto;
import leafmap.server.domain.user.dto.ProfileRequestDto;

public interface MyPageService {

    MyPageResponseDto getMyPage(Long userId);

    void patchUpdate(Long userId, ProfileRequestDto profileRequestDto);
}
