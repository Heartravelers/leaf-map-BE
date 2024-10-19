package leafmap.server.domain.user.service;

import leafmap.server.domain.user.dto.MyPageResponseDto;

public interface MyPageService {

    MyPageResponseDto getMyPage(Long userId);
}
