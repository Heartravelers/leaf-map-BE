package leafmap.server.domain.qna.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import leafmap.server.domain.qna.dto.AnswerResponseDto;
import leafmap.server.domain.qna.dto.InquiryRequestDto;
import leafmap.server.domain.qna.dto.InquiryResponseDto;
import leafmap.server.domain.qna.entity.Inquiry;
import leafmap.server.domain.qna.service.QnaService;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.SuccessCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@Tag(name = "Q&A", description = "Q&A 관련 API")
public class QnaController {

    @Autowired
    QnaService qnaService;

    @Operation(summary = "문의하기")
    @PostMapping("/inquiry")
    public ResponseEntity<ApiResponse<?>> createInquiry(@RequestBody InquiryRequestDto inquiryRequestDto,
                                                        @RequestHeader("Authorization") String authorization){
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            qnaService.save(inquiryRequestDto, userId);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
    }

    @Operation(summary = "문의 내역 조회")
    @GetMapping("/inquiry")
    public ResponseEntity<ApiResponse<?>> getInquiries(@RequestParam(required = false) String startDate,
                                                       @RequestParam(required = false) String endDate,
                                                       @RequestHeader("Authorization") String authorization) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            List<InquiryResponseDto> inquiries = qnaService.findAllByUserId(userId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.onSuccess(inquiries));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        } catch(DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCode.BAD_REQUEST.getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "문의 수정")
    @PatchMapping("/inquiry/{inquiryId}")
    public ResponseEntity<ApiResponse<?>> updateInquiry(@PathVariable("inquiryId") Long inquiryId,
                                                        @RequestBody InquiryRequestDto inquiryRequestDto,
                                                        @RequestHeader("Authorization") String authorization) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            Inquiry inquiry = qnaService.findByInquiryId(inquiryId);
            qnaService.validateUser(userId, inquiry);
            qnaService.update(inquiry, inquiryRequestDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse()); // 403 404
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "문의 삭제")
    @DeleteMapping("/inquiry/{inquiryId}")
    public ResponseEntity<ApiResponse<?>> deleteInquiry(@PathVariable("inquiryId") Long inquiryId,
                                                        @RequestHeader("Authorization") String authorization) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            Inquiry inquiry = qnaService.findByInquiryId(inquiryId);
            qnaService.validateUser(userId, inquiry);
            qnaService.delete(inquiry);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse()); // 403 404
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "답변 조회")
    @GetMapping("/inquiry/{inquiryId}")
    public ResponseEntity<ApiResponse<?>> getAnswers(@PathVariable("inquiryId") Long inquiryId,
                                                     @RequestHeader("Authorization") String authorization) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            Inquiry inquiry = qnaService.findByInquiryId(inquiryId);
            qnaService.validateUser(userId, inquiry);
            List<AnswerResponseDto> answers = inquiry.getAnswers().stream().map(AnswerResponseDto::new).toList();
            return ResponseEntity.ok(ApiResponse.onSuccess(answers));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
    }

}
