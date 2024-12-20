package leafmap.server.domain.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import leafmap.server.domain.note.dto.FolderDto;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.service.FolderServiceImpl;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.SuccessCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Folder", description = "Folder 관련 API")
public class FolderController {
    private FolderServiceImpl folderService;

    @Autowired
    private FolderController(FolderServiceImpl folderService){
        this.folderService = folderService;
    }

    @Operation(summary = "폴더 목록 조회")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - 존재하지 않는 사용자입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/folder/{userId}")
    public ResponseEntity<ApiResponse<?>> getFolder(@PathVariable("userId") Long userId){
        try{
            List<FolderDto> folders = folderService.getFolder(userId); //**폴더 목록 비공개 있다면 고쳐야 함(폴더 내 노트목록 api 참고) - serviceImpl 도
            return ResponseEntity.ok(ApiResponse.onSuccess(folders));
        }
        catch(CustomException.NotFoundUserException e){    //유저 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "폴더 생성")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - 잘못된 요청입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - 존재하지 않는 사용자입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/folder")
    public ResponseEntity<ApiResponse<?>> makeFolder(@RequestHeader("Authorization") String authorization,
                                                       @RequestBody FolderDto folderDto){
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            folderService.makeFolder(userId, folderDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.CREATED));
        }
        catch(CustomException.NotFoundUserException e){ //유저 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.BadRequestException e){ //챌린지가 이미 존재(rollback)
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "폴더 수정")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - 금지된 요청입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - 요청받은 리소스를 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PatchMapping("/folder/{folderId}")
    public ResponseEntity<ApiResponse<?>> updateFolder(@RequestHeader("Authorization") String authorization,
                                                     @PathVariable("folderId") Long folderId,
                                                     @RequestBody FolderDto folderDto){
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            folderService.updateFolder(userId, folderId, folderDto);
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - 금지된 요청입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - 요청받은 리소스를 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @DeleteMapping("/folder/{folderId}")
    public ResponseEntity<ApiResponse<?>> deleteFolder(@RequestHeader("Authorization") String authorization,
                                                     @PathVariable("folderId") Long folderId) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            folderService.deleteFolder(userId, folderId);
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - 요청받은 리소스를 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/folder/{userId}/{regionName}")
    public ResponseEntity<ApiResponse<?>> regionFiltering(@PathVariable("userId") Long userId,
                                                  @PathVariable("regionName") String regionName){
        try{
            List<NoteDto> notes = folderService.filterNotes(userId, regionName);
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
