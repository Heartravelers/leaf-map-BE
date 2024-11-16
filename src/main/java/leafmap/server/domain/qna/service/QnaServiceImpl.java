package leafmap.server.domain.qna.service;

import leafmap.server.domain.qna.dto.InquiryRequestDto;
import leafmap.server.domain.qna.dto.InquiryResponseDto;
import leafmap.server.domain.qna.entity.Inquiry;
import leafmap.server.domain.qna.repository.InquiryRepository;
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

    @Autowired
    InquiryRepository inquiryRepository;

    @Override
    public void save(InquiryRequestDto inquiryRequestDto, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            inquiryRepository.save(new Inquiry(inquiryRequestDto, userOptional.get()));
            return;
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    @Override
    public void update(Inquiry inquiry, InquiryRequestDto inquiryRequestDto) {
        if(inquiryRequestDto.getInquiryTitle() != null)
            inquiry.setInquiryTitle(inquiryRequestDto.getInquiryTitle());
        if(inquiryRequestDto.getInquiryText() != null)
            inquiry.setInquiryText(inquiryRequestDto.getInquiryText());
        if(inquiryRequestDto.getEmail() != null)
            inquiry.setEmail(inquiryRequestDto.getEmail());

        inquiryRepository.save(inquiry);
    }

    @Override
    public Inquiry findByInquiryId(Long inquiryId) {
        Optional<Inquiry> inquiryOptional = inquiryRepository.findById(inquiryId);
        if(inquiryOptional.isPresent()) {
            return inquiryOptional.get();
        }
        throw new CustomException(ErrorCode.NOT_FOUND);
    }

    @Override
    public List<InquiryResponseDto> findByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            return userOptional.get().getInquiries().stream().map(InquiryResponseDto::new).toList();
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    @Override
    public void validateUser(Long userId, Inquiry inquiry) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            if(inquiry.getUser().equals(userOptional.get())) {
                return;
            }
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}
