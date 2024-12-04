package leafmap.server.domain.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leafmap.server.domain.note.dto.CategoryDto;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.service.NoteServiceImpl;
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
@Tag(name = "NOTE", description = "NOTE 관련 API")
public class NoteController {
    private NoteServiceImpl noteService;

    @Autowired
    private NoteController(NoteServiceImpl noteService){
        this.noteService = noteService;
    }

    @Operation(summary = "노트 상세 조회")
    @GetMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> getNote(@RequestHeader("Authorization") String authorization,
                                                  @PathVariable Long noteId){
        try{
            Long userId = Long.parseLong(authorization); // 테스트용
            NoteDto noteDto = noteService.getNote(userId, noteId);
            return ResponseEntity.ok(ApiResponse.onSuccess(noteDto));
        }
        catch(CustomException.NotFoundUserException e){    //유저 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.NotFoundNoteException e){   //note 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.ForbiddenException e){    //노트가 공개인지
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }    }

    @Operation(summary = "노트 생성")
    @PostMapping("/note")
    public ResponseEntity<ApiResponse<?>> postNote(@RequestHeader("Authorization") String authorization,
                                                   @Valid @RequestBody NoteDto noteDto){
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            noteService.postNote(userId, noteDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.CREATED));
        }
        catch(CustomException.NotFoundUserException e){
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.BadRequestException e){
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }    }

    @Operation(summary = "노트 수정")
    @PutMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> updateNote(@RequestHeader("Authorization") String authorization,
                                                     @PathVariable Long noteId,
                                                     @Valid @RequestBody NoteDto noteDto){
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            noteService.updateNote(userId, noteId, noteDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch(CustomException.NotFoundNoteException e){   //note 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.ForbiddenException e){    //권한 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }    }

    @Operation(summary = "노트 삭제")
    @DeleteMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> deleteNote(@RequestHeader("Authorization") String authorization,
                                                     @PathVariable Long noteId) {
        try {
            Long userId = Long.parseLong(authorization); // 테스트용
            noteService.deleteNote(userId, noteId);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch (CustomException.NotFoundNoteException e) {   //note 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (CustomException.ForbiddenException e) {   //권한 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.BadRequestException e){
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }

    @Operation(summary = "폴더 내 노트목록 조회")
    @GetMapping("/notelist/{userId}/{category}")
    public ResponseEntity<ApiResponse<?>> getUserNoteList(@PathVariable Long userId,
                                                          @PathVariable String category) {
        try {
            List<NoteDto> notes = noteService.getList(userId, category);
            return ResponseEntity.ok(ApiResponse.onSuccess(notes));
        }
        catch(CustomException.NotFoundUserException e){    //유저 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (CustomException.NotFoundCategoryException e) {   //category 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }
}
