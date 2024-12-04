package leafmap.server.domain.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "SCRAP", description = "SCRAP 관련 API")
public class ScrapController {
    private ScrapServiceImpl scrapService;

    @Autowired
    private ScrapController(ScrapServiceImpl scrapService){
        this.scrapService = scrapService;
    }

    @Operation(summary = "하트 추가 및 스크랩")
    @PostMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> makeScarp(@RequestHeader("Authorization") String authorization,
                                                    @PathVariable Long noteId){
        try{
            Long userId = Long.parseLong(authorization);
            scrapService.makeScrap(userId, noteId);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.CREATED));
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
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }    }

    @Operation(summary = "하트 삭제 및 스크랩 취소")
    @DeleteMapping({"/note/{noteId}", "/note/{noteId}/scrap"})
    public ResponseEntity<ApiResponse<?>> deleteScrap(@RequestHeader("Authorization") String authorization,
                                                      @PathVariable Long noteId){
        try{
            Long userId = Long.parseLong(authorization);
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
        catch(CustomException.ForbiddenException e){   //권한 없음
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getErrorResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
        }
    }
}
