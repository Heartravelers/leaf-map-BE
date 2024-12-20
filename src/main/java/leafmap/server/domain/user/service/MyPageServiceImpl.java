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

            String url = null;
            if(file != null && !file.isEmpty()){
                url = s3Provider.uploadFile(file, new S3UploadRequest(userId, DIR_NAME));
                if(user.getProfilePicture() != null) {
                    s3Provider.removeFile(user.getProfilePicture(), DIR_NAME);
                }
            }

            user.update(profileRequestDto, url);
            userRepository.save(user);

            /*
            String username = user.getUsername();
            String bio = user.getBio();
            boolean isPublic = user.isPublic();
            String profilePicture = user.getProfilePicture();

            if(profileRequestDto != null) {
                if(profileRequestDto.getUsername() != null)
                    username = profileRequestDto.getUsername();
                if(profileRequestDto.getBio() != null)
                    bio = profileRequestDto.getBio();
                if(profileRequestDto.getIsPublic() != null)
                    isPublic = profileRequestDto.getIsPublic();
            }

            if(file != null && !file.isEmpty()){
                profilePicture = s3Provider.uploadFile(file, new S3UploadRequest(userId, DIR_NAME));
                if(user.getProfilePicture() != null) {
                    s3Provider.removeFile(user.getProfilePicture(), DIR_NAME);
                }
            }

            if(profileRequestDto != null) {
                if(profileRequestDto.getUsername() != null)
                    user.setUsername(profileRequestDto.getUsername());
                if(profileRequestDto.getBio() != null)
                    user.setBio(profileRequestDto.getBio());
                if(profileRequestDto.getIsPublic() != null)
                    user.setPublic(profileRequestDto.getIsPublic());
            }
            if(file != null && !file.isEmpty()){
                String url = s3Provider.uploadFile(file, new S3UploadRequest(userId, DIR_NAME));
                if(user.getProfilePicture() != null) {
                    s3Provider.removeFile(user.getProfilePicture(), DIR_NAME);
                }
                user.setProfilePicture(url);
            }

            userRepository.save(User.builder()
                    .id(user.getId())
                    .username(username)
                    .email(user.getEmail())
                    .provider(user.getProvider())
                    .providerId(user.getProviderId())
                    .refreshToken(user.getRefreshToken())
                    .role(user.getRole())
                    .profilePicture(profilePicture)
                    .bio(bio)
                    .isPublic(isPublic)
                    .notes(user.getNotes())
                    .scraps(user.getScraps())
                    .inquiries(user.getInquiries())
                    .challenge(user.getChallenge())
                    .regionFilters(user.getRegionFilters())
                    .folders(user.getFolders())
                    .followings(user.getFollowings())
                    .build());
            */
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
                s3Provider.removeFile(user.getProfilePicture(), DIR_NAME);
                //user.setProfilePicture(null);
                //userRepository.save(user);
                userRepository.save(userRepository.save(User.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .provider(user.getProvider())
                        .providerId(user.getProviderId())
                        .refreshToken(user.getRefreshToken())
                        .role(user.getRole())
                        .profilePicture(null)
                        .bio(user.getBio())
                        .isPublic(user.isPublic())
                        .notes(user.getNotes())
                        .scraps(user.getScraps())
                        .inquiries(user.getInquiries())
                        .challenge(user.getChallenge())
                        .regionFilters(user.getRegionFilters())
                        .folders(user.getFolders())
                        .followings(user.getFollowings())
                        .build()));
            }
            return;
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    @Override
    public List<FollowingUserDto> findAllFollowingsByUserId(Long userId) {
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
    public List<ScrapResponseDto> findAllScrapsByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            List<Scrap> scraps = scrapRepository.findAllByUser(userOptional.get());
            return scraps.stream().map(ScrapResponseDto::new).toList();
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}
