package leafmap.server.domain.qna.service;

import leafmap.server.domain.qna.dto.InquiryRequestDto;
import leafmap.server.domain.qna.dto.InquiryResponseDto;

import java.util.List;

public interface QnaService {

    void save(InquiryRequestDto inquiryRequestDto, Long userId);

    List<InquiryResponseDto> findByUserId(Long userId);

}
