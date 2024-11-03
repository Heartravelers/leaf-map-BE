package leafmap.server.domain.qna.service;

import leafmap.server.domain.qna.dto.InquiryRequestDto;
import leafmap.server.domain.qna.dto.InquiryResponseDto;
import leafmap.server.domain.qna.entity.Inquiry;

import java.util.List;
import java.util.Optional;

public interface QnaService {

    void save(InquiryRequestDto inquiryRequestDto, Long userId);

    Inquiry findByInquiryId(Long inquiryId);

    List<InquiryResponseDto> findByUserId(Long userId);

    void validateUser(Long userId, Inquiry inquiry);

}
