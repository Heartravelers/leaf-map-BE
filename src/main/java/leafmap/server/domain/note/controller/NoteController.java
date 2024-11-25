package leafmap.server.domain.note.controller;

import io.swagger.v3.oas.annotations.Operation;
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
public class NoteController {
    private NoteServiceImpl noteService;

    @Autowired
    private NoteController(NoteServiceImpl noteService){
        this.noteService = noteService;
    }

    @Operation(summary = "노트 상세 조회")
    @GetMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> getNote(@RequestHeader String token,
                                                  @PathVariable Long noteId){
        try{
            if (){ //본인 글
                NoteDto noteDto = noteService.getMyNote(noteId);
                return ResponseEntity.ok(ApiResponse.onSuccess(noteDto));
            }
            else{ //다른 사용자 글
                NoteDto noteDto = noteService.getUserNote(noteId);
                return ResponseEntity.ok(ApiResponse.onSuccess(noteDto));
            }
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
    public ResponseEntity<ApiResponse<?>> postNote(@RequestHeader String token,
                                                   @Valid @RequestBody NoteDto noteDto){
        try {
            //token 에서 userId 추출
            noteService.postNote(userId, noteDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.CREATED));
        }
        catch(CustomException.NotFoundUserException e){
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }    }

    @Operation(summary = "노트 수정")
    @PutMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> updateNote(@RequestHeader String token,
                                                     @PathVariable Long noteId,
                                                     @Valid @RequestBody NoteDto noteDto){
        try {
            if () { //본인이 아닌 경우 getToken != note userId
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCode.FORBIDDEN.getErrorResponse());
            }
            noteService.updateNote(noteId, noteDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch(CustomException.NotFoundNoteException e){   //note 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }    }

    @Operation(summary = "노트 삭제")
    @DeleteMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> deleteNote(@RequestHeader String token,
                                                     @PathVariable Long noteId) {
        try {
            if () { //본인이 아닌 경우 getToken != note userId
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCode.FORBIDDEN.getErrorResponse());
            }
            noteService.deleteNote(noteId);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch (CustomException.NotFoundNoteException e) {   //note 존재하지 않음
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
            if (){ //본인 노트 목록
                List<NoteDto> notes = noteService.getList(userId, category);
                return ResponseEntity.ok(ApiResponse.onSuccess(notes));
            }
            else{ //다른 사용자 노트 목록
                List<NoteDto> notes = noteService.getList(userId, category);
                return ResponseEntity.ok(ApiResponse.onSuccess(notes));
            }
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
