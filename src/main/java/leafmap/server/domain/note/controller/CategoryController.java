package leafmap.server.domain.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leafmap.server.domain.note.dto.CategoryDto;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.service.CategoryServiceImpl;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.SuccessCode;
import leafmap.server.global.common.exception.CustomException;
import leafmap.server.global.oauth2.user.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Folder", description = "Folder 관련 API")
public class CategoryController {
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryController(CategoryServiceImpl categoryService){
        this.categoryService = categoryService;
    }

    @Operation(summary = "폴더 목록 조회")
    @GetMapping("/folder/{userId}")
    public ResponseEntity<ApiResponse<?>> getNote(@PathVariable("userId") Long userId){
        try{
            List<CategoryDto> categories = categoryService.getCategory(userId); //**폴더 목록 비공개 있다면 고쳐야 함(폴더 내 노트목록 api 참고) - serviceImpl 도
            return ResponseEntity.ok(ApiResponse.onSuccess(categories));
        }
        catch(CustomException.NotFoundUserException e){    //유저 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "폴더 생성")
    @PostMapping("/folder")
    public ResponseEntity<ApiResponse<?>> makeCategory(@RequestHeader("Authorization") String authorization,
                                                       @RequestBody CategoryDto categoryDto){
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            categoryService.makeCategory(userId, categoryDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.CREATED));
        }
        catch(CustomException.NotFoundUserException e){ //유저 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "폴더 수정")
    @PatchMapping("/folder/{folderId}")
    public ResponseEntity<ApiResponse<?>> updateNote(@RequestHeader("Authorization") String authorization,
                                                     @PathVariable("folderId") Long folderId,
                                                     @RequestBody CategoryDto categoryDto){
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            categoryService.updateCategory(userId, folderId, categoryDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch(CustomException.NotFoundCategoryException e){   //folder 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.ForbiddenException e){   //권한 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }    }

    @Operation(summary = "폴더 삭제")
    @DeleteMapping("/folder/{folderId}")
    public ResponseEntity<ApiResponse<?>> deleteNote(@RequestHeader("Authorization") String authorization,
                                                     @PathVariable("folderId") Long folderId) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            categoryService.deleteCategory(userId, folderId);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch (CustomException.NotFoundCategoryException e) {   //category 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (CustomException.NotFoundChallengeException e) {   //challenge 존재하지 않고 category 는 삭제됨
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (CustomException.ForbiddenException e) {   //권한 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "폴더 내 지역 필터링")
    @GetMapping("/folder/{userId}/{regionName}")
    public ResponseEntity<ApiResponse<?>> getNote(@PathVariable("userId") Long userId,
                                                  @PathVariable("regionName") String regionName){
        try{
            List<NoteDto> notes = categoryService.filterNotes(userId, regionName);
            return ResponseEntity.ok(ApiResponse.onSuccess(notes));
        }
        catch(CustomException.NotFoundUserException e){    //유저 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }
}
