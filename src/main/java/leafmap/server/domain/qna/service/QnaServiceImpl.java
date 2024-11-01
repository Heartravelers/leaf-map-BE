package leafmap.server.domain.qna.service;

import leafmap.server.domain.qna.dto.InquiryResponseDto;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QnaServiceImpl implements QnaService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<InquiryResponseDto> getInquiries(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            return userOptional.get().getInquiries().stream().map(InquiryResponseDto::new).toList();
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}
