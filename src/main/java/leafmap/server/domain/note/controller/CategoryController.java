package leafmap.server.domain.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import leafmap.server.domain.note.dto.CategoryDto;
import leafmap.server.domain.note.service.CategoryServiceImpl;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.SuccessCode;
import leafmap.server.global.common.exception.CustomException;
import leafmap.server.global.oauth2.user.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {
    CategoryServiceImpl categoryService;

    public CategoryController(CategoryServiceImpl categoryService){
        this.categoryService = categoryService;
    }

    @Operation(summary = "폴더 목록 조회")
    @GetMapping("/folder/{userId}")
    public ResponseEntity<ApiResponse<?>> getNote(@PathVariable Long userId){
        try{
            if (){ //본인 폴더 목록
                List<CategoryDto> categories = categoryService.getCategory(userId);
                return ResponseEntity.ok(ApiResponse.onSuccess(categories));
            }
            else{ //다른 사용자 폴더 목록
                List<CategoryDto> categories = categoryService.getCategory(userId);
                return ResponseEntity.ok(ApiResponse.onSuccess(categories));
            }
        }
        catch(CustomException.NotFoundUserException e){    //유저 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "카테고리(폴더) 생성")
    @PostMapping("/folder")
    public ResponseEntity<ApiResponse<?>> makeCategory(@CurrentUser  @Valid @RequestBody CategoryDto categoryDto){
        try {
            //token 에서 userId 추출
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            categoryService.makeCategory(userId, categoryDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.CREATED));
        }
        catch(CustomException.NotFoundUserException e){
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "카테고리(폴더) 수정")
    @PutMapping("/folder/{folderId}")
    public ResponseEntity<ApiResponse<?>> updateNote(@PathVariable Long folderId,
                                                     @Valid @RequestBody CategoryDto categoryDto){
        try {
            if () { //본인이 아닌 경우 getToken != note userId
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCode.FORBIDDEN.getErrorResponse());
            }
            categoryService.updateCategory(folderId, categoryDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch(CustomException.NotFoundCategoryException e){   //folder 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }    }

    @Operation(summary = "폴더 삭제")
    @DeleteMapping("/folder/{folderId}")
    public ResponseEntity<ApiResponse<?>> deleteNote(@PathVariable Long folderId) {
        try {
            if () { //본인이 아닌 경우 getToken != category folderId
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCode.FORBIDDEN.getErrorResponse());
            }
            categoryService.deleteCategory(folderId);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch (CustomException.NotFoundCategoryException e) {   //category 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (CustomException.NotFoundChallengeException e) {   //challenge 존재하지 않고 category 는 삭제됨
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "폴더 내 지역 필터링")
    @GetMapping("/folder/{userId}")
    public ResponseEntity<ApiResponse<?>> getNote(@PathVariable Long userId){
        try{
            if (){ //본인 폴더 목록
                List<CategoryDto> categories = categoryService.getCategory(userId);
                return ResponseEntity.ok(ApiResponse.onSuccess(categories));
            }
            else{ //다른 사용자 폴더 목록
                List<CategoryDto> categories = categoryService.getCategory(userId);
                return ResponseEntity.ok(ApiResponse.onSuccess(categories));
            }
        }
        catch(CustomException.NotFoundUserException e){    //유저 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }
}
