package leafmap.server.domain.challenge.service;

import leafmap.server.domain.challenge.dto.ChallengeResponseDto;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    @Autowired
    UserRepository userRepository;

    @Override
    public ChallengeResponseDto getChallenge(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            return new ChallengeResponseDto(user.getChallenge(), user.getCategoryFilters());
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}
