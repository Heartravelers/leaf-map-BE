package leafmap.server.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import leafmap.server.domain.user.dto.FollowingUserDto;
import leafmap.server.domain.user.dto.MyPageResponseDto;
import leafmap.server.domain.user.dto.ProfileRequestDto;
import leafmap.server.domain.user.dto.ScrapResponseDto;
import leafmap.server.domain.user.service.MyPageService;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.SuccessCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "MyPage", description = "마이페이지 관련 API")
public class MyPageController {

    @Autowired
    MyPageService myPageService;

    @Operation(summary = "마이페이지 조회")
    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<?>> getMyPage(@RequestHeader("Authorization") String authorization){
        try {
            Long userId = Long.parseLong(authorization);  // 테스트용
            MyPageResponseDto myPageResponseDto = myPageService.getMyPage(userId);
            if(myPageResponseDto != null) {
                    return ResponseEntity.ok(ApiResponse.onSuccess(myPageResponseDto));
            }
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
        } return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
    }

    @Operation(summary = "프로필 수정")
    @PatchMapping("/mypage/edit")
    public ResponseEntity<ApiResponse<?>> updateProfile(@RequestPart(value = "data", required = false) ProfileRequestDto profileRequestDto,
                                                        @RequestPart(value = "file", required = false) MultipartFile file,
                                                        @RequestHeader("Authorization") String authorization){
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            myPageService.patchUpdate(userId, profileRequestDto, file);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "프로필 이미지 삭제")
    @DeleteMapping("/mypage/edit/image")
    public ResponseEntity<ApiResponse<?>> deleteProfileImage(@RequestHeader("Authorization") String authorization) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            myPageService.deleteProfileImage(userId);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "구독한 사용자 조회")
    @GetMapping("/mypage/subscribe")
    public ResponseEntity<ApiResponse<?>> getSubscribe(@RequestHeader("Authorization") String authorization) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            List<FollowingUserDto> followings = myPageService.getFollowing(userId);
            return ResponseEntity.ok(ApiResponse.onSuccess(followings));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "스크랩 조회")
    @GetMapping("/mypage/scraps")
    public ResponseEntity<ApiResponse<?>> getScraps(@RequestHeader("Authorization") String authorization) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            List<ScrapResponseDto> scraps = myPageService.getScraps(userId);
            return ResponseEntity.ok(ApiResponse.onSuccess(scraps));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }
}
