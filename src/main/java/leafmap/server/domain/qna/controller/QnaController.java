package leafmap.server.domain.qna.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import leafmap.server.domain.qna.dto.InquiryResponseDto;
import leafmap.server.domain.qna.service.QnaService;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Q&A", description = "Q&A 관련 API")
public class QnaController {

    @Autowired
    QnaService qnaService;

    @Operation(summary = "문의 내역 조회")
    @GetMapping("/inquiry")
    public ResponseEntity<ApiResponse<?>> getInquiries() {
        try {
            List<InquiryResponseDto> inquiries = qnaService.getInquiries(1L); // 테스트용
            return ResponseEntity.ok(ApiResponse.onSuccess(inquiries));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.USER_NOT_FOUND.getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

}
