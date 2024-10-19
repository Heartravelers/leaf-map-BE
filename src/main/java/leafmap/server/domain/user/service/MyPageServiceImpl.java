package leafmap.server.domain.user.service;

import leafmap.server.domain.user.dto.MyPageResponseDto;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyPageServiceImpl implements MyPageService {

    @Autowired
    UserRepository userRepository;

    @Override
    public MyPageResponseDto getMyPage(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if(userOptional.isPresent()) {
                return new MyPageResponseDto(userOptional.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
