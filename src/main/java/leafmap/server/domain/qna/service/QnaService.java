package leafmap.server.domain.qna.service;

import leafmap.server.domain.qna.dto.InquiryResponseDto;

import java.util.List;

public interface QnaService {

    List<InquiryResponseDto> getInquiries(Long userId);

}
