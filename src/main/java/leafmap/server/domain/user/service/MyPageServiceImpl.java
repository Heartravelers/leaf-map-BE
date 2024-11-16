package leafmap.server.domain.user.service;

import leafmap.server.domain.note.entity.Scrap;
import leafmap.server.domain.note.repository.ScrapRepository;
import leafmap.server.domain.user.dto.FollowingUserDto;
import leafmap.server.domain.user.dto.MyPageResponseDto;
import leafmap.server.domain.user.dto.ProfileRequestDto;
import leafmap.server.domain.user.dto.ScrapResponseDto;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyPageServiceImpl implements MyPageService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScrapRepository scrapRepository;

    @Override
    public MyPageResponseDto getMyPage(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            return new MyPageResponseDto(userOptional.get());
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    @Override
    public void patchUpdate(Long userId, ProfileRequestDto profileRequestDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(profileRequestDto.getUsername() != null)
                user.setUsername(profileRequestDto.getUsername());
            if(profileRequestDto.getBio() != null)
                user.setBio(profileRequestDto.getBio());
            if(profileRequestDto.getIsPublic() != null)
                user.setPublic(profileRequestDto.getIsPublic());
            userRepository.save(user);
            return;
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    @Override
    public List<FollowingUserDto> getFollowing(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            return userOptional.get().getFollowings().stream().map(FollowingUserDto::new).toList();
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    @Override
    public List<ScrapResponseDto> getScraps(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            List<Scrap> scraps = scrapRepository.findAllByUser(userOptional.get());
            return scraps.stream().map(ScrapResponseDto::new).toList();
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}
