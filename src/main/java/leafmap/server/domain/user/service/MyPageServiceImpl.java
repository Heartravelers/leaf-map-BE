package leafmap.server.domain.user.service;

import leafmap.server.domain.note.entity.Scrap;
import leafmap.server.domain.note.repository.ScrapRepository;
import leafmap.server.domain.user.dto.FollowingUserDto;
import leafmap.server.domain.user.dto.MyPageResponseDto;
import leafmap.server.domain.user.dto.ProfileRequestDto;
import leafmap.server.domain.user.dto.ScrapResponseDto;
import leafmap.server.domain.user.entity.Follow;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import leafmap.server.global.util.S3Provider;
import leafmap.server.global.util.s3.dto.S3UploadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MyPageServiceImpl implements MyPageService {

    private static final String DIR_NAME = "profile_image"; // 임시

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    S3Provider s3Provider;

    @Override
    public MyPageResponseDto getMyPage(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            return new MyPageResponseDto(userOptional.get());
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    @Override
    public void patchUpdate(Long userId, ProfileRequestDto profileRequestDto, MultipartFile file) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(profileRequestDto.getUsername() != null)
                user.setUsername(profileRequestDto.getUsername());
            if(profileRequestDto.getBio() != null)
                user.setBio(profileRequestDto.getBio());
            if(profileRequestDto.getIsPublic() != null)
                user.setPublic(profileRequestDto.getIsPublic());
            if(file != null && !file.isEmpty()){
                String url = s3Provider.uploadFile(file, new S3UploadRequest(userId, DIR_NAME));
                if(user.getProfilePicture() != null) {
                    s3Provider.removeFile(user.getProfilePicture(), DIR_NAME); // 이미지 삭제가 안됨
                }
                user.setProfilePicture(url);
            }
            userRepository.save(user);
            return;
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    @Override
    public void deleteProfileImage(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(user.getProfilePicture() != null) {
                s3Provider.removeFile(user.getProfilePicture(), DIR_NAME); // 이미지 삭제가 안됨
                user.setProfilePicture(null);
                userRepository.save(user);
                return;
            }
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    @Override
    public List<FollowingUserDto> getFollowing(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            List<Follow> followings = userOptional.get().getFollowings();
            List<FollowingUserDto> result = new ArrayList<>();
            for(Follow follow : followings) {
                if(follow.getFollowing().isPublic()) {
                    result.add(new FollowingUserDto(follow));
                }
            }
            return result;
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
