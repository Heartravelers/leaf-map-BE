package leafmap.server.domain.qna.service;

import leafmap.server.domain.qna.dto.InquiryRequestDto;
import leafmap.server.domain.qna.dto.InquiryResponseDto;
import leafmap.server.domain.qna.entity.Inquiry;

import java.util.List;

public interface QnaService {

    void save(InquiryRequestDto inquiryRequestDto, Long userId);

    void update(Inquiry inquiry, InquiryRequestDto inquiryRequestDto);

    void delete(Inquiry inquiry);

    Inquiry findByInquiryId(Long inquiryId);

    List<InquiryResponseDto> findByUserId(Long userId);

    void validateUser(Long userId, Inquiry inquiry);

}
