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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "MyPage", description = "마이페이지 관련 API")
public class MyPageController {

    @Autowired
    MyPageService myPageService;

    @Operation(summary = "마이페이지 조회")
    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<?>> getMyPage(){
        try {
            MyPageResponseDto myPageResponseDto = myPageService.getMyPage(1L); // 테스트용
            if(myPageResponseDto != null) {
                    return ResponseEntity.ok(ApiResponse.onSuccess(myPageResponseDto));
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.USER_NOT_FOUND.getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
        } return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
    }

    @Operation(summary = "프로필 수정")
    @PatchMapping("/mypage/edit")
    public ResponseEntity<ApiResponse<?>> updateProfile(@RequestBody ProfileRequestDto profileRequestDto){
        try {
            myPageService.patchUpdate(1L, profileRequestDto); // 테스트용
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.USER_NOT_FOUND.getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "구독한 사용자 조회")
    @GetMapping("/mypage/subscribe")
    public ResponseEntity<ApiResponse<?>> getSubscribe() {
        try {
            List<FollowingUserDto> followings = myPageService.getFollowing(1L); // 테스트용
            return ResponseEntity.ok(ApiResponse.onSuccess(followings));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.USER_NOT_FOUND.getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "스크랩 조회")
    @GetMapping("/mypage/scraps")
    public ResponseEntity<ApiResponse<?>> getScraps() {
        try {
            List<ScrapResponseDto> scraps = myPageService.getScraps(1L); // 테스트용
            return ResponseEntity.ok(ApiResponse.onSuccess(scraps));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.USER_NOT_FOUND.getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }
}
