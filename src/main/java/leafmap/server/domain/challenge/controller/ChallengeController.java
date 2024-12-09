package leafmap.server.domain.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import leafmap.server.domain.challenge.dto.ChallengeResponseDto;
import leafmap.server.domain.challenge.service.ChallengeService;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Challenge", description = "챌린지(스탬프) 관련 API")
public class ChallengeController {

    @Autowired
    ChallengeService challengeService;

    @Operation(summary = "챌린지(스탬프) 조회")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - 존재하지 않는 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/challenge")
    public ResponseEntity<ApiResponse<ChallengeResponseDto>> getChallenge(@RequestHeader("Authorization") String authorization) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            ChallengeResponseDto challengeResponseDto = challengeService.getChallenge(userId);
            return ResponseEntity.ok(ApiResponse.onSuccess(challengeResponseDto));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiResponse.onFailure(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
        }
    }

}
