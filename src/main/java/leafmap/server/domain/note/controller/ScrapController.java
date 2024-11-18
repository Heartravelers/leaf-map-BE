package leafmap.server.domain.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import leafmap.server.domain.note.service.NoteServiceImpl;
import leafmap.server.domain.note.service.ScrapServiceImpl;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.SuccessCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ScrapController {
    private ScrapServiceImpl scrapService;

    @Autowired
    private ScrapController(NoteServiceImpl noteService){
        this.noteService = noteService;
    }

    @Operation(summary = "하트 추가 및 스크랩")
    @PostMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> makeScarp(@RequestHeader String token,
                                                    @PathVariable Long noteId){
        try{
            //token 에서 userId 추출
            scrapService.makeScrap(userId, noteId);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch(CustomException.NotFoundNoteException e){   //note 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.BadRequestException e){   //이미 스크랩한 note
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.NotFoundUserException e){   //user 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
    }

    @Operation(summary = "하트 삭제 및 스크랩 취소")
    @DeleteMapping({"/note/{noteId}", "/note/{noteId}/scrap"})
    public ResponseEntity<ApiResponse<?>> deleteScrap(@RequestHeader String token,
                                                      @PathVariable Long noteId){
        try{
            if () { //본인이 아닌 경우 getToken != note userId
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCode.FORBIDDEN.getErrorResponse());
            }
            scrapService.deleteScrap(userId, noteId);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch(CustomException.NotFoundNoteException e){   //note 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.BadRequestException e){   //이미 스크랩 취소된 note
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch(CustomException.NotFoundUserException e){   //user 존재하지 않음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
    }
}
