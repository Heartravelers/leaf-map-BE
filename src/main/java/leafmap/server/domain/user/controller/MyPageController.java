package leafmap.server.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import leafmap.server.domain.user.dto.MyPageResponseDto;
import leafmap.server.domain.user.service.MyPageService;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.USER_NOT_FOUND.getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
    }

}
