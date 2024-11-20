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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QnaServiceImpl implements QnaService {

    public static final LocalDateTime MIN_DATE_TIME = LocalDateTime.parse("2024-01-01T00:00:00");

    public static final LocalDateTime MAX_DATE_TIME = LocalDateTime.parse("3000-01-01T00:00:00");

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
    public void delete(Inquiry inquiry) {
        inquiryRepository.delete(inquiry);
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
    public List<InquiryResponseDto> findAllByUserId(Long userId, String startDate, String endDate) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            LocalDateTime start, end;
            if(startDate == null) {
                start = MIN_DATE_TIME;
            } else {
                start = LocalDateTime.parse(startDate + "T00:00:00");
            }
            if(endDate == null) {
                end = MAX_DATE_TIME;
            } else {
                end = LocalDateTime.parse(endDate + "T23:59:59");
            }

            List<Inquiry> inquiries = inquiryRepository.findByUserAndCreatedAtBetween(userOptional.get(), start, end);
            return inquiries.stream().map(InquiryResponseDto::new).toList();
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
